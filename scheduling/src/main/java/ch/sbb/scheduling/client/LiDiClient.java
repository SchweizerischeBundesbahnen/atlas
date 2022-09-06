package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "lidiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface LiDiClient {

  @PostMapping(value = "/line-directory/v1/lines/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiLineExportFull();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiLineExportActual();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiLineExportNextTimetableVersions();

  @PostMapping(value = "/line-directory/v1/sublines/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiSublineExportFull();

  @PostMapping(value = "/line-directory/v1/sublines/export-csv/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiSublineExportActual();

  @PostMapping(value = "/line-directory/v1/sublines/export-csv/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiSublineExportNextTimetableVersions();

}
