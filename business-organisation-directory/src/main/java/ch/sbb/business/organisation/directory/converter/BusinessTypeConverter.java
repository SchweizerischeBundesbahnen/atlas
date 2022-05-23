package ch.sbb.business.organisation.directory.converter;

import ch.sbb.business.organisation.directory.entity.BusinessType;
import java.util.Arrays;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BusinessTypeConverter implements AttributeConverter<BusinessType, Integer> {

  @Override
  public Integer convertToDatabaseColumn(BusinessType businessType) {
    return businessType.getId();
  }

  @Override
  public BusinessType convertToEntityAttribute(Integer businessTypeId) {
    return Arrays.stream(BusinessType.values())
                 .filter(businessType -> businessType.getId() == businessTypeId)
                 .findFirst()
                 .orElseThrow(() -> new IllegalStateException(
                     "BusinessTypeId: " + businessTypeId + " does not exist!"));
  }
}
