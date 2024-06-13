package ch.sbb.workflow;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StopPointWorkflowTestData {

    static final String MAIL_ADDRESS = "marek@hamsik.com";

    public static StopPointAddWorkflowModel getAddStopPointWorkflow1() {
        List<StopPointClientPersonModel> stopPointClientPersonModels = new ArrayList<>();
        stopPointClientPersonModels.add(getClientPerson());

        long versionId = 123456L;
        String sloid = "ch:1:sloid:1234";
        StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
                .sloid(sloid)
                .ccEmails(List.of(MAIL_ADDRESS))
                .workflowComment("WF comment")
                .examinants(stopPointClientPersonModels)
                .designationOfficial("Test")
                .localityName("BERN")
                .versionValidFrom(LocalDate.of(2020, 03, 01))
                .createdAt(LocalDateTime.of(LocalDate.of(2020, 01, 01), LocalTime.of(15, 43, 22)))
                .ccEmails(List.of("a@b.ch", "b@c.it"))
                .versionId(versionId)
                .build();

        return workflowModel;
    }

    public static StopPointAddWorkflowModel getAddStopPointWorkflow2() {
        List<StopPointClientPersonModel> clientPersonModels = new ArrayList<>();
        clientPersonModels.add(getClientPerson());

        long versionId = 654321L;
        String sloid = "ch:1:sloid:4321";
        StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
                .sloid(sloid)
                .ccEmails(List.of(MAIL_ADDRESS))
                .workflowComment("Commentaros")
                .examinants(clientPersonModels)
                .designationOfficial("Test")
                .localityName("ZURICH")
                .versionValidFrom(LocalDate.of(2015, 03, 01))
                .createdAt(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(9, 26, 29)))
                .ccEmails(List.of("a@b.ch", "b@c.it"))
                .versionId(versionId)
                .build();

        return workflowModel;
    }


    public static StopPointClientPersonModel getClientPerson(){
        StopPointClientPersonModel person = StopPointClientPersonModel.builder()
                .firstName("Marek")
                .lastName("Hamsik")
                .personFunction("Centrocampista")
                .organisation("BAV")
                .mail(MAIL_ADDRESS).build();
        return person;
    }
}
