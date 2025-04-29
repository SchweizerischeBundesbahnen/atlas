package ch.sbb.workflow.mail;

import ch.sbb.atlas.api.AtlasApiConstants;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseNotificationService {

  public static final String WORKFLOW_URL = "service-point-directory/workflows/";
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH);

  @Value("${spring.profiles.active:local}")
  protected String activeProfile;

  @Value("${mail.workflow.stop-point.from}")
  protected String from;

}
