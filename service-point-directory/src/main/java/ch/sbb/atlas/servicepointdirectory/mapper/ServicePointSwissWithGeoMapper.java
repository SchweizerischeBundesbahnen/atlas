package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel.Detail;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSwissWithGeoTransfer;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointSwissWithGeoMapper {

  public static ServicePointSwissWithGeoModel mapTo(String sloid, List<ServicePointSwissWithGeoTransfer> swissWithGeoTransfers) {
    List<Detail> details = swissWithGeoTransfers.stream()
        .map(geoTransfer -> Detail.builder()
            .id(geoTransfer.getId())
            .validFrom(geoTransfer.getValidFrom())
            .build())
        .toList();
    return ServicePointSwissWithGeoModel.builder()
        .sloid(sloid)
        .details(details)
        .build();
  }
}