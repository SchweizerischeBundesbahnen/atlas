package ch.sbb.atlas.user.administration.entity;

import ch.sbb.atlas.kafka.model.SwissCanton;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SwissCantonConverter implements AttributeConverter<SwissCanton, String> {

  @Override
  public String convertToDatabaseColumn(SwissCanton category) {
    return category.name();
  }

  @Override
  public SwissCanton convertToEntityAttribute(String category) {
    return SwissCanton.valueOf(category);
  }

}
