package ch.sbb.atlas.imports.servicepoint.trafficpoint;

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
@Schema(name = "TrafficPointItemImportResult")
public class TrafficPointItemImportResult {

  private Integer itemNumber;

  private LocalDate validFrom;

  private LocalDate validTo;

  private ItemImportResponseStatus status;

  private String message;

  public static TrafficPointItemImportResultBuilder successResultBuilder() {
    return TrafficPointItemImportResult.builder()
        .status(ItemImportResponseStatus.SUCCESS)
        .message("[SUCCESS]: This version was imported successfully");
  }

  public static TrafficPointItemImportResultBuilder failedResultBuilder(Exception exception) {
    return TrafficPointItemImportResult.builder()
        .status(ItemImportResponseStatus.FAILED)
        .message("[FAILED]: This version could not be imported due to: " + exception.getMessage());
  }

}
