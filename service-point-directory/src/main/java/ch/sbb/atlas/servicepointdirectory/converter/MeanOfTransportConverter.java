package ch.sbb.atlas.servicepointdirectory.converter;

import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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