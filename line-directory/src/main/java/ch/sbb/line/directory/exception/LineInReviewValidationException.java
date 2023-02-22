package ch.sbb.line.directory.exception;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.Line;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor
public class LineInReviewValidationException extends AtlasException {


    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message("Business rule validation failed")
                .error("May not update validFrom, validTo or type while status is " + Status.IN_REVIEW.name())
                .details(new TreeSet<>(getErrorDetails()))
                .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
                .message("Not allowed to update validFrom, validTo or lineType")
                .field(Line.Fields.lineType)
                .displayInfo(builder().code("LIDI.LINE.UPDATE_IN_REVIEW").build())
                .build());
    }

}
