package ch.sbb.atlas.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class LocalizedPropertyNamingStrategyTest {

  @Mock
  private MessageSource messageSource;

  private LocalizedPropertyNamingStrategy localizedPropertyNamingStrategy;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(messageSource.getMessage(anyString(), eq(null), anyString(), any(Locale.class)))
        .thenAnswer(i -> i.getArgument(0, String.class) + i.getArgument(3, Locale.class).getLanguage());

    localizedPropertyNamingStrategy = new LocalizedPropertyNamingStrategy(messageSource, new Locale("de"));
  }

  @Test
  void shouldLocalizeFieldName() {
    String result = localizedPropertyNamingStrategy.nameForField(null, null, "fieldName");
    assertThat(result).isEqualTo("fieldNamede");
  }

  @Test
  void shouldLocalizeSetter() {
    String result = localizedPropertyNamingStrategy.nameForField(null, null, "setFieldName");
    assertThat(result).isEqualTo("setFieldNamede");
  }

  @Test
  void shouldLocalizeGetter() {
    String result = localizedPropertyNamingStrategy.nameForField(null, null, "getFieldName");
    assertThat(result).isEqualTo("getFieldNamede");
  }
}