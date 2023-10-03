package ch.sbb.prm.directory.converter;

import ch.sbb.prm.directory.enumeration.InfoOpportunityType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class InfoOpportunityTypeConverter  implements AttributeConverter<InfoOpportunityType, String> {

  @Override
  public String convertToDatabaseColumn(InfoOpportunityType infoOpportunityType) {
    return infoOpportunityType.name();
  }

  @Override
  public InfoOpportunityType convertToEntityAttribute(String infoOpportunityType) {
    return InfoOpportunityType.valueOf(infoOpportunityType);
  }

}
