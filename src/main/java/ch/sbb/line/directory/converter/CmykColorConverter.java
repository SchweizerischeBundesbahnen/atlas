package ch.sbb.line.directory.converter;

import ch.sbb.line.directory.model.CmykColor;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CmykColorConverter implements AttributeConverter<CmykColor, String> {

  private static final String SEPARATOR = ",";

  private static final int CYAN_INDEX = 0;
  private static final int MAGENTA_INDEX = 1;
  private static final int YELLOW_INDEX = 2;
  private static final int BLACK_INDEX = 3;

  @Override
  public String convertToDatabaseColumn(CmykColor color) {
    return toCmykString(color);
  }

  public static String toCmykString(CmykColor color) {
    if (color == null) {
      return null;
    }
    return color.getCyan() + SEPARATOR
        + color.getMagenta() + SEPARATOR
        + color.getYellow() + SEPARATOR
        + color.getBlack();
  }

  @Override
  public CmykColor convertToEntityAttribute(String colorString) {
    return fromCmykString(colorString);
  }

  public static CmykColor fromCmykString(String colorString) {
    if (colorString == null) {
      return null;
    }
    String[] cymkColors = colorString.split(SEPARATOR);
    return new CmykColor(Integer.parseInt(cymkColors[CYAN_INDEX]),
        Integer.parseInt(cymkColors[MAGENTA_INDEX]),
        Integer.parseInt(cymkColors[YELLOW_INDEX]), Integer.parseInt(cymkColors[BLACK_INDEX]));
  }

}