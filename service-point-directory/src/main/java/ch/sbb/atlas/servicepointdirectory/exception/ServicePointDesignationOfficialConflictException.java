package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import java.util.List;
import java.util.function.Function;

public class ServicePointDesignationOfficialConflictException extends ServicePointDesignationConflictException {

  public ServicePointDesignationOfficialConflictException(ServicePointVersion newVersion,
      List<ServicePointVersion> overlappingVersions) {
    super(newVersion, overlappingVersions);
  }

  protected Function<ServicePointVersion, Detail> toErrorDetail() {
    return servicePointVersion -> ValidFromDetail.builder()
        .field(Fields.designationOfficial)
        .message("DesignationOfficial {0} already taken from {1} to {2} by {3}")
        .displayInfo(builder()
            .code(CODE_PREFIX + "DESIGNATION_OFFICIAL")
            .with(Fields.designationOfficial, getNewVersion().getDesignationOfficial())
            .with(Fields.validFrom, servicePointVersion.getValidFrom())
            .with(Fields.validTo, servicePointVersion.getValidTo())
            .with(Fields.number, String.valueOf(servicePointVersion.getNumber().getNumber()))
            .build()).build();
  }

}
