package ch.sbb.exportservice.entity.converter;

import ch.sbb.exportservice.entity.enumeration.Category;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CategoryConverter implements AttributeConverter<Category, String> {

  @Override
  public String convertToDatabaseColumn(Category category) {
    return category.name();
  }

  @Override
  public Category convertToEntityAttribute(String category) {
    return Category.valueOf(category);
  }

}
