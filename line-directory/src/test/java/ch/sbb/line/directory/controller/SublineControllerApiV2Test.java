package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.CreateSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class SublineControllerApiV2Test extends BaseControllerApiTest {

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private SublineControllerV2 sublineController;

  private LineVersion mainLineVersion;

  @BeforeEach
  void setUp() {
    LineVersion lineVersion = LineTestData.lineVersionV2Builder().build();
    mainLineVersion = lineVersionRepository.saveAndFlush(lineVersion);
  }

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSublineVersion() throws Exception {
    //given
    SublineVersion sublineVersion = SublineTestData.sublineVersionV2Builder()
        .mainlineSlnid(mainLineVersion.getSlnid())
        .build();
    sublineVersionRepository.saveAndFlush(sublineVersion);

    //when
    mvc.perform(get("/v2/sublines/versions/" + sublineVersion.getSlnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldReturnNotFound() throws Exception {
    mvc.perform(get("/v2/sublines/versions/ch:1:slnid:123"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateSublineV2() throws Exception {
    //given
    CreateSublineVersionModelV2 sublineVersionModel =
        CreateSublineVersionModelV2.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .description("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .mainlineSlnid(mainLineVersion.getSlnid())
            .build();
    //when
    mvc.perform(post("/v2/sublines/versions")
            .contentType(contentType)
            .content(mapper.writeValueAsString(sublineVersionModel)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateSubline() throws Exception {
    //given
    CreateSublineVersionModelV2 sublineVersionModel =
        CreateSublineVersionModelV2.builder()
            .validFrom(LocalDate.of(2000, 1, 1))
            .validTo(LocalDate.of(2000, 12, 31))
            .businessOrganisation("sbb")
            .description("b0.Ic2-sibline")
            .sublineType(SublineType.TECHNICAL)
            .mainlineSlnid(mainLineVersion.getSlnid())
            .build();
    ReadSublineVersionModelV2 result = sublineController.createSublineVersionV2(sublineVersionModel);

    // When first update it is ok
    SublineVersionModelV2 updateModel = SublineVersionModelV2.builder()
        .id(result.getId())
        .validFrom(result.getValidFrom())
        .validTo(result.getValidTo())
        .businessOrganisation(result.getBusinessOrganisation())
        .mainlineSlnid(result.getMainlineSlnid())
        .etagVersion(result.getEtagVersion())
        .build();
    updateModel.setDescription("Kinky subline, ready to roll");
    mvc.perform(put("/v2/sublines/versions/" + updateModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateModel)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    updateModel.setDescription("Kinky subline, ready to rock");
    mvc.perform(put("/v2/sublines/versions/" + updateModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateModel)))
        .andExpect(status().isPreconditionFailed()).andReturn();

  }
 // todo: update frontend
}
