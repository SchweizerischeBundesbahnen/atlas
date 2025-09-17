package ch.sbb.atlas.export;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.io.Serial;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public class LocalizedPropertyNamingStrategy extends PropertyNamingStrategies.NamingBase {

  @Serial private static final long serialVersionUID = 1;

  private transient final MessageSource messageSource;
  private final Locale locale;

  @Override
  public String translate(String propertyName) {
    return messageSource.getMessage(propertyName, null, propertyName, locale);
  }
}