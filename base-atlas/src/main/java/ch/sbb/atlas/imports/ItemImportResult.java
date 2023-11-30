package ch.sbb.atlas.imports;

import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
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
@Schema(name = "ItemImportResult")
public class ItemImportResult {

  private String itemNumber;

  private LocalDate validFrom;

  private LocalDate validTo;

  private ItemImportResponseStatus status;

  private String message;

  public static ItemImportResultBuilder successResultBuilder() {
    return ItemImportResult.builder()
        .status(ItemImportResponseStatus.SUCCESS)
        .message("[SUCCESS]: This version was imported successfully");
  }

  public static ItemImportResultBuilder failedResultBuilder(Exception exception) {
    return ItemImportResult.builder()
        .status(ItemImportResponseStatus.FAILED)
        .message("[FAILED]: This version could not be imported due to: " + exception.getMessage());
  }

  public static ItemImportResultBuilder failedHeightResultBuilder(Exception exception) {
    return ItemImportResult.builder()
        .status(ItemImportResponseStatus.FAILED)
        .message("[Success]: This version was imported successfully but the height could not be calculated due to: " + exception.getMessage());
  }

}
