package ch.sbb.atlas.imports.servicepoint.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.model.ItemImportResponseStatus;
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
@Schema(name = "LoadingPointItemImportResult")
public class LoadingPointItemImportResult {

  private String itemNumber;

  private LocalDate validFrom;

  private LocalDate validTo;

  private ItemImportResponseStatus status;

  private String message;

  public static LoadingPointItemImportResultBuilder successResultBuilder() {
    return LoadingPointItemImportResult.builder()
        .status(ItemImportResponseStatus.SUCCESS)
        .message("[SUCCESS]: This version was imported successfully");
  }

  public static LoadingPointItemImportResultBuilder failedResultBuilder(Exception exception) {
    return LoadingPointItemImportResult.builder()
        .status(ItemImportResponseStatus.FAILED)
        .message("[FAILED]: This version could not be imported due to: " + exception.getMessage());
  }

}
