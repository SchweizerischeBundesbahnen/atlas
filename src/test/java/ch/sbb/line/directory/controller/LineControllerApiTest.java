package ch.sbb.line.directory.controller;

import static ch.sbb.line.directory.entity.LineVersion.Fields.alternativeName;
import static ch.sbb.line.directory.entity.LineVersion.Fields.businessOrganisation;
import static ch.sbb.line.directory.entity.LineVersion.Fields.combinationName;
import static ch.sbb.line.directory.entity.LineVersion.Fields.longName;
import static ch.sbb.line.directory.entity.LineVersion.Fields.paymentType;
import static ch.sbb.line.directory.entity.LineVersion.Fields.swissLineNumber;
import static ch.sbb.line.directory.entity.LineVersion.Fields.type;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.line.directory.WithMockJwtAuthentication;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@WithMockJwtAuthentication
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc(addFilters = false)
public class LineControllerApiTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LineController lineController;

  @Autowired
  private SublineController sublineController;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @Autowired
  private ObjectMapper mapper;

  private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      StandardCharsets.UTF_8);

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldUpdateLineVersion() throws Exception {
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
                        .swissLineNumber("b0.IC2")
                        .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    //when
    lineVersionModel.setBusinessOrganisation("PostAuto");
    mvc.perform(put("/v1/lines/versions/" + lineVersionSaved.getId().toString())
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
  void shouldCreateLineVersion() throws Exception {
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
  void shouldReturnLineRangeSmallerThanSublineRangeErrorResponse() throws Exception {
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
   sublineController.createSublineVersion(sublineVersionModel);

    //when
    lineVersionModel.setValidFrom(LocalDate.of(2000, 1, 2));
    mvc.perform(put("/v1/lines/versions/" + lineVersionSaved.getId().toString())
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel)))
       .andExpect(status().isPreconditionFailed())
       .andExpect(jsonPath("$.httpStatus", is(412)))
       .andExpect(jsonPath("$.message", is("A precondition fail occurred due to a business rule")))
       .andExpect(jsonPath("$.details[0].message",
           is("The line range 02.01.2000-31.12.2000 is smaller then the subline b0.Ic2-sibline range 01.01.2000-31.12.2000")))
       .andExpect(jsonPath("$.details[0].field", is("mainlineSlnid")))
       .andExpect(jsonPath("$.details[0].displayInfo.code",
           is("LIDI.SUBLINE.PRECONDITION.LINE_OUTSIDE_OF_LINE_RANGE")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("validFrom")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("02.01.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("validTo")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("31.12.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].key", is("swissSublineNumber")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].value", is("b0.Ic2-sibline")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].key", is("validFrom")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].value", is("01.01.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[4].key", is("validTo")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[4].value", is("31.12.2000")));
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

    //when
    lineVersionModel.setValidFrom(LocalDate.of(2000, 1, 2));
    mvc.perform(post("/v1/lines/versions/")
           .contentType(contentType)
           .content(mapper.writeValueAsString(lineVersionModel)))
       .andExpect(status().isConflict())
       .andExpect(jsonPath("$.httpStatus", is(409)))
       .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule")))
       .andExpect(jsonPath("$.details[0].message",
           is("SwissLineNumber b0.IC2-libne already taken from 01.01.2000 to 31.12.2000 by "+ lineVersionSaved.getSlnid())))
       .andExpect(jsonPath("$.details[0].field", is("swissLineNumber")))
       .andExpect(jsonPath("$.details[0].displayInfo.code",is("LIDI.LINE.CONFLICT.SWISS_NUMBER")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("swissLineNumber")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("b0.IC2-libne")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("validFrom")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("01.01.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].key", is("validTo")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[2].value", is("31.12.2000")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].key", is("slnid")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[3].value", is(lineVersionSaved.getSlnid())));
  }
}
