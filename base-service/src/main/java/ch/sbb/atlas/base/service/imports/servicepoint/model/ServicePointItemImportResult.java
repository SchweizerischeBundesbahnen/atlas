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

  public static ServicePointItemImportResultBuilder successResultBuilder() {
    return ServicePointItemImportResult.builder()
        .status("SUCCESS")
        .message("[SUCCESS]: This version was imported successfully");
  }

  public static ServicePointItemImportResultBuilder failedResultBuilder(Exception exception) {
    return ServicePointItemImportResult.builder()
        .status("FAILED")
        .message("[FAILED]: This version could not be imported due to: " + exception.getMessage());
  }

}
