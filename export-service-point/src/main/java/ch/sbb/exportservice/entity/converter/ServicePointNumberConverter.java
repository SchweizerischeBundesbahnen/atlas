package ch.sbb.exportservice.entity.converter;

import ch.sbb.exportservice.entity.model.ServicePointNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ServicePointNumberConverter implements AttributeConverter<ServicePointNumber, Integer> {

  @Override
  public Integer convertToDatabaseColumn(ServicePointNumber servicePointNumber) {
    return servicePointNumber == null ? null : servicePointNumber.getValue();
  }

  @Override
  public ServicePointNumber convertToEntityAttribute(Integer servicePointNumber) {
    return servicePointNumber == null ? null : ServicePointNumber.of(servicePointNumber);
  }

}
