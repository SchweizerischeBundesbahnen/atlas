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
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.lidi.LineVersionSnapshotModel;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerWithAmazonS3ApiTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionSnapshotRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.export.LineVersionExportService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

class LineControllerApiTest extends BaseControllerWithAmazonS3ApiTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private LineController lineController;

  @Autowired
  private SublineController sublineController;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @Autowired
  private LineVersionExportService lineVersionExportService;

  @Autowired
  private LineVersionSnapshotRepository lineVersionSnapshotService;

  @MockBean
  private AmazonService amazonService;

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
    lineVersionSnapshotService.deleteAll();
  }

  @Test
  void shouldGetLineOverview() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineController.createLineVersion(lineVersionModel);

    //when
    mvc.perform(get("/v1/lines")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "swissLineNumber,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldExportFullLineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel1 = LineTestData.lineVersionModelBuilder().build();
    LineVersionModel lineVersionModel2 = LineTestData.lineVersionModelBuilder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("descriptiön2")
        .build();
    lineController.createLineVersion(lineVersionModel1);
    lineController.createLineVersion(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/lines/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportActualLineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel1 = LineTestData.lineVersionModelBuilder().build();
    LineVersionModel lineVersionModel2 = LineTestData.lineVersionModelBuilder()
        .validFrom(LocalDate.now()
            .withMonth(1)
            .withDayOfMonth(1))
        .validTo(LocalDate.now()
            .withMonth(12)
            .withDayOfMonth(31))
        .description("desc2")
        .build();
    lineController.createLineVersion(lineVersionModel1);
    lineController.createLineVersion(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/lines/export-csv/actual"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportFutureTimetableLineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel1 = LineTestData.lineVersionModelBuilder().build();
    LineVersionModel lineVersionModel2 = LineTestData.lineVersionModelBuilder()
        .validFrom(LocalDate.now()
            .withMonth(1)
            .withDayOfMonth(1))
        .validTo(LocalDate.now()
            .withMonth(12)
            .withDayOfMonth(31))
        .description("desc2")
        .build();
    lineController.createLineVersion(lineVersionModel1);
    lineController.createLineVersion(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/lines/export-csv/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldRevokeLine() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    //when
    mvc.perform(post("/v1/lines/" + lineVersionSaved.getSlnid() + "/revoke")
    ).andExpect(status().isOk());

    List<LineVersionModel> lineVersions = lineController.getLineVersions(lineVersionSaved.getSlnid());
    assertThat(lineVersions).hasSize(1).first().extracting("status").isEqualTo(Status.REVOKED);
  }

  @Test
  void shouldReturnLineDeleteConflictErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.DISPOSITION)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber(null)
            .build();

    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .description("b0.Ic2-sibline")
            .sublineType(SublineType.DISPOSITION)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .build();
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        sublineVersionModel);

    //when
    mvc.perform(delete("/v1/lines/" + lineVersionSaved.getSlnid())
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
  void shouldReturnNotFoundErrorResponseWhenNoFoundLines() throws Exception {
    //when
    mvc.perform(get("/v1/lines/versions/123")
            .contentType(contentType))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with slnid 123 not found")))
        .andExpect(jsonPath("$.details[0].field", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
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
    mvc.perform(get("/v1/lines/workflows")
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
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.ORDERLY)
            .swissLineNumber("sln")
            .paymentType(PaymentType.LOCAL)
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);

    //when
    mvc.perform(post("/v1/lines/versions/" + lineVersionSaved.getId().toString() + "/skip-workflow")
    ).andExpect(status().isOk());

    //then
    List<LineVersionModel> lineVersions = lineController.getLineVersions(lineVersionSaved.getSlnid());
    assertThat(lineVersions).hasSize(1);
    assertThat(lineVersions.get(0).getStatus()).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldReturnBadRequestWhenPageSizeExceeded() throws Exception {
    mvc.perform(get("/v1/lines?size=5000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("The page size is limited to 2000")));
  }
}
