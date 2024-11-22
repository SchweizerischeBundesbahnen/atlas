package ch.sbb.atlas.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Redacted
@Data
@Builder(toBuilder = true)
class RedactTarget {

  private String info;

  @RedactBySboid(application = ApplicationType.SEPODI)
  private String sboid;

  @Redacted(showFirstChar = true)
  private String mail;

  @Redacted
  private Set<NestedRedactTarget> examinants;

  @Redacted
  private NestedRedactTarget examinant;

  @Redacted
  private NestedRedactTarget nullObject;

  @Redacted(showFirstChar = true)
  private List<String> ccEmails;

}