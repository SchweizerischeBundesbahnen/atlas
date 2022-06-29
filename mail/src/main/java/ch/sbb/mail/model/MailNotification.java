package ch.sbb.mail.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailNotification {

  private String from;

  private List<@Email @NotEmpty String> to;

  private List<@Email @NotEmpty String> cc;

  private List<@Email @NotEmpty String> bcc;

  private String subject;

  private String content;
  
  private List<Map<String, Object>> templateProperties;

  private MailType mailType;

  public String[] toAsArray() {
    if(to == null){
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return to.toArray(String[]::new);
  }

  public String[] ccAsArray() {
    if(cc == null){
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return cc.toArray(String[]::new);
  }

  public String[] bccAsArray() {
    if(bcc == null){
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return bcc.toArray(String[]::new);
  }

}
