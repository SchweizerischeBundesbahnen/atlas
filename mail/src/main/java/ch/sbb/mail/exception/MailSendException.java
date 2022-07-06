package ch.sbb.mail.exception;

public class MailSendException extends RuntimeException {

  public MailSendException(String message) {
	super(message);
  }

  public MailSendException(String message, Throwable cause) {
	super(message, cause);
  }
}
