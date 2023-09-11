package ch.sbb.line.directory.controller;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.lidi.LineVersionModel.Fields;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.CoverageType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.ModelType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerWithAmazonS3ApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.repository.CoverageRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.export.SublineVersionExportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SublineControllerApiTest extends BaseControllerWithAmazonS3ApiTest {

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
  private CoverageRepository coverageRepository;

  @Autowired
  private SublineVersionExportService sublineVersionExportService;

  @MockBean
  private AmazonService amazonService;

  @AfterEach
  public void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
    coverageRepository.deleteAll();
  }

  @Test
  void shouldCreateSubline() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .build();
    //when
    lineVersionModel.setValidFrom(LocalDate.of(2000, 1, 2));
    mvc.perform(post("/v1/sublines/versions")
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldGetSublineOverview() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionSaved.getSlnid())
        .build();
    sublineController.createSublineVersion(sublineVersionModel);

    //when
    mvc.perform(get("/v1/sublines")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "swissSublineNumber,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldGetSublineCoverage() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .build();
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        sublineVersionModel);
    //when
    mvc.perform(get("/v1/sublines/subline-coverage/" + sublineVersionSaved.getSlnid())
            .contentType(contentType)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.slnid", is(sublineVersionSaved.getSlnid())))
        .andExpect(jsonPath("$.modelType", is(ModelType.SUBLINE.toString())))
        .andExpect(jsonPath("$.coverageType", is(CoverageType.COMPLETE.toString())))
        .andExpect(jsonPath("$.validFrom", is("2000-01-01")))
        .andExpect(jsonPath("$.validTo", is("2000-12-31")))
        .andExpect(jsonPath("$.validationErrorType", is(nullValue())));
  }

  @Test
  void shouldReturnConflictErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .build();
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        sublineVersionModel);

    //when
    mvc.perform(post("/v1/sublines/versions")
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)))
        .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule")))
        .andExpect(jsonPath("$.error", is("Subline conflict")))
        .andExpect(jsonPath("$.details[0].message",
            is("SwissSublineNumber b0.Ic2-sibline already taken from 01.01.2000 to 31.12.2000 by "
                + sublineVersionSaved.getSlnid())))
        .andExpect(jsonPath("$.details[0].field", is("swissSublineNumber")))
        .andExpect(
            jsonPath("$.details[0].displayInfo.code", is("LIDI.SUBLINE.CONFLICT.SWISS_NUMBER")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("swissSublineNumber")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("b0.Ic2-sibline")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("validFrom")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("01.01.2000")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].key", is("validTo")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].value", is("31.12.2000")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].key", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].value",
            is(sublineVersionSaved.getSlnid())));
  }

  @Test
  void shouldReturnSubLineAssignToLineConflictErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    LineVersionModel changedLineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne-changed")
            .build();
    LineVersionModel changedlineVersionSaved = lineController.createLineVersion(
        changedLineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .build();
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(sublineVersionModel);

    //when
    sublineVersionSaved.setSwissSublineNumber("another");
    sublineVersionSaved.setMainlineSlnid(changedlineVersionSaved.getSlnid());
    mvc.perform(post("/v1/sublines/versions/" + sublineVersionSaved.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionSaved)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)))
        .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule")))
        .andExpect(jsonPath("$.error", is("Subline conflict")))
        .andExpect(jsonPath("$.details[0].message",
            is("The mainline " + sublineVersionModel.getMainlineSlnid() + " cannot be changed")))
        .andExpect(jsonPath("$.details[0].field", is("mainlineSlnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code",
            is("LIDI.SUBLINE.CONFLICT.ASSIGN_DIFFERENT_LINE_CONFLICT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("mainlineSlnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value",
            is(sublineVersionModel.getMainlineSlnid())));
  }

  @Test
  void shouldReturnSublineOutsideOfLineRange() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2001, 1, 1))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionSaved.getSlnid())
            .build();

    //when
    mvc.perform(post("/v1/sublines/versions")
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionModel.getSlnid())
            .build();
    sublineVersionModel = sublineController.createSublineVersion(sublineVersionModel);

    // When first update it is ok
    sublineVersionModel.setDescription("Kinky subline, ready to roll");
    mvc.perform(post("/v1/sublines/versions/" + sublineVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    sublineVersionModel.setDescription("Kinky subline, ready to rock");
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/versions/" + sublineVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isPreconditionFailed()).andReturn();

    ErrorResponse errorResponse = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getDetails()).size().isEqualTo(1);
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
    assertThat(errorResponse.getError()).isEqualTo("Stale object state error");
  }

  @Test
  void shouldReturnValidationNoChangesErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2001, 12, 31))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.ORDERLY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2-libne")
            .build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel firstSublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionModel.getSlnid())
            .build();
    firstSublineVersionModel = sublineController.createSublineVersion(firstSublineVersionModel);

    SublineVersionModel secondSublineVersionModel =
        SublineVersionModel.builder()
            .validFrom(LocalDate.of(2001, 1, 1))
            .validTo(LocalDate.of(2001, 12, 31))
            .businessOrganisation("bls")
            .swissSublineNumber("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .paymentType(PaymentType.LOCAL)
            .mainlineSlnid(lineVersionModel.getSlnid())
            .build();
    sublineController.createSublineVersion(secondSublineVersionModel);

    //when & then
    mvc.perform(post("/v1/sublines/versions/" + firstSublineVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(firstSublineVersionModel)))
        .andExpect(jsonPath("$.status", is(520)))
        .andExpect(
            jsonPath("$.message", is("No entities were modified after versioning execution.")))
        .andExpect(jsonPath("$.error", is("No changes after versioning")))
        .andExpect(jsonPath("$.details[0].message",
            is("No entities were modified after versioning execution.")))
        .andExpect(jsonPath("$.details[0].field", is(nullValue())))
        .andExpect(
            jsonPath("$.details[0].displayInfo.code", is("ERROR.WARNING.VERSIONING_NO_CHANGES")));
  }

  @Test
  void shouldReturnNotFoundErrorResponseWhenNoFoundLines() throws Exception {
    //when
    mvc.perform(get("/v1/sublines/versions/123")
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
  void shouldReturnOptimisticLockingOnBusinessObjectChanges() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel =
        SublineTestData.sublineVersionModelBuilder()
            .mainlineSlnid(lineVersionModel.getSlnid())
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .build();
    sublineVersionModel = sublineController.createSublineVersion(sublineVersionModel);

    // When first update it is ok
    sublineVersionModel.setValidFrom(LocalDate.of(2010, 1, 1));
    sublineVersionModel.setValidTo(LocalDate.of(2010, 12, 31));
    mvc.perform(post("/v1/sublines/versions/" + sublineVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    sublineVersionModel.setValidFrom(LocalDate.of(2000, 1, 1));
    sublineVersionModel.setValidTo(LocalDate.of(2011, 12, 31));
    mvc.perform(post("/v1/sublines/versions/" + sublineVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isPreconditionFailed());
  }

  @Test
  void shouldExportFullSublineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel1 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    SublineVersionModel sublineVersionModel2 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("desc2")
        .build();
    sublineController.createSublineVersion(sublineVersionModel1);
    sublineController.createSublineVersion(sublineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportActualSublineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel1 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    SublineVersionModel sublineVersionModel2 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.now()
            .withMonth(1)
            .withDayOfMonth(
                1))
        .validTo(LocalDate.now()
            .withMonth(12)
            .withDayOfMonth(31))
        .description("desc2")
        .build();
    sublineController.createSublineVersion(sublineVersionModel1);
    sublineController.createSublineVersion(sublineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/export-csv/actual"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportFutureTimetableLineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel1 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    SublineVersionModel sublineVersionModel2 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.now()
            .withMonth(1)
            .withDayOfMonth(
                1))
        .validTo(LocalDate.now()
            .withMonth(12)
            .withDayOfMonth(31))
        .description("desc2")
        .build();
    sublineController.createSublineVersion(sublineVersionModel1);
    sublineController.createSublineVersion(sublineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/export-csv/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldRevokeSubline() throws Exception {
    //given
    LineVersionModel lineVersionModel = lineController.createLineVersion(
        LineTestData.lineVersionModelBuilder().build());
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        SublineTestData.sublineVersionModelBuilder()
            .mainlineSlnid(
                lineVersionModel.getSlnid())
            .build());

    //when
    mvc.perform(post("/v1/sublines/" + sublineVersionSaved.getSlnid() + "/revoke")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.status, is("REVOKED")));
  }
}
