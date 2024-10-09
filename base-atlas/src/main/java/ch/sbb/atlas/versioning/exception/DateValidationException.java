package ch.sbb.atlas.versioning.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor
@Getter
public class DateValidationException extends AtlasException {

  private final String message;



  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(message)
            .error(message)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
            .message(message)
            .displayInfo(builder()
                    .code("VALIDATION.DATE_RANGE_ERROR")
                    .with("date", extractDatesFromMessage(message))
                    .build())
            .build());
  }

  private String extractDatesFromMessage(String message) {
    String regex = "\\b\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\b";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(message);
    List<String> dates = new ArrayList<>();
    while (matcher.find()) {
      dates.add(matcher.group());
    }
    if (dates.size() == 2) {
      return "Valid From: " + dates.get(0) + ", Valid To: " + dates.get(1);
    } else {
      return "Date: " + dates.get(0);
    }
  }
}
