FROM ubuntu:22.04

ENV PATH="/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
SHELL ["/bin/bash", "-lc"]

RUN set -euo pipefail; \
  mkdir -p /usr/local/bin; \
  printf '%s\n' \
'#!/usr/bin/env bash' \
'set -euo pipefail' \
'' \
'REAL_DF="/usr/bin/df"' \
'if [[ "${DF_PASSTHROUGH:-0}" == "1" ]]; then exec "$REAL_DF" "$@"; fi' \
'' \
'TOTAL_DISK_BYTES="${TOTAL_DISK_BYTES:-53687091200}"' \
'DISP_DISK_TOTAL="$TOTAL_DISK_BYTES"' \
'' \
'mem_total_bytes="$(awk '\''/MemTotal:/ {print $2*1024; exit}'\'' /proc/meminfo 2>/dev/null || echo 0)"' \
'dev_bytes=$(( mem_total_bytes * 90 / 100 ))' \
'tmp_bytes=$(( mem_total_bytes * 95 / 100 ))' \
'runuser_bytes=$(( mem_total_bytes * 20 / 100 ))' \
'cgroup_bytes="$tmp_bytes"' \
'' \
'du_b() { du -sx -B1 "$1" 2>/dev/null | awk '\''{print $1}'\'' || echo 0; }' \
'' \
'used_data="$(du_b /data)"' \
'' \
'used_root_only="$(du -sx -B1 / --exclude=/proc --exclude=/sys --exclude=/dev --exclude=/run --exclude=/data 2>/dev/null | awk '\''{print $1}'\'' || echo 0)"' \
'used_root_total=$(( used_root_only + used_data ))' \
'' \
'used_run="$(du_b /run)"' \
'used_shm="$(du_b /dev/shm)"' \
'used_cgroup="$(du_b /sys/fs/cgroup)"' \
'' \
'ceil_div() { echo $(( ($1 + $2 - 1) / $2 )); }' \
'' \
'human() {' \
'  local b="$1"' \
'  local KB=1024' \
'  local MB=$((1024*1024))' \
'  local GB=$((1024*1024*1024))' \
'  if (( b >= GB )); then echo "$(ceil_div "$b" "$GB")G";' \
'  elif (( b >= MB )); then echo "$(ceil_div "$b" "$MB")M";' \
'  else echo "$(ceil_div "$b" "$KB")K"; fi' \
'}' \
'' \
'line() {' \
'  local fs="$1" size="$2" used="$3" mnt="$4"' \
'  local avail=$(( size - used )); (( avail < 0 )) && avail=0' \
'  local usep=0; (( size > 0 )) && usep=$(( used * 100 / size ))' \
'  printf "%-12s %6s %6s %6s %4s%% %s\n" "$fs" "$(human "$size")" "$(human "$used")" "$(human "$avail")" "$usep" "$mnt"' \
'}' \
'' \
'printf "%-12s %6s %6s %6s %5s %s\n" "Filesystem" "Size" "Used" "Avail" "Use%" "Mounted on"' \
'line "/dev/sda3" "$DISP_DISK_TOTAL" "$used_root_total" "/"' \
'line "/dev/sda3" "$DISP_DISK_TOTAL" "$used_data" "/data"' \
'line "devtmpfs" "$dev_bytes" "0" "/dev"' \
'line "tmpfs" "$tmp_bytes" "$used_shm" "/dev/shm"' \
'line "tmpfs" "$tmp_bytes" "$used_run" "/run"' \
'line "tmpfs" "$cgroup_bytes" "$used_cgroup" "/sys/fs/cgroup"' \
> /usr/local/bin/df; \
  chmod +x /usr/local/bin/df

CMD ["bash","-lc","sleep infinity"]
