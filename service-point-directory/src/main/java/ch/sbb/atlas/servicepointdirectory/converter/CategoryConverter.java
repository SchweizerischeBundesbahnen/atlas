package ch.sbb.atlas.servicepointdirectory.converter;

import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
