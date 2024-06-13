package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class StopPointWorkflowRequestParams {

    @Parameter(description = "Workflow id")
    @Singular(ignoreNullCollections = true)
    private List<Integer> workflowIds = new ArrayList<>();

    @Parameter(description = "Status")
    @Singular("status")
    private List<WorkflowStatus> status = new ArrayList<>();

    @Parameter(description = "Unique key for service points which is used in the customer information.")
    @Singular(ignoreNullCollections = true)
    private List<String> sloids = new ArrayList<>();

    @Parameter(description = "Designation")
    @Singular("designationOfficial")
    private List<String> designationOfficial = new ArrayList<>();

    @Parameter(description = "Locality Name")
    @Singular("localityName")
    private List<String> localityName = new ArrayList<>();

    @Parameter(description = "List of sboids")
    @Singular(ignoreNullCollections = true)
    private List<String> sboids = new ArrayList<>();

    @Parameter(description = "Workflow created at."
            + " Date format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN, fallbackPatterns = { AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T, AtlasApiConstants.ISO_DATE_TIME_FORMAT_PATTERN })
    private LocalDateTime createdAt;

    @Parameter(description = "Service Point version valid from."
            + " Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN, fallbackPatterns = { AtlasApiConstants.DATE_FORMAT_PATTERN_CH })
    private LocalDate versionValidFrom;


}
