package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
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
@FieldNameConstants
@Schema(name = "TimetableHearingStatementDocument")
public class TimetableHearingStatementDocumentModel {

  @Schema(description = "Technical identifier", example = "1", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "FileName of uploaded document", example = "Document.pdf", accessMode = AccessMode.READ_ONLY)
  @Size(max = AtlasFieldLengths.LENGTH_500)
  @NotNull
  private String fileName;

  @Schema(description = "Content length", example = "1123123", accessMode = AccessMode.READ_ONLY)
  @NotNull
  private Long fileSize;

}
