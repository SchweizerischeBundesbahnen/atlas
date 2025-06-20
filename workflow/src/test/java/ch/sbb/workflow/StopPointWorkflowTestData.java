package ch.sbb.workflow;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.DecisionType;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

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
                .ccEmails(List.of("a@b.ch", "b@c.it"))
                .versionId(versionId)
                .build();

        return workflowModel;
    }

    public static StopPointAddWorkflowModel getAddStopPointWorkflowWithMultipleExaminants() {
        List<StopPointClientPersonModel> stopPointClientPersonModels = new ArrayList<>();
        stopPointClientPersonModels.add(getClientPerson());
        stopPointClientPersonModels.add(getMunicipalityPerson());
        stopPointClientPersonModels.add(getAtlasPerson());

        long versionId = 123456L;
        String sloid = "ch:1:sloid:1234";
        StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
                .sloid(sloid)
                .ccEmails(List.of(MAIL_ADDRESS))
                .workflowComment("WF comment")
                .examinants(stopPointClientPersonModels)
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

    public static StopPointClientPersonModel getMunicipalityPerson(){
        StopPointClientPersonModel person = StopPointClientPersonModel.builder()
                .firstName("Name")
                .lastName("Lastname")
                .personFunction("Verkehrsplanung")
                .organisation("Amt für öffentlichen Verkehr, Bau-, Verkehrs- und Energiedirektion")
                .mail("TechSupport-ATLAS-fake@sbb.ch").build();
        return person;
    }

    public static StopPointClientPersonModel getAtlasPerson(){
        StopPointClientPersonModel person = StopPointClientPersonModel.builder()
                .firstName("atlas")
                .lastName("SKI")
                .personFunction("Fachstelle atlas")
                .organisation("SKI - Systemaufgaben Kundeninformation")
                .mail("testuser-atlas-fake@sbb.ch").build();
        return person;
    }

    public static Decision getDecisionWithExaminant(Person person) {
        return Decision.builder()
            .decisionType(DecisionType.VOTED)
            .examinant(person)
            .judgement(JudgementType.NO)
            .motivation("This is motivation")
            .build();
    }

    public static StopPointWorkflow getStopPointWorkflow() {
        long versionId = 654321L;
        String sloid = "ch:1:sloid:4321";
        return StopPointWorkflow.builder()
            .sloid(sloid)
            .ccEmails(List.of("a@b.ch", "b@c.it"))
            .workflowComment("Commentaros")
            .status(WorkflowStatus.HEARING)
            .designationOfficial("Designation official")
            .sboid("sboid")
            .versionId(versionId)
            .build();
    }

    public static Person getPerson() {
        return Person.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .function("Centrocampista")
            .organisation("BAV")
            .mail("email@example.com").build();
    }

}
