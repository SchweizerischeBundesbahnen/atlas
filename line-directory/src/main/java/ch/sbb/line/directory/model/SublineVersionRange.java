package ch.sbb.line.directory.model;

import ch.sbb.line.directory.entity.SublineVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "")
public class SublineVersionRange {

  private SublineVersion oldestVersion;
  private SublineVersion latestVersion;
}
