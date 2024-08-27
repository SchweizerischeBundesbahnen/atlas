package ch.sbb.workflow.mapper;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowMapper {

  public static ReadStopPointWorkflowModel toModelWithReorderingExaminants(StopPointWorkflow entity, List<StopPointClientPersonModel> examinants) {
    ReadStopPointWorkflowModel model = toModel(entity);
    return reorderExaminants(model, examinants);
  }

  public static ReadStopPointWorkflowModel toModel(StopPointWorkflow entity) {
    return ReadStopPointWorkflowModel.builder()
        .id(entity.getId())
        .sloid(entity.getSloid())
        .versionId(entity.getVersionId())
        .examinants(entity.getExaminants().stream()
            .sorted(Comparator.comparing(Person::getId))
            .map(StopPointClientPersonMapper::toModel)
            .toList())
        .designationOfficial(entity.getDesignationOfficial())
        .ccEmails(entity.getCcEmails())
        .status(entity.getStatus())
        .workflowComment(entity.getWorkflowComment())
        .localityName(entity.getLocalityName())
        .followUpWorkflowId(entity.getFollowUpWorkflow() != null ? entity.getFollowUpWorkflow().getId() : null)
        .versionValidFrom(entity.getVersionValidFrom())
        .versionValidTo(entity.getVersionValidTo())
        .sboid(entity.getSboid())
        .startDate(entity.getStartDate())
        .endDate(entity.getEndDate())
        .createdAt(entity.getCreationDate())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .build();
  }

  public static StopPointWorkflow addStopPointWorkflowToEntity(StopPointAddWorkflowModel model,
      ReadServicePointVersionModel servicePointVersionModel) {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(model.getSloid())
        .versionId(model.getVersionId())
        .ccEmails(model.getCcEmails())
        .applicantMail(model.getApplicantMail())
        .workflowComment(model.getWorkflowComment())
        .sboid(servicePointVersionModel.getBusinessOrganisation())
        .localityName(
            servicePointVersionModel.getServicePointGeolocation().getSwissLocation().getLocalityMunicipality().getLocalityName())
        .designationOfficial(servicePointVersionModel.getDesignationOfficial())
        .versionValidFrom(servicePointVersionModel.getValidFrom())
        .versionValidTo(servicePointVersionModel.getValidTo())
        .build();
    List<StopPointClientPersonModel> examinants = new ArrayList<>(model.getExaminants());
    stopPointWorkflow.setExaminants(examinants.stream()
        .map(StopPointClientPersonMapper::toEntity)
        .collect(Collectors.toSet()));
    return stopPointWorkflow;
  }


  private ReadStopPointWorkflowModel reorderExaminants(ReadStopPointWorkflowModel stopPointWorkflowModel, List<StopPointClientPersonModel> importantPersons) {
    List<StopPointClientPersonModel> examinants = stopPointWorkflowModel.getExaminants();
    List<Person> importantPersonsForComparison = importantPersons.stream()
        .map(StopPointClientPersonMapper::toEntity)
        .collect(Collectors.toList());

    List<StopPointClientPersonModel> sortedExaminants = new ArrayList<>();

    for (Person importantPerson : importantPersonsForComparison) {
      examinants.stream()
          .filter(examinant ->
              Objects.equals(examinant.getFirstName(), importantPerson.getFirstName()) &&
                  Objects.equals(examinant.getLastName(), importantPerson.getLastName()) &&
                  Objects.equals(examinant.getMail(), importantPerson.getMail()) &&
                  Objects.equals(examinant.getOrganisation(), importantPerson.getOrganisation()) &&
                  Objects.equals(examinant.getPersonFunction(), importantPerson.getFunction())
          )
          .findFirst()
          .ifPresent(sortedExaminants::add);
    }

    examinants.stream()
        .filter(examinant -> !sortedExaminants.contains(examinant))
        .forEach(sortedExaminants::add);

    stopPointWorkflowModel.setExaminants(sortedExaminants);
    return stopPointWorkflowModel;
  }

}
