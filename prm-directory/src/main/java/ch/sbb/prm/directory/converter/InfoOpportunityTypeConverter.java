package ch.sbb.prm.directory.converter;

import ch.sbb.prm.directory.enumeration.InfoOpportunityAttributeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class InfoOpportunityTypeConverter  implements AttributeConverter<InfoOpportunityAttributeType, String> {

  @Override
  public String convertToDatabaseColumn(InfoOpportunityAttributeType infoOpportunityAttributeType) {
    return infoOpportunityAttributeType.name();
  }

  @Override
  public InfoOpportunityAttributeType convertToEntityAttribute(String infoOpportunityType) {
    return InfoOpportunityAttributeType.valueOf(infoOpportunityType);
  }

}
