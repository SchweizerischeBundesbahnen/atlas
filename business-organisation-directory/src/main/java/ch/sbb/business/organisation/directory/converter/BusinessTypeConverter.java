package ch.sbb.business.organisation.directory.converter;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BusinessTypeConverter implements AttributeConverter<BusinessType, String> {

  @Override
  public String convertToDatabaseColumn(BusinessType businessType) {
    return businessType.name();
  }

  @Override
  public BusinessType convertToEntityAttribute(String businessType) {
    return BusinessType.valueOf(businessType);
  }

}
