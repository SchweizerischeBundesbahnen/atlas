package ch.sbb.mail.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.rules.ExternalResource;

import javax.mail.internet.MimeMessage;

public class SmtpServerRule extends ExternalResource {

  private final int port;
  private GreenMail smtpServer;

  public SmtpServerRule(int port) {
	this.port = port;
  }

  @Override
  protected void before() throws Throwable {
	super.before();
	smtpServer = new GreenMail(new ServerSetup(port, null, "smtp"));
	smtpServer.setUser("username", "secret");
	smtpServer.start();
  }

  public MimeMessage[] getMessages() {
	return smtpServer.getReceivedMessages();
  }

  @Override
  protected void after() {
	super.after();
	smtpServer.stop();
  }
}