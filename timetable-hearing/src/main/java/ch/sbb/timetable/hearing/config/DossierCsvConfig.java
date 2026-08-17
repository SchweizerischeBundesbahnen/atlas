package ch.sbb.timetable.hearing.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class DossierCsvConfig {

  @Bean
  public MessageSource dossierCsvTranslations() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("i18n/tth-dossier-csv");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
}
