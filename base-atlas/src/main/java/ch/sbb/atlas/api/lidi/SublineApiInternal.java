package ch.sbb.atlas.api.lidi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URL;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sublines")
@RequestMapping("internal/sublines")
public interface SublineApiInternal {

  @PostMapping("{slnid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void revokeSubline(@PathVariable String slnid);

  @DeleteMapping("{slnid}")
  void deleteSublines(@PathVariable String slnid);

  @Deprecated(forRemoval = true)
  @Operation(description = "Export all subline versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFullSublineVersions();

  @Deprecated(forRemoval = true)
  @Operation(description = "Export all actual subline versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportActualSublineVersions();

  @Deprecated(forRemoval = true)
  @Operation(description = "Export all subline versions for the current timetable year change as csv and zip file to the ATLAS "
      + "Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFutureTimetableSublineVersions();

}
