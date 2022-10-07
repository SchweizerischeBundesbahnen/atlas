package ch.sbb.atlas.base.service.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class BaseModel {

  @Schema(description = "Object creation date", example = "01.01.2000")
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456")
  private String creator;

  @Schema(description = "Last edition date", example = "01.01.2000")
  private LocalDateTime editionDate;

  @Schema(description = "User editor", example = "u123456")
  private String editor;

}
