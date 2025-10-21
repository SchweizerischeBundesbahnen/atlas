package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Timetable Field Numbers")
@RequestMapping("internal/field-numbers")
public interface TimetableFieldNumberApiInternal {

  @GetMapping
  @PageableAsQueryParam
  Container<TimetableFieldNumberModel> getOverview(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) String number,
      @RequestParam(required = false) String businessOrganisation,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices);

  @PostMapping("/{ttfnId}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TTFN)")
  List<TimetableFieldNumberVersionModel> revokeTimetableFieldNumber(@PathVariable String ttfnId);

  @DeleteMapping("/{ttfnid}")
  void deleteVersions(@PathVariable String ttfnid);

  @PostMapping("/import-quovadis")
  void importQuoVadisData(MultipartFile file);

}
