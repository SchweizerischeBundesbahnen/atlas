package ch.sbb.atlas.base.service.imports.servicepoint.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ServicePointItemImportResult")
public class ServicePointItemImportResult {

  private Integer itemNumber;

  private LocalDate validFrom;

  private LocalDate validTo;

  private String status;

  private String message;

}
