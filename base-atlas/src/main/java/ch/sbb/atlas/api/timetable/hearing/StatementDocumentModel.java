package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.model.BaseVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Schema(name = "StatementDocument")
public class StatementDocumentModel extends BaseVersionModel {

    @Schema(description = "Technical identifier", example = "1", accessMode = AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "TimetableHearing to which Statement belongs")
    private TimetableHearingStatementModel timetableHearingStatementModel;

    @Schema(description = "Name of Document Statement")
    private String statementDocumentName;

    @Schema(description = "Size of Document Statement")
    private Long statementDocumentSize;

}
