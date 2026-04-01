package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.AuditableVersionModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
@Schema(name = "TimetableHearingStatementDataProtection")
public class TimetableHearingStatementDataProtectionModel extends AuditableVersionModel {

  @Schema(description = "Technical identifier",
      example = "1", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Statement does not contain personal data")
  private boolean statementAnonymous;

  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Schema(description = "Statement anonymized by canton", example = "I need some more busses please.")
  private String anonymousStatement;

  @Size(max = TimetableHearingConstants.MAX_DOCUMENTS)
  @Schema(description = "List of uploaded documents")
  private List<TimetableHearingStatementDocumentModel> documents;

  public List<TimetableHearingStatementDocumentModel> getDocuments() {
    return Objects.requireNonNullElseGet(documents, ArrayList::new);
  }

  @JsonIgnore
  @AssertTrue
  public boolean isWithValidAnonymousStatement() {
    return !statementAnonymous || anonymousStatement == null;
  }
}
