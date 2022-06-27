package ch.sbb.mail.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailNotification {

  /**
   * Sender e-mail address
   */
  @Email
  private String from;

  /**
   * Recipient-s e-mail address
   */
  @NotEmpty
  private List<@Email @NotEmpty String> to;

  /**
   * E-mail subject
   */
  @NotEmpty
  private String subject;

  /**
   * E-mail Content
   */
  @NotEmpty
  private String content;

  public String[] toAsArray() {
    return to.toArray(String[]::new);
  }

}
