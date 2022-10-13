package ch.sbb.atlas.base.service.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class BaseVersionModel {

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String creator;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

  @Schema(description = "User editor", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String editor;

}
