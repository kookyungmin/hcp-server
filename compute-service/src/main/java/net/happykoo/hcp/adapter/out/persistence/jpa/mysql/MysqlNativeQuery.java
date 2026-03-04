package net.happykoo.hcp.adapter.out.persistence.jpa.mysql;

public class MysqlNativeQuery {

  public static final String UPDATE_PROCESSING_STATUS_QUERY = """
          UPDATE h_outbox_event
          SET status = :newStatus, claim_token = :claimToken
          WHERE status = :oldStatus
          ORDER BY created_at ASC
          LIMIT :batchSize
      """;

  private MysqlNativeQuery() {
  }

}
