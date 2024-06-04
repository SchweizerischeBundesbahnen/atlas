package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class WorkflowRequestParams {

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<Integer> workflowId = new ArrayList<>();

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<WorkflowStatus> status = new ArrayList<>();

    @Parameter(description = "Unique key for service points which is used in the customer information.")
    @Singular(ignoreNullCollections = true)
    private List<String> sloids = new ArrayList<>();

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<String> newWorkflowName = new ArrayList<>();

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<String> locality = new ArrayList<>();

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<String> sboids = new ArrayList<>();

    @Parameter(description = ""
            + ". Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
    private List<WorkflowStatus> stopPointValidFrom = new ArrayList<>();

    @Parameter(description = ""
            + ". Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
    private List<WorkflowStatus> createdAt = new ArrayList<>();
}
