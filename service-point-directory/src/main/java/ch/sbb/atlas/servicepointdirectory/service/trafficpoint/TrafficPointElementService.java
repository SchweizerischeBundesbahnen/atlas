package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TrafficPointElementService {

    private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;
    private final VersionableService versionableService;
    private final TrafficPointElementValidationService trafficPointElementValidationService;

    public Page<TrafficPointElementVersion> findAll(TrafficPointElementSearchRestrictions searchRestrictions) {
        return trafficPointElementVersionRepository.findByServicePointParameters(
                searchRestrictions.getTrafficPointElementRequestParams(), searchRestrictions.getPageable());
    }
    public Integer findServicePointNumberForSboid(String sboid) {
        return trafficPointElementVersionRepository.forGivenSboidFindSpn(sboid).stream().findFirst().orElseThrow();
    }

//  public Page<TrafficPointElementVersion> findAll(TrafficPointElementSearchRestrictions searchRestrictions) {
//    List<String> bos = new ArrayList<>();
//    bos.add("imposiblebos");
//    List<Integer> shorts = new ArrayList<>();
//    shorts.add(-1);
//    if (!searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations().isEmpty() ||
//    !searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort().isEmpty() ||
//    !searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes().isEmpty()) {
//      bos.addAll(searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations());
//      shorts.addAll(searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort());
//      List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu(bos,shorts);
//      Page<TrafficPointElementVersion> page = new PageImpl<>(list);
//      return page;
//    } else {
//      return trafficPointElementVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
//    }
//  }

  public Page<TrafficPointElementVersion> findAll(TrafficPointElementSearchRestrictions searchRestrictions) {

        if (!searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations().isEmpty()) {
            if (!searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort().isEmpty()) {
                if (!searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes().isEmpty()) {
                    String query = "where spv.business_organisation in (" + searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations() + ") " +
                            "and spv.number_short in (" + searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort() + ")" +
                            "ans spv.country in (" + searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes() + ")";
                    List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
                    Page<TrafficPointElementVersion> page = new PageImpl<>(list);
                    return page;
                } else {
                    String query = "where spv.business_organisation in (" + searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations() + ") " +
                            "and spv.number_short in (" + searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort() + ")";
                    List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
                    Page<TrafficPointElementVersion> page = new PageImpl<>(list);
                    return page;
                }
            }
            if (!searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes().isEmpty()) {
                String query = "where spv.business_organisation in (" + searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations() + ") " +
                        "and spv.country in (" + searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes() + ")";
                List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
                Page<TrafficPointElementVersion> page = new PageImpl<>(list);
                return page;
            } else {
                String query = "where spv.business_organisation in (" + searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations() + ") ";
                List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
                Page<TrafficPointElementVersion> page = new PageImpl<>(list);
                return page;
            }
        }
        if (!searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort().isEmpty()) {
            if (!searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes().isEmpty()) {
                String query = "where spv.number_short in (" + searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort() + ")" +
                        "and spv.country in (" + searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes() + ")";
                List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
                Page<TrafficPointElementVersion> page = new PageImpl<>(list);
                return page;
            } else {
                String stringOfShorts = searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                String query = " where spv.number_short in (" + stringOfShorts + ")";
                List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
                Page<TrafficPointElementVersion> page = new PageImpl<>(list);
                return page;
            }
        }
        if (!searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes().isEmpty()) {
            String query = "where spv.country in (" + searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes() + ")";
            List<TrafficPointElementVersion> list = trafficPointElementVersionRepository.blaBloBlu1(query);
            Page<TrafficPointElementVersion> page = new PageImpl<>(list);
            return page;
        } else {
            return trafficPointElementVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
        }

        if (!searchRestrictions.getTrafficPointElementRequestParams().getBusinessOrganisations().isEmpty() ||
                !searchRestrictions.getTrafficPointElementRequestParams().getServicePointNumberShort().isEmpty() ||
                !searchRestrictions.getTrafficPointElementRequestParams().getUicCountryCodes().isEmpty()) {
            return trafficPointElementVersionRepository.blaBloBlu2(searchRestrictions.getTrafficPointElementRequestParams(), searchRestrictions.getPageable());
        } else {
            return trafficPointElementVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
        }
    }

    public List<TrafficPointElementVersion> findBySloidOrderByValidFrom(String sloid) {
        return trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(sloid);
    }

    public Optional<TrafficPointElementVersion> findById(Long id) {
        return trafficPointElementVersionRepository.findById(id);
    }

    public boolean isTrafficPointElementExisting(String sloid) {
        return trafficPointElementVersionRepository.existsBySloid(sloid);
    }

    @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditTrafficPoint(#servicePointVersions, "
            + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
    public TrafficPointElementVersion checkPermissionRightsAndSave(TrafficPointElementVersion trafficPointElementVersion, List<ServicePointVersion> servicePointVersions) {
        return save(trafficPointElementVersion);
    }

    public TrafficPointElementVersion save(TrafficPointElementVersion trafficPointElementVersion) {
        trafficPointElementValidationService.validateServicePointNumberExists(trafficPointElementVersion.getServicePointNumber());
        return trafficPointElementVersionRepository.save(trafficPointElementVersion);
    }

    public void deleteById(Long id) {
        trafficPointElementVersionRepository.deleteById(id);
    }

    @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditTrafficPoint(#currentVersions, "
            + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
    public void checkPermissionRightsAndUpdate(TrafficPointElementVersion currentVersionTPEV, TrafficPointElementVersion editedVersionTPEV, List<ServicePointVersion> currentVersions) {
        updateTrafficPointElementVersion(currentVersionTPEV, editedVersionTPEV);
    }

    public void updateTrafficPointElementVersion(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion) {
        trafficPointElementVersionRepository.incrementVersion(currentVersion.getSloid());
        if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
            throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
        }

        List<TrafficPointElementVersion> dbVersions = findBySloidOrderByValidFrom(currentVersion.getSloid());
        List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion, editedVersion,
                dbVersions);
        versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, this::save, this::deleteById);
    }
}
