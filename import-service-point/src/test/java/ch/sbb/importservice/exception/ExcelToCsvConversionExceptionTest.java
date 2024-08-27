package ch.sbb.importservice.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import java.util.SortedSet;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ExcelToCsvConversionExceptionTest {

  @Mock
  private Cell cell;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    when(cell.getRowIndex()).thenReturn(1);
    when(cell.getColumnIndex()).thenReturn(2);
  }

  @Test
  void shouldProvideErrorResponse() {
    ExcelToCsvConversionException exception = new ExcelToCsvConversionException(cell);
    SortedSet<Detail> details = exception.getErrorResponse().getDetails();

    assertThat(details).hasSize(1);
    assertThat(details.getFirst().getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.ERROR.EXCEL_CONVERSION");
    assertThat(details.getFirst().getMessage()).isEqualTo("Could not convert cell at column=2, row=1");
  }
}