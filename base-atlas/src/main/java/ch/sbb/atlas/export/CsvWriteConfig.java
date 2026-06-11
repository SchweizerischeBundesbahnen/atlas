package ch.sbb.atlas.export;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.Builder;
import org.springframework.context.MessageSource;

public record CsvWriteConfig<T>(Locale locale, MessageSource messageSource, Class<T> elementClass, List<T> csvData,
                                Path filePath) {

  @Builder
  public CsvWriteConfig(
      Locale locale,
      MessageSource messageSource,
      Class<T> elementClass,
      List<T> csvData,
      Path filePath) {
    this.locale = Objects.requireNonNull(locale, "locale must not be null");
    this.messageSource = Objects.requireNonNull(messageSource, "messageSource must not be null");
    this.elementClass = Objects.requireNonNull(elementClass, "elementClass must not be null");
    this.csvData = Objects.requireNonNull(csvData, "csvData must not be null");
    this.filePath = Objects.requireNonNull(filePath, "filePath must not be null");
  }

  public LocalizedPropertyNamingStrategy namingStrategy() {
    return new LocalizedPropertyNamingStrategy(messageSource, locale);
  }
}
