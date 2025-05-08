package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.workflow.PersonModel;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointRestartWorkflowModel;
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

  public Person toPersonEntity(StopPointRejectWorkflowModel rejectWorkflowModel) {
    return Person.builder()
        .firstName(rejectWorkflowModel.getFirstName())
        .lastName(rejectWorkflowModel.getLastName())
        .mail(rejectWorkflowModel.getMail())
        .organisation(rejectWorkflowModel.getOrganisation())
        .build();
  }

  public Person toPersonEntity(StopPointRestartWorkflowModel restartWorkflowModel) {
    return Person.builder()
        .firstName(restartWorkflowModel.getFirstName())
        .lastName(restartWorkflowModel.getLastName())
        .mail(restartWorkflowModel.getMail())
        .organisation(restartWorkflowModel.getOrganisation())
        .build();
  }

}
