package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
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
@Schema(name = "ServicePointImportResult")
public class ServicePointImportResult {

  private ServicePointNumber servicePointNumber;

  private LocalDate validFrom;

  private LocalDate validTo;

  private String status;

  private String message;

}
