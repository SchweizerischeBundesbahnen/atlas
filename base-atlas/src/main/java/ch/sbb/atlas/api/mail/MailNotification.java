package ch.sbb.atlas.api.mail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class MailNotification {

  @Email
  private String from;

  @NotEmpty
  private List<@Email @NotBlank String> to;

  private List<@Email @NotBlank String> cc;

  private List<@Email @NotBlank String> bcc;

  private String subject;

  private String content;

  private List<Map<String, Object>> templateProperties;

  @NotNull
  private MailType mailType;

}
