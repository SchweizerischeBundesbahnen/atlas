package ch.sbb.atlas.imports.servicepoint.model;

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

  private ItemImportResponseStatus status;

  private String message;

  public static ServicePointItemImportResultBuilder successResultBuilder() {
    return ServicePointItemImportResult.builder()
        .status(ItemImportResponseStatus.SUCCESS)
        .message("[SUCCESS]: This version was imported successfully");
  }

  public static ServicePointItemImportResultBuilder failedResultBuilder(Exception exception) {
    return ServicePointItemImportResult.builder()
        .status(ItemImportResponseStatus.FAILED)
        .message("[FAILED]: This version could not be imported due to: " + exception.getMessage());
  }

}
