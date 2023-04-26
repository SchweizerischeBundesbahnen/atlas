package ch.sbb.atlas.export;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public class LocalizedPropertyNamingStrategy extends PropertyNamingStrategy {

  private final MessageSource messageSource;
  private final Locale locale;

  @Override
  public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
    return localize(defaultName);
  }

  @Override
  public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    return localize(defaultName);
  }

  @Override
  public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    return localize(defaultName);
  }

  private String localize(String property) {
    return messageSource.getMessage(property, null, property, locale);
  }
}