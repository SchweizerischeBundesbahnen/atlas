package ch.sbb.atlas.servicepointdirectory.module.geodata.service;

import static ch.sbb.atlas.servicepointdirectory.module.geodata.helper.ServicePointGeoLocationUtils.hasDiffServicePointGeolocation;
import static ch.sbb.atlas.servicepointdirectory.module.geodata.model.UpdateGeoLocationResultContainer.mapToCurrentVersionDataRages;
import static ch.sbb.atlas.servicepointdirectory.module.geodata.model.UpdateGeoLocationResultContainer.mapToUpdatedVersionDataRages;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.geodata.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.module.geodata.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeoReferenceJobService {

  private final ServicePointService servicePointService;

  public UpdateGeoLocationResultContainer updateGeoLocation(Long id) {
    ServicePointVersion servicePointVersionToUpdate = servicePointService.getServicePointVersionById(id);
    ServicePointGeolocation currentServicePointGeolocation = servicePointVersionToUpdate.getServicePointGeolocation();
    ServicePointGeolocation updatedServicePointGeolocation = getGeoReferenceInformation(currentServicePointGeolocation);

    if (hasDiffServicePointGeolocation(currentServicePointGeolocation, updatedServicePointGeolocation)) {

      List<ServicePointVersion> currentVersions = servicePointService.findAllByNumberOrderByValidFrom(
          servicePointVersionToUpdate.getNumber());
      ServicePointVersion editedVersion = servicePointVersionToUpdate.toBuilder()
          .servicePointGeolocation(updatedServicePointGeolocation)
          .validFrom(LocalDate.now())
          .build();
      List<ReadServicePointVersionModel> updatedServicePointVersionModels =
          servicePointService.updateAndPublish(servicePointVersionToUpdate, editedVersion, currentVersions);

      return UpdateGeoLocationResultContainer.builder()
          .currentServicePointGeolocation(currentServicePointGeolocation)
          .updatedServicePointGeolocation(updatedServicePointGeolocation)
          .id(servicePointVersionToUpdate.getId())
          .sloid(servicePointVersionToUpdate.getSloid())
          .updatedVersionsDataRange(mapToUpdatedVersionDataRages(updatedServicePointVersionModels))
          .currentVersionsDataRange(mapToCurrentVersionDataRages(currentVersions))
          .build();
    }
    return null;
  }
}