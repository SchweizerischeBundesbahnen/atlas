package ch.sbb.atlas.geoupdate.job.model;

import ch.sbb.atlas.imports.ItemImportResponseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "GeoUpdateItemResult")
public class GeoUpdateItemResultModel {

  private String sloid;

  private Long id;

  private String message;

  private ItemImportResponseStatus status;

}
