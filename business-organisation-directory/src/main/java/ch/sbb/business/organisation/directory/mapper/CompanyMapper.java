package ch.sbb.business.organisation.directory.mapper;

import ch.sbb.atlas.api.bodi.CompanyModel;
import ch.sbb.business.organisation.directory.entity.Company;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CompanyMapper {

  public static CompanyModel fromEntity(Company entity) {
    return CompanyModel.builder()
        .uicCode(entity.getUicCode())
        .name(entity.getName())
        .url(entity.getUrl())
        .startValidity(entity.getStartValidity())
        .endValidity(entity.getEndValidity())
        .shortName(entity.getShortName())
        .freeText(entity.getFreeText())
        .countryCodeIso(entity.getCountryCodeIso())
        .build();
  }

}
