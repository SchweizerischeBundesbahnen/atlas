package ch.sbb.business.organisation.directory.converter;

import ch.sbb.business.organisation.directory.entity.BusinessType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
