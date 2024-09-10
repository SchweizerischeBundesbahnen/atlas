package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel.Detail;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSwissWithGeoTransfer;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointSwissWithGeoMapper {

  public static ServicePointSwissWithGeoLocationModel toModel(String sloid,
      List<ServicePointSwissWithGeoTransfer> swissWithGeoTransfers) {
    List<Detail> details = swissWithGeoTransfers.stream()
        .map(geoTransfer -> Detail.builder()
            .id(geoTransfer.getId())
            .validFrom(geoTransfer.getValidFrom())
            .build())
        .toList();
    return ServicePointSwissWithGeoLocationModel.builder()
        .sloid(sloid)
        .details(details)
        .build();
  }
}