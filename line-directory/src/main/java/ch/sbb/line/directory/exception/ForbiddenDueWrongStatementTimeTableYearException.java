package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ForbiddenDueWrongStatementTimeTableYearException extends AtlasException {

    private final List<Long> statementIds;
    private final Long hearingYear;
    private final String settingField;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .message("Operation not allowed")
            .error("The TimeTableYear of the following Statement ID's: " + statementIds + " are not equal as the HearingYear: " + hearingYear + ".")
            .details(new TreeSet<>(getErrorDetails()))
            .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
            .message("The TimeTableYear of the following Statement ID's: " + statementIds + " are not equal as the HearingYear: " + hearingYear + ".")
            .field(settingField)
            .displayInfo(builder().code("TTH.NOTIFICATION.OPERATION_NOT_ALLOWED_DUE_TO_TTH_YEAR_SETTINGS").build())
            .build());
    }
}
