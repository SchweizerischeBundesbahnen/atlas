package ch.sbb.atlas.servicepoint.converter;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MeanOfTransportConverter implements AttributeConverter<MeanOfTransport, String> {

  @Override
  public String convertToDatabaseColumn(MeanOfTransport meanOfTransport) {
    return meanOfTransport.name();
  }

  @Override
  public MeanOfTransport convertToEntityAttribute(String meansOfTransport) {
    return MeanOfTransport.valueOf(meansOfTransport);
  }

}