package ch.sbb.workflow.mapper;

import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointClientPersonMapper {

  public static StopPointClientPersonModel toModel(Person person) {
    return StopPointClientPersonModel.builder()
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .personFunction(person.getFunction())
        .mail(person.getMail())
        .organisation(person.getOrganisation())
        .creationDate(person.getCreationDate())
        .editionDate(person.getEditionDate())
        .build();
  }

  public static Person toEntity(StopPointClientPersonModel model) {
    return Person.builder()
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .function(model.getPersonFunction())
        .organisation(model.getOrganisation())
        .mail(model.getMail())
        .build();
  }

}
