package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.PersonModel;
import ch.sbb.workflow.entity.Person;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PersonMapper {

  public static PersonModel toModel(Person person) {
    return PersonModel.builder()
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .personFunction(person.getFunction())
        .creationDate(person.getCreationDate())
        .editionDate(person.getEditionDate())
        .build();
  }

  public static Person toEntity(PersonModel model) {
    return Person.builder()
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .function(model.getPersonFunction())
        .build();
  }

}
