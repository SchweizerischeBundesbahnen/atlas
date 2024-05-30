package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientPersonMapper {

  public static ClientPersonModel toModel(Person person) {
    return ClientPersonModel.builder()
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .personFunction(person.getFunction())
        .mail(person.getMail())
        .creationDate(person.getCreationDate())
        .editionDate(person.getEditionDate())
        .build();
  }

  public static Person toEntity(ClientPersonModel model) {
    return Person.builder()
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .function(model.getPersonFunction())
        .mail(model.getMail())
        .build();
  }

  public static Person toEntity(ClientPersonModel model, StopPointWorkflow  stopPointWorkflow) {
    return Person.builder()
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .function(model.getPersonFunction())
        .mail(model.getMail())
        .stopPointWorkflow(stopPointWorkflow)
        .build();
  }

}
