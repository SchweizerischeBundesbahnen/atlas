package ch.sbb.line.directory.converter;

import ch.sbb.line.directory.model.RgbColor;
import java.awt.Color;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RgbColorConverter implements AttributeConverter<RgbColor, String> {

  @Override
  public String convertToDatabaseColumn(RgbColor color) {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())
                 .toUpperCase();
  }

  @Override
  public RgbColor convertToEntityAttribute(String colorString) {
    Color color = Color.decode(colorString);
    return new RgbColor(color.getRed(), color.getGreen(), color.getBlue());
  }

}