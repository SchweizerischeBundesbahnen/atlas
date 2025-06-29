package ch.sbb.workflow.sepodi.hearing.mapper;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowMapper {

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
      ReadServicePointVersionModel servicePointVersionModel,
      List<StopPointClientPersonModel> examinants) {
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
    examinants.addAll(model.getExaminants());
    stopPointWorkflow.setExaminants(
        examinants.stream().map(StopPointClientPersonMapper::toEntity)
            .collect(Collectors.toSet()));
    return stopPointWorkflow;
  }

}
