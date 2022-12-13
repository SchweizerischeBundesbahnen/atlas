package ch.sbb.atlas.servicepointdirectory.converter;

import ch.sbb.atlas.servicepointdirectory.enumeration.MeansOfTransport;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MeansOfTransportConverter implements AttributeConverter<MeansOfTransport, String> {

    @Override
    public String convertToDatabaseColumn(MeansOfTransport meansOfTransport) {
        return meansOfTransport.name();
    }

    @Override
    public MeansOfTransport convertToEntityAttribute(String meansOfTransport) {
        return MeansOfTransport.valueOf(meansOfTransport);
    }

}