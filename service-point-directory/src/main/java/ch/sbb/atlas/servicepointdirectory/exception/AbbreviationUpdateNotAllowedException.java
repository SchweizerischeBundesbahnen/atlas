package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class AbbreviationUpdateNotAllowedException extends AtlasException {

    private static final String ABBREVIATION_UPDATE_NOT_ALLOWED = "The businessorganization of the service point is not permitted to set an abbreviation, or the service point already has an abbreviation and cannot be updated or deleted.";

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .message(ABBREVIATION_UPDATE_NOT_ALLOWED)
            .error(ABBREVIATION_UPDATE_NOT_ALLOWED)
            .details(new TreeSet<>(getErrorDetails()))
            .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
            .message(ABBREVIATION_UPDATE_NOT_ALLOWED)
            .displayInfo(builder()
                .code("SEPODI.SERVICE_POINTS.ABBREVIATION_UPDATE_NOT_ALLOWED")
                .build())
            .build());
    }
}
