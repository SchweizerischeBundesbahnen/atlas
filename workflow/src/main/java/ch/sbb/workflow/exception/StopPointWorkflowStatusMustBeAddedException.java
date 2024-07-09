package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow.Fields;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.TreeSet;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

public class StopPointWorkflowStatusMustBeAddedException extends AtlasException {

    private static final String WORKFLOW_STATUS_WRONG = "Workflow status must be " + WorkflowStatus.ADDED;

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.PRECONDITION_REQUIRED.value())
                .message(WORKFLOW_STATUS_WRONG)
                .error("StopPoint Workflow error")
                .details(new TreeSet<>(getErrorDetails()))
                .build();
    }

    private List<Detail> getErrorDetails() {
        return List.of(Detail.builder()
                .message("Wrong status")
                .field(Fields.status)
                .displayInfo(builder()
                        .code("WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_ADDED")
                        .with(Fields.status, WorkflowStatus.ADDED.name())
                        .build())
                .build());
    }
}
