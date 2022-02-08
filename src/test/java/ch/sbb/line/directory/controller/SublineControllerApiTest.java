package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class SublineControllerApiTest extends BaseControllerApiTest {

  @Autowired
  private LineController lineController;

  @Autowired
  private SublineController sublineController;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @AfterEach
  public void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }


  @Test
  void shouldCreateSubline() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineVersionModel.builder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
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
                           .type(SublineType.TECHNICAL)
                           .paymentType(PaymentType.LOCAL)
                           .mainlineSlnid(lineVersionSaved.getSlnid())
                           .build();
    //when
    lineVersionModel.setValidFrom(LocalDate.of(2000, 1, 2));
    mvc.perform(post("/v1/sublines/versions/")
           .contentType(contentType)
           .content(mapper.writeValueAsString(sublineVersionModel)))
       .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnConflictErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineVersionModel.builder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
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
                           .type(SublineType.TECHNICAL)
                           .paymentType(PaymentType.LOCAL)
                           .mainlineSlnid(lineVersionSaved.getSlnid())
                           .build();
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        sublineVersionModel);

    //when
    mvc.perform(post("/v1/sublines/versions/")
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
        LineVersionModel.builder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
                        .paymentType(PaymentType.LOCAL)
                        .swissLineNumber("b0.IC2-libne")
                        .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    LineVersionModel changedLineVersionModel =
        LineVersionModel.builder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
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
                           .type(SublineType.TECHNICAL)
                           .paymentType(PaymentType.LOCAL)
                           .mainlineSlnid(lineVersionSaved.getSlnid())
                           .build();
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        sublineVersionModel);

    //when
    sublineVersionModel.setMainlineSlnid(changedlineVersionSaved.getSlnid());
    mvc.perform(post("/v1/sublines/versions/" + sublineVersionSaved.getId())
           .contentType(contentType)
           .content(mapper.writeValueAsString(sublineVersionModel)))
       .andExpect(status().isConflict())
       .andExpect(jsonPath("$.status", is(409)))
       .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule")))
       .andExpect(jsonPath("$.error", is("Subline conflict")))
       .andExpect(jsonPath("$.details[0].message",
           is("The mainline " + sublineVersionSaved.getMainlineSlnid() + " cannot be changed")))
       .andExpect(jsonPath("$.details[0].field", is("mainlineSlnid")))
       .andExpect(jsonPath("$.details[0].displayInfo.code",
           is("LIDI.SUBLINE.CONFLICT.ASSIGN_DIFFERENT_LINE_CONFLICT")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("mainlineSlnid")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value",
           is(sublineVersionSaved.getMainlineSlnid())));
  }

  @Test
  void shouldReturnSublineOutsideOfLineRange() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineVersionModel.builder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
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
                           .type(SublineType.TECHNICAL)
                           .paymentType(PaymentType.LOCAL)
                           .mainlineSlnid(lineVersionSaved.getSlnid())
                           .build();

    //when
    mvc.perform(post("/v1/sublines/versions/")
           .contentType(contentType)
           .content(mapper.writeValueAsString(sublineVersionModel)))
       .andExpect(status().isPreconditionFailed())
       .andExpect(jsonPath("$.status", is(412)))
       .andExpect(jsonPath("$.message", is("A precondition fail occurred due to a business rule")))
       .andExpect(jsonPath("$.error", is("Subline outside of the line range")))
       .andExpect(jsonPath("$.details[0].message",
           is("The subline range 01.01.2000-01.01.2001 is outside of the line b0.IC2-libne range 01.01.2000-31.12.2000")))
       .andExpect(jsonPath("$.details[0].field", is("mainlineSlnid")))
       .andExpect(jsonPath("$.details[0].displayInfo.code",
           is("LIDI.SUBLINE.PRECONDITION.SUBLINE_OUTSIDE_OF_LINE_RANGE")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("validFrom")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("01.01.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("validTo")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("01.01.2001")))
       .andExpect(
           jsonPath("$.details[0].displayInfo.parameters[2].key", is("mainline.swissLineNumber")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].value", is("b0.IC2-libne")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].key", is("mainline.validFrom")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].value", is("01.01.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[4].key", is("mainline.validTo")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[4].value", is("31.12.2000")));
  }

  @Test
  void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineVersionModel.builder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
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
                           .type(SublineType.TECHNICAL)
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
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getCode()).isEqualTo(
        "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
    assertThat(errorResponse.getError()).isEqualTo("Stale object state error");
  }

  @Test
  void shouldReturnValidationNoChangesErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineVersionModel.builder()
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .validTo(LocalDate.of(2001, 12, 31))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.ORDERLY)
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
                           .type(SublineType.TECHNICAL)
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
                           .type(SublineType.TECHNICAL)
                           .paymentType(PaymentType.LOCAL)
                           .mainlineSlnid(lineVersionModel.getSlnid())
                           .build();
    sublineController.createSublineVersion(secondSublineVersionModel);


    //when & then
    mvc.perform(post("/v1/sublines/versions/" + firstSublineVersionModel.getId())
           .contentType(contentType)
           .content(mapper.writeValueAsString(firstSublineVersionModel)))
       .andExpect(jsonPath("$.status", is(520)))
       .andExpect(jsonPath("$.message", is("No entities were modified after versioning execution.")))
       .andExpect(jsonPath("$.error", is("No changes after versioning")))
       .andExpect(jsonPath("$.details[0].message", is("No entities were modified after versioning execution.")))
       .andExpect(jsonPath("$.details[0].field", is(nullValue())))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.WARNING.VERSIONING_NO_CHANGES")));
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
}
