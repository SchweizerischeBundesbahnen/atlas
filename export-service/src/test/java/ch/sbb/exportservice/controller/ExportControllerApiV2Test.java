package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

class ExportControllerApiV2Test {

  private ExportControllerApiV2 controller;
  private Runnable exportServiceOperationMock;

  @BeforeEach
  void setUp() {
    exportServiceOperationMock = Mockito.mock(Runnable.class);
    controller = new ExportControllerApiV2(
        Map.of(
            "prm/stop-point-batch",
            exportServiceOperationMock
        )
    );
  }

  @Test
  void startExport() {
    // given

    // when
    final ResponseEntity<Void> response = controller.startExport("prm", "stop-point-batch");

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    Mockito.verify(exportServiceOperationMock).run();
  }

}
