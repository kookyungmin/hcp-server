package net.happykoo.hcp.application.port.out.data;

public enum IdempotencyAcquireResult {
  ACQUIRED, //insert 완료 (선점)
  ALREADY_DONE, //insert 실패(키중복) -> 이미 완료된 상태
  BUSY; //insert 실패(키중복) -> 다른 컨슈머가 처리 중
}
