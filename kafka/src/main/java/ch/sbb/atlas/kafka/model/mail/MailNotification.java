package ch.sbb.atlas.kafka.model.mail;

import ch.sbb.atlas.kafka.model.workflow.event.AtlasEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class MailNotification implements Serializable, AtlasEvent {

  @Serial
  private static final long serialVersionUID = 1;

  private String from;

  private List<@Email @NotEmpty String> to;

  private List<@Email @NotEmpty String> cc;

  private List<@Email @NotEmpty String> bcc;

  private String subject;

  private String content;

  private List<Map<String, Object>> templateProperties;

  private MailType mailType;

  public String[] toAsArray() {
    if (to == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return to.toArray(String[]::new);
  }

  public String[] ccAsArray() {
    if (cc == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return cc.toArray(String[]::new);
  }

  public String[] bccAsArray() {
    if (bcc == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return bcc.toArray(String[]::new);
  }

}
