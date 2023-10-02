package ch.sbb.atlas.servicepoint.converter;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ServicePointNumberConverter implements AttributeConverter<ServicePointNumber, Integer> {

  @Override
  public Integer convertToDatabaseColumn(ServicePointNumber servicePointNumber) {
    return servicePointNumber == null ? null : servicePointNumber.getNumber();
  }

  @Override
  public ServicePointNumber convertToEntityAttribute(Integer servicePointNumber) {
    return servicePointNumber == null ? null : ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
  }

}
