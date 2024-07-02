package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.workflow.entity.StopPointWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

@RequiredArgsConstructor

public class StopPointWorkflowDesignationOfficialInvalidException extends AtlasException {
    private static final String DESIGNATION_OFFICIAL_NOT_EMPTY = "Designation Official must not be empty.";

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.PRECONDITION_REQUIRED.value())
                .message(DESIGNATION_OFFICIAL_NOT_EMPTY)
                .error("StopPoint Designation Official error")
                .details(new TreeSet<>(getErrorDetails()))
                .build();
    }

    private List<ErrorResponse.Detail> getErrorDetails() {
        return List.of(ErrorResponse.Detail.builder()
                .message("Wrong status")
                .field(StopPointWorkflow.Fields.status)
                .displayInfo(builder()
                        //TODO:
                        .code("Fehler")
                        .build())
                .build());
    }
}
