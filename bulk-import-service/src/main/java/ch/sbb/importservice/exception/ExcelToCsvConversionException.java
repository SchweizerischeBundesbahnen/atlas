package ch.sbb.importservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ExcelToCsvConversionException extends AtlasException {

  private final Cell cell;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("""
            Could not convert given Excel File to CSV.
            """)
        .error("Unrecognized Cell Value at row=" + cell.getRowIndex() + " column=" + cell.getColumnIndex())
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Could not convert cell at column={0}, row={1}")
        .field("file")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.ERROR.EXCEL_CONVERSION")
            .with("columnIndex", String.valueOf(cell.getColumnIndex()))
            .with("rowIndex", String.valueOf(cell.getRowIndex()))
            .build())
        .build());
  }

}
