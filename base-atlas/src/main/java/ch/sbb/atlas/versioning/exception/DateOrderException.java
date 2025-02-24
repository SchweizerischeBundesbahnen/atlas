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
                .message(this.getDetailMessage())
                .error(this.getDetailMessage())
                .details(new TreeSet<>(getErrorDetails()))
                .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
                .message(this.getDetailMessage())
                .displayInfo(builder()
                        .code("VALIDATION.DATE_ORDER_ERROR")
                        .with("validFrom", validFrom)
                        .with("validTo", validTo)
                        .build())
                .build());
    }

    private String getDetailMessage(){
        return message+ "; validFrom: " + validFrom + " validTo: " + validTo;
    }
}
