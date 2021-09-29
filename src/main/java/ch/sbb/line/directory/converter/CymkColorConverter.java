package ch.sbb.line.directory.converter;

import ch.sbb.line.directory.model.CymkColor;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CymkColorConverter implements AttributeConverter<CymkColor, String> {

  private static final String SEPARATOR = ",";

  private static final int CYAN_INDEX = 0;
  private static final int MAGENTA_INDEX = 1;
  private static final int YELLOW_INDEX = 2;
  private static final int BLACK_INDEX = 3;

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
    return new CymkColor(Integer.parseInt(cymkColors[CYAN_INDEX]),
        Integer.parseInt(cymkColors[MAGENTA_INDEX]),
        Integer.parseInt(cymkColors[YELLOW_INDEX]), Integer.parseInt(cymkColors[BLACK_INDEX]));
  }

}