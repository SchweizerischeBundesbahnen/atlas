package ch.sbb.line.directory.converter;

import java.awt.Color;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ColorConverter implements AttributeConverter<Color, String> {

  public static String toHexString(Color color) {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  public static Color fromHexString(String hexString) {
    return Color.decode(hexString);
  }

  @Override
  public String convertToDatabaseColumn(Color color) {
    return toHexString(color);
  }

  @Override
  public Color convertToEntityAttribute(String colorString) {
    return fromHexString(colorString);
  }

}