package ch.sbb.line.directory.controller;

import static ch.sbb.line.directory.entity.LineVersion.Fields.alternativeName;
import static ch.sbb.line.directory.entity.LineVersion.Fields.businessOrganisation;
import static ch.sbb.line.directory.entity.LineVersion.Fields.combinationName;
import static ch.sbb.line.directory.entity.LineVersion.Fields.longName;
import static ch.sbb.line.directory.entity.LineVersion.Fields.paymentType;
import static ch.sbb.line.directory.entity.LineVersion.Fields.swissLineNumber;
import static ch.sbb.line.directory.entity.LineVersion.Fields.type;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.line.directory.LineTestData;
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

public class LineControllerApiTest extends BaseControllerApiTest {

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
  void shouldUpdateLineVersion() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
                        .paymentType(PaymentType.LOCAL)
                        .swissLineNumber("b0.IC2")
                        .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    //when
    lineVersionModel.setBusinessOrganisation("PostAuto");
    mvc.perform(post("/v1/lines/versions/" + lineVersionSaved.getId().toString())
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel))
       ).andExpect(status().isOk())
       .andExpect(jsonPath("$[0]." + businessOrganisation, is("PostAuto")))
       .andExpect(jsonPath("$[0]." + alternativeName, is("alternative")))
       .andExpect(jsonPath("$[0]." + combinationName, is("combination")))
       .andExpect(jsonPath("$[0]." + longName, is("long name")))
       .andExpect(jsonPath("$[0]." + type, is(LineType.TEMPORARY.toString())))
       .andExpect(jsonPath("$[0]." + paymentType, is(PaymentType.LOCAL.toString())))
       .andExpect(jsonPath("$[0]." + swissLineNumber, is("b0.IC2")))
       .andExpect(jsonPath("$[0]." + businessOrganisation, is("PostAuto")));
  }


  @Test
  void shouldTrimAllWhiteSpacesInLineVersion() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
                    .businessOrganisation(" sbb")
                    .alternativeName("alternative ")
                    .combinationName(" combination ")
                    .longName("  long name  ")
                    .swissLineNumber("  b0.IC2       ")
                    .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    //when
    mvc.perform(post("/v1/lines/versions/" + lineVersionSaved.getId().toString())
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel))
       ).andExpect(status().isOk())
       .andExpect(jsonPath("$[0]." + businessOrganisation, is("sbb")))
       .andExpect(jsonPath("$[0]." + alternativeName, is("alternative")))
       .andExpect(jsonPath("$[0]." + combinationName, is("combination")))
       .andExpect(jsonPath("$[0]." + longName, is("long name")))
       .andExpect(jsonPath("$[0]." + swissLineNumber, is("b0.IC2")));
  }

  @Test
  void shouldCreateLineVersion() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
                        .validTo(LocalDate.of(2000, 12, 31))
                        .validFrom(LocalDate.of(2000, 1, 1))
                        .businessOrganisation("sbb")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
                        .paymentType(PaymentType.LOCAL)
                        .swissLineNumber("b0.IC5")
                        .build();
    lineController.createLineVersion(lineVersionModel);
    //when
    lineVersionModel.setSwissLineNumber("b0.IC3");
    mvc.perform(post("/v1/lines/versions/")
        .contentType(contentType)
        .content(mapper.writeValueAsString(lineVersionModel))
    ).andExpect(status().isCreated());
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
                        .type(LineType.TEMPORARY)
                        .paymentType(PaymentType.LOCAL)
                        .swissLineNumber("b0.IC2-libne")
                        .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);

    //when
    lineVersionModel.setValidFrom(LocalDate.of(2000, 1, 2));
    mvc.perform(post("/v1/lines/versions/")
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel)))
       .andExpect(status().isConflict())
       .andExpect(jsonPath("$.status", is(409)))
       .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule")))
       .andExpect(jsonPath("$.error", is("Line conflict")))
       .andExpect(jsonPath("$.details[0].message",
           is("SwissLineNumber b0.IC2-libne already taken from 01.01.2000 to 31.12.2000 by "
               + lineVersionSaved.getSlnid())))
       .andExpect(jsonPath("$.details[0].field", is("swissLineNumber")))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.LINE.CONFLICT.SWISS_NUMBER")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("swissLineNumber")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("b0.IC2-libne")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("validFrom")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("01.01.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].key", is("validTo")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].value", is("31.12.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].key", is("slnid")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].value",
           is(lineVersionSaved.getSlnid())));
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
                        .type(LineType.TEMPORARY)
                        .paymentType(PaymentType.LOCAL)
                        .swissLineNumber("b0.IC2-libne")
                        .build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);

    // When first update it is ok
    lineVersionModel.setComment("New and hot line, ready to roll");
    mvc.perform(post("/v1/lines/versions/" + lineVersionModel.getId())
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel)))
       .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    lineVersionModel.setComment("New and hot line, ready to rock");
    MvcResult mvcResult = mvc.perform(post("/v1/lines/versions/" + lineVersionModel.getId())
                                 .contentType(contentType)
                                 .content(mapper.writeValueAsString(lineVersionModel)))
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
    LineVersionModel firstLineVersionModel =
        LineTestData.lineVersionModelBuilder()
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
    firstLineVersionModel = lineController.createLineVersion(firstLineVersionModel);
    LineVersionModel secondLineVersionModel =
        LineTestData.lineVersionModelBuilder()
                        .validTo(LocalDate.of(2001, 12, 31))
                        .validFrom(LocalDate.of(2001, 1, 1))
                        .businessOrganisation("BLS")
                        .alternativeName("alternative")
                        .combinationName("combination")
                        .longName("long name")
                        .type(LineType.TEMPORARY)
                        .paymentType(PaymentType.LOCAL)
                        .swissLineNumber("b0.IC2-libne")
                        .build();
    lineController.createLineVersion(secondLineVersionModel);

    //when & then
    mvc.perform(post("/v1/lines/versions/" + firstLineVersionModel.getId())
           .contentType(contentType)
           .content(mapper.writeValueAsString(firstLineVersionModel)))
       .andExpect(jsonPath("$.status", is(520)))
       .andExpect(jsonPath("$.message", is("No entities were modified after versioning execution.")))
       .andExpect(jsonPath("$.error", is("No changes after versioning")))
       .andExpect(jsonPath("$.details[0].message", is("No entities were modified after versioning execution.")))
       .andExpect(jsonPath("$.details[0].field", is(nullValue())))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.WARNING.VERSIONING_NO_CHANGES")));
  }

  @Test
  void shouldReturnNotFoundErrorResponse() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
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

    //when
    mvc.perform(post("/v1/lines/versions/123")
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel)))
       .andExpect(status().isNotFound())
       .andExpect(jsonPath("$.status", is(404)))
       .andExpect(jsonPath("$.message", is("Entity not found")))
       .andExpect(jsonPath("$.error", is("Not found")))
       .andExpect(jsonPath("$.details[0].message", is("Object with id 123 not found")))
       .andExpect(jsonPath("$.details[0].field", is("id")))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("id")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
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
}
