package ch.sbb.workflow;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
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
        List<ClientPersonModel> clientPersonModels = new ArrayList<>();
        clientPersonModels.add(getClientPerson());

        long versionId = 123456L;
        String sloid = "ch:1:sloid:1234";
        StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
                .sloid(sloid)
                .ccEmails(List.of(MAIL_ADDRESS))
                .workflowComment("WF comment")
                .examinants(clientPersonModels)
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
        List<ClientPersonModel> clientPersonModels = new ArrayList<>();
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


    public static ClientPersonModel getClientPerson(){
        ClientPersonModel person = ClientPersonModel.builder()
                .firstName("Marek")
                .lastName("Hamsik")
                .personFunction("Centrocampista")
                .mail(MAIL_ADDRESS).build();

        return person;
    }
}
