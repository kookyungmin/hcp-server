package net.happykoo.hcp.exception;

public class RetryableException extends RuntimeException {

  public RetryableException(String message) {
    super(message);
  }

}
