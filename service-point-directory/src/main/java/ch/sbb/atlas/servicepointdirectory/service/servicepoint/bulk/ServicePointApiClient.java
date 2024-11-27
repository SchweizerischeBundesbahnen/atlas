package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ServicePointApiClient {

  private final ServicePointApiV1 servicePointApiV1;

  public void updateServicePoint(Long currentVersionId, UpdateServicePointVersionModel servicePointVersionModel) {
    servicePointApiV1.updateServicePoint(currentVersionId, servicePointVersionModel);
  }

  public ReadServicePointVersionModel createServicePoint(CreateServicePointVersionModel servicePointVersionModel) {
    return servicePointApiV1.createServicePoint(servicePointVersionModel);
  }

}
