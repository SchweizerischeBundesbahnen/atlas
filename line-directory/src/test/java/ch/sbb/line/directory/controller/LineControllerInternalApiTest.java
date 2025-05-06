package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.lidi.CreateSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.LineVersionSnapshotModel;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionSnapshotRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@MockitoBean(types = {SharedBusinessOrganisationService.class, AmazonService.class})
class LineControllerInternalApiTest extends BaseControllerApiTest {

  private final LineControllerV2 lineControllerV2;
  private final SublineControllerV2 sublineControllerV2;
  private final LineVersionRepository lineVersionRepository;
  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionSnapshotRepository lineVersionSnapshotService;

  @Autowired
  LineControllerInternalApiTest(
      LineControllerV2 lineControllerV2,
      SublineControllerV2 sublineControllerV2,
      LineVersionRepository lineVersionRepository,
      SublineVersionRepository sublineVersionRepository,
      LineVersionSnapshotRepository lineVersionSnapshotService
  ) {
    this.lineControllerV2 = lineControllerV2;
    this.sublineControllerV2 = sublineControllerV2;
    this.lineVersionRepository = lineVersionRepository;
    this.sublineVersionRepository = sublineVersionRepository;
    this.lineVersionSnapshotService = lineVersionSnapshotService;
  }

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
    lineVersionSnapshotService.deleteAll();
  }

  @Test
  void shouldExportFullLineVersionsCsv() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel1 = LineTestData.createLineVersionModelBuilder().build();
    LineVersionModelV2 lineVersionModel2 = LineTestData.createLineVersionModelBuilder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("descriptiön2")
        .build();
    lineControllerV2.createLineVersionV2(lineVersionModel1);
    lineControllerV2.createLineVersionV2(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/internal/lines/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportActualLineVersionsCsv() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel1 = LineTestData.createLineVersionModelBuilder().build();
    LineVersionModelV2 lineVersionModel2 = LineTestData.createLineVersionModelBuilder()
        .validFrom(LocalDate.now()
            .withMonth(1)
            .withDayOfMonth(1))
        .validTo(LocalDate.now()
            .withMonth(12)
            .withDayOfMonth(31))
        .description("desc2")
        .build();
    lineControllerV2.createLineVersionV2(lineVersionModel1);
    lineControllerV2.createLineVersionV2(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/internal/lines/export-csv/actual"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportFutureTimetableLineVersionsCsv() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel1 = LineTestData.createLineVersionModelBuilder().build();
    LineVersionModelV2 lineVersionModel2 = LineTestData.createLineVersionModelBuilder()
        .validFrom(LocalDate.now()
            .withMonth(1)
            .withDayOfMonth(1))
        .validTo(LocalDate.now()
            .withMonth(12)
            .withDayOfMonth(31))
        .description("desc2")
        .build();
    lineControllerV2.createLineVersionV2(lineVersionModel1);
    lineControllerV2.createLineVersionV2(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/internal/lines/export-csv/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldRevokeLine() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    LineVersionModelV2 lineVersionSaved = lineControllerV2.createLineVersionV2(lineVersionModel);

    //when
    mvc.perform(post("/internal/lines/" + lineVersionSaved.getSlnid() + "/revoke"))
        .andExpect(status().isOk());

    List<LineVersionModelV2> lineVersions = lineControllerV2.getLineVersionsV2(lineVersionSaved.getSlnid());
    assertThat(lineVersions).hasSize(1).first().extracting("status").isEqualTo(Status.REVOKED);
  }

  @Test
  void shouldReturnLineDeleteConflictErrorResponse() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel =
        LineTestData.createLineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .longName("long name")
            .build();

    LineVersionModelV2 lineVersionSaved = lineControllerV2.createLineVersionV2(lineVersionModel);
    CreateSublineVersionModelV2 sublineVersionModel =
        CreateSublineVersionModelV2.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .description("b0.Ic2-sibline")
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .sublineType(SublineType.TECHNICAL)
            .build();
    SublineVersionModelV2 sublineVersionSaved = sublineControllerV2.createSublineVersionV2(sublineVersionModel);

    //when
    mvc.perform(delete("/internal/lines/" + lineVersionSaved.getSlnid())
            .contentType(contentType)
            .content(mapper.writeValueAsString(lineVersionModel)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)))
        .andExpect(jsonPath("$.message", is("A line related to a subline cannot be deleted.")))
        .andExpect(jsonPath("$.error", is("Line delete conflict")))
        .andExpect(jsonPath("$.details[0].message",
            is("Line with SLNID " + lineVersionSaved.getSlnid() + " is related to Subline SLNID "
                + sublineVersionSaved.getSlnid())))
        .andExpect(jsonPath("$.details[0].field", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.LINE.CONFLICT.DELETE")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value",
            is(lineVersionSaved.getSlnid())))
        .andExpect(
            jsonPath("$.details[0].displayInfo.parameters[1].key", is("SublineVersion.slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value",
            is(sublineVersionSaved.getSlnid())));
  }

  @Test
  void shouldGetLineVersionSnaphots() throws Exception {
    //given
    LineVersionSnapshot lineVersionSnapshot = LineVersionSnapshot.builder()
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.COLLECTION_LINE)
        .offerCategory(OfferCategory.IC)
        .shortNumber("asd")
        .workflowStatus(WorkflowStatus.STARTED)
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.now())
        .editionDate(LocalDateTime.now())
        .editor("Marek")
        .creator("Hamsik")
        .businessOrganisation("businessOrganisation")
        .comment("comment")
        .workflowId(123L)
        .version(1)
        .parentObjectId(123L)
        .description("b0.IC2")
        .swissLineNumber("swissLineNumber")
        .slnid("b0.IC2")
        .build();

    lineVersionSnapshotService.save(lineVersionSnapshot);
    //when
    mvc.perform(get("/internal/lines/workflows")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "swissLineNumber,asc")
            .contentType(contentType)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects", hasSize(1)))
        .andExpect(jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.longName, is("longName")))
        .andExpect(jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.lineType, is(LineType.ORDERLY.toString())))
        .andExpect(
            jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.paymentType, is(PaymentType.INTERNATIONAL.toString())))
        .andExpect(jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.description, is("b0.IC2")))
        .andExpect(jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.workflowId, is(123)))
        .andExpect(jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.parentObjectId, is(123)))
        .andExpect(jsonPath("$.objects.[0]." + LineVersionSnapshotModel.Fields.etagVersion, is(1)));

  }

  @Test
  void shouldSkipWorkflowOnLineVersion() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel =
        LineTestData.createLineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .longName("long name")
            .lineType(LineType.ORDERLY)
            .swissLineNumber("sln")
            .build();
    LineVersionModelV2 lineVersionSaved = lineControllerV2.createLineVersionV2(lineVersionModel);

    //when
    mvc.perform(post("/internal/lines/versions/" + lineVersionSaved.getId().toString() + "/skip-workflow"))
        .andExpect(status().isOk());

    //then
    List<LineVersionModelV2> lineVersions = lineControllerV2.getLineVersionsV2(lineVersionSaved.getSlnid());
    assertThat(lineVersions).hasSize(1);
    assertThat(lineVersions.get(0).getStatus()).isEqualTo(Status.VALIDATED);
  }

}
