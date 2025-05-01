package ch.sbb.workflow.sepodi;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Setter
public abstract class BaseExaminants {

  protected static final String PROD_PROFILE = "prod";

  @Value("${spring.profiles.active:local}")
  protected String activeProfile;

}
