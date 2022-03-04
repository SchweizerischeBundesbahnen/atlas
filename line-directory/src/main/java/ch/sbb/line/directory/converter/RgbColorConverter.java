package ch.sbb.line.directory.converter;

import ch.sbb.line.directory.model.RgbColor;
import java.awt.Color;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RgbColorConverter implements AttributeConverter<RgbColor, String> {

  public static String toHex(RgbColor color) {
    if (color == null) {
      return null;
    }
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())
                 .toUpperCase();
  }

  public static RgbColor fromHex(String hexString) {
    if (hexString == null) {
      return null;
    }
    Color color = Color.decode(hexString);
    return new RgbColor(color.getRed(), color.getGreen(), color.getBlue());
  }

  @Override
  public String convertToDatabaseColumn(RgbColor color) {
    return toHex(color);
  }

  @Override
  public RgbColor convertToEntityAttribute(String colorString) {
    return fromHex(colorString);
  }

}