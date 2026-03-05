FROM ubuntu:22.04

ENV PATH="/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
SHELL ["/bin/bash", "-lc"]
ENV DEBIAN_FRONTEND=noninteractive

ARG USERNAME=hcp_dev

# 배너(escape 지옥 피해서 COPY 유지)
COPY banner.txt /etc/issue.net

# 1) df wrapper (원본 그대로)
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

# 2) SSH + sudo (키로그인 only) + banner 설정 파일
RUN set -euo pipefail; \
  apt-get update; \
  apt-get install -y --no-install-recommends openssh-server sudo ca-certificates; \
  rm -rf /var/lib/apt/lists/*; \
  \
  # sshd 런타임 디렉토리 (Ubuntu는 /run/sshd를 요구하는 경우가 많음)
  mkdir -p /run/sshd /var/run/sshd; \
  \
  # 유저 + sudo
  useradd -m -s /bin/bash "${USERNAME}"; \
  usermod -aG sudo "${USERNAME}"; \
  echo "${USERNAME} ALL=(ALL) NOPASSWD:ALL" > "/etc/sudoers.d/${USERNAME}"; \
  chmod 440 "/etc/sudoers.d/${USERNAME}"; \
  \
  mkdir -p "/home/${USERNAME}/.ssh"; \
  chmod 700 "/home/${USERNAME}/.ssh"; \
  chown -R "${USERNAME}:${USERNAME}" "/home/${USERNAME}/.ssh"; \
  \
  # SSH 설정: root 로그인 금지 + 비번 로그인 금지(키만)
  sed -i 's/^[#[:space:]]*PermitRootLogin.*/PermitRootLogin no/' /etc/ssh/sshd_config; \
  sed -i 's/^[#[:space:]]*PasswordAuthentication.*/PasswordAuthentication no/' /etc/ssh/sshd_config; \
  (grep -q '^[#[:space:]]*KbdInteractiveAuthentication' /etc/ssh/sshd_config && \
    sed -i 's/^[#[:space:]]*KbdInteractiveAuthentication.*/KbdInteractiveAuthentication no/' /etc/ssh/sshd_config) || true; \
  (grep -q '^[#[:space:]]*ChallengeResponseAuthentication' /etc/ssh/sshd_config && \
    sed -i 's/^[#[:space:]]*ChallengeResponseAuthentication.*/ChallengeResponseAuthentication no/' /etc/ssh/sshd_config) || true; \
  \
  # Ubuntu 22.04에서는 sshd_config.d 로 넣는 게 제일 확실
  mkdir -p /etc/ssh/sshd_config.d; \
  printf 'Banner /etc/issue.net\n' > /etc/ssh/sshd_config.d/99-banner.conf; \
  printf 'AllowUsers %s\n' "${USERNAME}" > /etc/ssh/sshd_config.d/99-allowusers.conf

# entrypoint (런타임 host key 생성 + authorized_keys 권한 정리 + sshd 실행)
RUN cat > /usr/local/bin/entrypoint <<'EOF'
#!/usr/bin/env bash
set -euo pipefail

USERNAME="${USERNAME:-hcp_dev}"
HOME_DIR="/home/${USERNAME}"
AK="${HOME_DIR}/.ssh/authorized_keys"

mkdir -p /run/sshd /var/run/sshd

# 컨테이너마다 host key 다르게 (없으면 생성)
if ! ls /etc/ssh/ssh_host_*_key >/dev/null 2>&1; then
  ssh-keygen -A
fi

# authorized_keys 권한 정리 (마운트한 경우)
if [[ -f "$AK" ]]; then
  chown -R "${USERNAME}:${USERNAME}" "${HOME_DIR}/.ssh" || true
  chmod 700 "${HOME_DIR}/.ssh" || true
  chmod 600 "$AK" || true
fi

exec /usr/sbin/sshd -D -e
EOF
RUN chmod +x /usr/local/bin/entrypoint

CMD ["/usr/local/bin/entrypoint"]
