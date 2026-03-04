package net.happykoo.hcp.application.port.out;

import java.util.List;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

public interface GetOutboxEventPort {

  List<OutboxEvent> claimPendingOutboxEvent();

}
