package ch.sbb.atlas.api.lidi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URL;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sublines")
@RequestMapping("v1/sublines")
public interface SublineApiV1 {

  @PostMapping("{slnid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  List<SublineVersionModel> revokeSubline(@PathVariable String slnid);

  @DeleteMapping("{slnid}")
  void deleteSublines(@PathVariable String slnid);

  /**
   * @deprecated
   */
  @Deprecated(forRemoval = true, since = "2.328.0")
  @GetMapping("versions/{slnid}")
  List<SublineVersionModel> getSublineVersion(@PathVariable String slnid);

  @GetMapping("subline-coverage/{slnid}")
  CoverageModel getSublineCoverage(@PathVariable String slnid);

  @Operation(description = "Export all subline versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFullSublineVersions();

  @Operation(description = "Export all actual subline versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportActualSublineVersions();

  @Operation(description = "Export all subline versions for the current timetable year change as csv and zip file to the ATLAS "
      + "Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFutureTimetableSublineVersions();

}
