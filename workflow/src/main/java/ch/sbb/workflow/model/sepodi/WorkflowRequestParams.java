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
public class WorkflowRequestParams {

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<Integer> workflowIds = new ArrayList<>();

    @Parameter(description = "")
    @Singular("status")
    private List<WorkflowStatus> status = new ArrayList<>();

    @Parameter(description = "Unique key for service points which is used in the customer information.")
    @Singular(ignoreNullCollections = true)
    private List<String> sloids = new ArrayList<>();

    @Parameter(description = "")
    @Singular("designation")
    private List<String> designation = new ArrayList<>();

    @Parameter(description = "")
    @Singular("localityName")
    private List<String> localityName = new ArrayList<>();

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<String> sboids = new ArrayList<>();

    @Parameter(description = ""
            + ". Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
    @Singular("stopPointValidFrom")
    private List<WorkflowStatus> stopPointValidFrom = new ArrayList<>();

    @Parameter(description = "")
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN, fallbackPatterns = { AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T, AtlasApiConstants.ISO_DATE_TIME_FORMAT_PATTERN })
    private LocalDateTime createdAt;

    @Parameter(description = "")
    @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN, fallbackPatterns = { AtlasApiConstants.DATE_FORMAT_PATTERN_CH })
    private LocalDate validFrom;


}
