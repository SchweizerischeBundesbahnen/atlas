package ch.sbb.atlas.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class AtlasCsvMapperTest {

  @Mock
  private MessageSource messageSource;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(messageSource.getMessage(anyString(), eq(null), anyString(), any(Locale.class)))
        .thenAnswer(i -> i.getArgument(0, String.class) + i.getArgument(3, Locale.class).getLanguage());
  }

  @Test
  void shouldMapToCsvCorrectly() throws JsonProcessingException {
    // Given
    AtlasCsvMapper csvMapper = new AtlasCsvMapper(DummyCsvModel.class);

    String expectedCsv = """
        dateValue;value
        "2020-12-31";stringValue
        """;
    DummyCsvModel model = new DummyCsvModel("stringValue", LocalDate.of(2020, 12, 31));

    // When
    String result = csvMapper.getObjectWriter().writeValueAsString(model);

    //Then
    assertThat(result).isEqualTo(expectedCsv);
  }

  @Test
  void shouldMapToCsvWithLocalizationCorrectly() throws JsonProcessingException {
    // Given
    AtlasCsvMapper csvMapper = new AtlasCsvMapper(DummyCsvModel.class, new LocalizedPropertyNamingStrategy(messageSource,
        new Locale("de")));

    String expectedCsv = """
        dateValuede;valuede
        "2020-12-31";stringValue
        """;
    DummyCsvModel model = new DummyCsvModel("stringValue", LocalDate.of(2020, 12, 31));

    // When
    String result = csvMapper.getObjectWriter().writeValueAsString(model);

    //Then
    assertThat(result).isEqualTo(expectedCsv);
  }
}