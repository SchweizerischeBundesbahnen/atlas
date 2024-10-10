package ch.sbb.atlas.versioning.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor
@Getter
public class DateOrderException extends AtlasException {

    private final String message;
    private final LocalDate validFrom;
    private final LocalDate validTo;



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
        String dateRange = "Valid From: " + validFrom + ", Valid To: " + validTo;

        return List.of(Detail.builder()
                .message(message)
                .displayInfo(builder()
                        .code("VALIDATION.DATE_RANGE_ERROR")
                        .with("date", dateRange)
                        .build())
                .build());
    }
}
