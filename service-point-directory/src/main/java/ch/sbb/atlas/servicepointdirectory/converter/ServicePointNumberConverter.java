package ch.sbb.atlas.servicepointdirectory.converter;

import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ServicePointNumberConverter implements AttributeConverter<ServicePointNumber, Integer> {

  @Override
  public Integer convertToDatabaseColumn(ServicePointNumber servicePointNumber) {
    return servicePointNumber == null ? null : servicePointNumber.getValue();
  }

  @Override
  public ServicePointNumber convertToEntityAttribute(Integer servicePointNumber) {
    return ServicePointNumber.of(servicePointNumber);
  }

}
