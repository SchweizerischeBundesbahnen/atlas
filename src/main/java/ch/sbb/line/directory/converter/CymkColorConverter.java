package ch.sbb.line.directory.converter;

import ch.sbb.line.directory.model.CymkColor;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CymkColorConverter implements AttributeConverter<CymkColor, String> {

  private static final String SEPARATOR = ",";

  @Override
  public String convertToDatabaseColumn(CymkColor color) {
    return color.getCyan() + SEPARATOR
        + color.getMagenta() + SEPARATOR
        + color.getYellow() + SEPARATOR
        + color.getBlack();
  }

  @Override
  public CymkColor convertToEntityAttribute(String colorString) {
    String[] cymkColors = colorString.split(SEPARATOR);
    return new CymkColor(Integer.parseInt(cymkColors[0]), Integer.parseInt(cymkColors[1]),
        Integer.parseInt(cymkColors[2]), Integer.parseInt(cymkColors[3]));
  }

}