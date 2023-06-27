package ch.sbb.importservice.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Import Service Point Batch")
@RequestMapping("v1/import")
public interface ImportServicePointBatchApiV1 {

  @PostMapping("service-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  void startServicePointImportBatch();

  @PostMapping("service-point")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  ResponseEntity<?> startServicePointImport(@RequestParam("file") MultipartFile multipartFile);

  @PostMapping("loading-point")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  ResponseEntity<?> startLoadingPointImport(@RequestParam("file") MultipartFile multipartFile);

  @PostMapping("traffic-point-batch")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @Async
  void startTrafficPointImportBatch();

  @PostMapping("traffic-point")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  ResponseEntity<?> startTrafficPointImport(@RequestParam("file") MultipartFile multipartFile);
}
