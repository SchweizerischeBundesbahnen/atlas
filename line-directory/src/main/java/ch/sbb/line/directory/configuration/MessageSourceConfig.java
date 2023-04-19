package ch.sbb.line.directory.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

  @Bean
  public MessageSource timetableHearingStatementCsvTranslations() {
    ResourceBundleMessageSource resourceBundle = new ResourceBundleMessageSource();
    resourceBundle.setBasename("hearing/translations/csvHeaders");
    return resourceBundle;
  }
}
