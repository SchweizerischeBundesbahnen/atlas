package ch.sbb.line.directory.module.line.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.node.ObjectNode;

@MockitoBean(types = SharedBusinessOrganisationService.class)
class LineControllerV3WriteApiTest extends BaseControllerApiTest {

  private static final String VERSIONS_URL = "/v3/lines/versions";
  private static final String SBOID = "ch:1:sboid:1100048";
  private static final String SLNID_PATTERN = "ch:1:slnid:\\d+";
  private static final List<String> READ_ONLY_FIELDS = List.of("id", "slnid", "type", "lineType", "referencedLine", "status",
      "creationDate", "creator", "editionDate", "editor");

  private final LineVersionV3Repository lineVersionV3Repository;

  @Autowired
  LineControllerV3WriteApiTest(LineVersionV3Repository lineVersionV3Repository) {
    this.lineVersionV3Repository = lineVersionV3Repository;
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
  }

  @Test
  void shouldCreateOrderlyLine() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("$.slnid", matchesPattern(SLNID_PATTERN)))
        .andExpect(jsonPath("$.status", is(Status.VALIDATED.name())))
        .andExpect(jsonPath("$.type", is(LineObjectType.LINE.name())))
        .andExpect(jsonPath("$.lineType", is(LineType.ORDERLY.name())))
        .andExpect(jsonPath("$.referencedLine", is(nullValue())));
    List<LineVersionV3> versions = lineVersionV3Repository.findAll();
    assertThat(versions).hasSize(1);
    assertThat(versions.getFirst().getSlnid()).matches(SLNID_PATTERN);
    assertThat(versions.getFirst().getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(versions.getFirst().getReferencedLineSlnid()).isNull();
    assertThat(versions.getFirst().getSwissLineNumber()).isEqualTo("90.100");
  }

  @Test
  void shouldCreateTechnicalMemberWithResolvedReferencedLine() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    CreateLineVersionModelV3 model = memberModel(LineType.TECHNICAL).build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("$.type", is(LineObjectType.SUBLINE.name())))
        .andExpect(jsonPath("$.lineType", is(LineType.TECHNICAL.name())))
        .andExpect(jsonPath("$.referencedLine.lineSlnid", is(LineVersionV3TestData.PARENT_SLNID)))
        .andExpect(jsonPath("$.slnid", matchesPattern(SLNID_PATTERN)));
    LineVersionV3 member = findMember();
    assertThat(member.getReferencedLineSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
    assertThat(member.getLineType()).isEqualTo(LineType.TECHNICAL);
    assertThat(member.getSlnid()).isNotEqualTo(LineVersionV3TestData.PARENT_SLNID);
  }

  @Test
  void shouldCreateConcessionMemberWithResolvedReferencedLine() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    CreateLineVersionModelV3 model = memberModel(LineType.CONCESSION)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .swissLineNumber("90.583.7")
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("$.type", is(LineObjectType.SUBLINE.name())))
        .andExpect(jsonPath("$.referencedLine.lineSlnid", is(LineVersionV3TestData.PARENT_SLNID)))
        .andExpect(jsonPath("$.swissLineNumber", is("90.583.7")));
    LineVersionV3 member = findMember();
    assertThat(member.getLineType()).isEqualTo(LineType.CONCESSION);
    assertThat(member.getSwissLineNumber()).isEqualTo("90.583.7");
  }

  @Test
  void shouldIgnoreParentResolvedFieldsOnMember() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    CreateLineVersionModelV3 model = memberModel(LineType.TECHNICAL)
        .number("L9")
        .shortNumber("9")
        .offerCategory(OfferCategory.IC)
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated());
    LineVersionV3 member = findMember();
    assertThat(member.getNumber()).isNull();
    assertThat(member.getShortNumber()).isNull();
    assertThat(member.getOfferCategory()).isNull();
  }

  @Test
  void shouldNotCreateMemberWhenParentNotFound() throws Exception {
    // Given
    CreateLineVersionModelV3 model = memberModel(LineType.TECHNICAL)
        .referencedLineSlnid(LineVersionV3TestData.UNKNOWN_SLNID)
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isNotFound());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldNotCreateMemberWhenParentRevoked() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build());
    CreateLineVersionModelV3 model = memberModel(LineType.TECHNICAL).build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isConflict())
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.LINE.CONFLICT.REFERENCED_LINE_REVOKED")));
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  void shouldNotCreateMemberWhenParentIsItselfAMember() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());
    CreateLineVersionModelV3 model = memberModel(LineType.TECHNICAL)
        .referencedLineSlnid(LineVersionV3TestData.CHILD_SLNID)
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isConflict());
    assertThat(lineVersionV3Repository.findAll()).hasSize(2);
  }

  @Test
  void shouldNotCreateMemberWithTypeNotMatchingParent() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    CreateLineVersionModelV3 model = memberModel(LineType.DISPOSITION).build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  void shouldNotCreateOrderlyLineWithoutConcessionType() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").concessionType(null).build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldNotCreateOrderlyLineWithOverlappingSwissLineNumber() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().swissLineNumber("90.100").build());
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isConflict());
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  void shouldNotCreateMemberOutsideParentValidity() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    CreateLineVersionModelV3 model = memberModel(LineType.TECHNICAL)
        .validFrom(LocalDate.of(2025, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31))
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isConflict())
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.LINE.CONFLICT.SUBLINE_VALIDITY")));
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  void shouldNotCreateWhenSlnidIsSet() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").slnid("ch:1:slnid:1").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldNotCreateWhenIdIsSet() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").id(5L).build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldNotCreateWithoutDescription() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").description(null).build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldSplitVersionsWhenUpdatingInTheMiddle() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validTo(LocalDate.of(2026, 12, 31)).build());
    UpdateLineVersionModelV3 model = updateModelOf(saved)
        .description("Updated description")
        .validFrom(LocalDate.of(2026, 4, 1))
        .validTo(LocalDate.of(2026, 8, 31))
        .build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[1].description", is("Updated description")));
    List<LineVersionV3> versions = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID);
    assertThat(versions).hasSize(3);
    assertThat(versions).extracting(LineVersionV3::getDescription)
        .containsExactly("Val-Surses-Bus", "Updated description", "Val-Surses-Bus");
    assertThat(versions).extracting(LineVersionV3::getValidFrom)
        .containsExactly(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 4, 1), LocalDate.of(2026, 9, 1));
  }

  @Test
  void shouldNotUpdateWithStaleEtag() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved)
        .etagVersion(saved.getVersion() + 5)
        .description("Updated description")
        .build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isPreconditionFailed());
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getDescription()).isEqualTo("Val-Surses-Bus");
  }

  @Test
  void shouldRejectReferencedLineSlnidOnUpdate() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    LineVersionV3 member = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());
    UpdateLineVersionModelV3 model = updateModelOf(member)
        .referencedLineSlnid("ch:1:slnid:1000001")
        .description("Updated description")
        .build();

    // When
    ResultActions result = update(member.getId(), model);

    // Then
    result.andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("referencedLineSlnid is immutable")));
    LineVersionV3 unchanged = lineVersionV3Repository.findById(member.getId()).orElseThrow();
    assertThat(unchanged.getReferencedLineSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
    assertThat(unchanged.getDescription()).isEqualTo("Savognin - Cre digl Lai");
  }

  @Test
  void shouldRejectLineTypeOnUpdate() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved)
        .lineType(LineType.TEMPORARY)
        .description("Updated description")
        .build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("lineType is immutable")));
    LineVersionV3 unchanged = lineVersionV3Repository.findById(saved.getId()).orElseThrow();
    assertThat(unchanged.getLineType()).isEqualTo(LineType.ORDERLY);
    assertThat(unchanged.getDescription()).isEqualTo("Val-Surses-Bus");
  }

  @Test
  void shouldShortenMemberWhenShorteningParent() throws Exception {
    // Given
    LineVersionV3 parent = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validTo(LocalDate.of(2026, 12, 31)).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .validTo(LocalDate.of(2026, 12, 31)).build());
    UpdateLineVersionModelV3 model = updateModelOf(parent)
        .validTo(LocalDate.of(2026, 6, 30))
        .build();

    // When
    ResultActions result = update(parent.getId(), model);

    // Then
    result.andExpect(status().isOk());
    LineVersionV3 member = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID)
        .getFirst();
    assertThat(member.getValidTo()).isEqualTo(LocalDate.of(2026, 6, 30));
    LineVersionV3 shortenedParent = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID)
        .getFirst();
    assertThat(shortenedParent.getValidTo()).isEqualTo(LocalDate.of(2026, 6, 30));
  }

  @Test
  void shouldNotShortenParentBeforeStartOfLastMemberVersion() throws Exception {
    // Given
    LineVersionV3 parent = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validTo(LocalDate.of(2026, 12, 31)).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .validTo(LocalDate.of(2026, 6, 30)).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder().description("Second half")
        .validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build());
    UpdateLineVersionModelV3 model = updateModelOf(parent)
        .validTo(LocalDate.of(2026, 5, 31))
        .build();

    // When
    ResultActions result = update(parent.getId(), model);

    // Then
    result.andExpect(status().isConflict())
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.LINE.CONFLICT.MEMBERS_OUTSIDE_VALIDITY")))
        .andExpect(jsonPath("$.error", containsString(LineVersionV3TestData.CHILD_SLNID)));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID))
        .extracting(LineVersionV3::getValidTo)
        .containsExactly(LocalDate.of(2026, 12, 31));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID))
        .extracting(LineVersionV3::getValidTo)
        .containsExactly(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 12, 31));
  }

  @Test
  void shouldSplitParentWithoutShorteningWhenDescriptionAndValidToChangeTogether() throws Exception {
    // Given
    LineVersionV3 parent = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validTo(LocalDate.of(2026, 12, 31)).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .validTo(LocalDate.of(2026, 12, 31)).build());
    UpdateLineVersionModelV3 model = updateModelOf(parent)
        .description("Updated description")
        .validTo(LocalDate.of(2026, 6, 30))
        .build();

    // When
    ResultActions result = update(parent.getId(), model);

    // Then
    result.andExpect(status().isOk());
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID))
        .extracting(LineVersionV3::getDescription, LineVersionV3::getValidTo)
        .containsExactly(
            tuple("Updated description", LocalDate.of(2026, 6, 30)),
            tuple("Val-Surses-Bus", LocalDate.of(2026, 12, 31)));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID))
        .extracting(LineVersionV3::getValidTo)
        .containsExactly(LocalDate.of(2026, 12, 31));
  }

  @Test
  void shouldNotUpdateRevokedLine() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .status(Status.REVOKED).build());
    UpdateLineVersionModelV3 model = updateModelOf(saved).description("Updated description").build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isForbidden())
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.ERROR.REVOKED")));
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getDescription()).isEqualTo("Val-Surses-Bus");
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingUnknownId() throws Exception {
    // Given
    UpdateLineVersionModelV3 model = updateModelOf(LineVersionV3TestData.line()).etagVersion(0).build();

    // When
    ResultActions result = update(-1L, model);

    // Then
    result.andExpect(status().isNotFound());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotCreateWithoutWritePermissionOnBusinessOrganisation() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldNotCreateAsUnauthorizedClient() throws Exception {
    // Given
    CreateLineVersionModelV3 model = orderlyLineModel("90.100").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotUpdateWithoutWritePermissionOnBusinessOrganisation() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved).description("Updated description").build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getDescription()).isEqualTo("Val-Surses-Bus");
  }

  @Test
  void shouldReturnOwnConcessionTypeOfConcessionMemberWhenCreating() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE).build());
    CreateLineVersionModelV3 model = memberModel(LineType.CONCESSION)
        .concessionType(ConcessionType.FEDERALLY_LICENSED_LINE)
        .swissLineNumber("90.583.7")
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("$.concessionType", is(ConcessionType.FEDERALLY_LICENSED_LINE.name())));
    assertThat(findMember().getConcessionType()).isEqualTo(ConcessionType.FEDERALLY_LICENSED_LINE);
  }

  @Test
  void shouldReturnOwnConcessionTypeOfConcessionMemberWhenReading() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.concessionMemberBuilder()
        .concessionType(ConcessionType.FEDERALLY_LICENSED_LINE).build());

    // When
    ResultActions result = mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CONCESSION_MEMBER_SLNID));

    // Then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].concessionType", is(ConcessionType.FEDERALLY_LICENSED_LINE.name())));
  }

  @Test
  void shouldKeepOwnConcessionTypeOfConcessionMemberOnGetPutRoundTrip() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.concessionMemberBuilder()
        .concessionType(ConcessionType.FEDERALLY_LICENSED_LINE).build());
    ObjectNode readVersion = readFirstVersion(LineVersionV3TestData.CONCESSION_MEMBER_SLNID);

    // When
    ResultActions result = updateFromReadModel(readVersion, "Updated description");

    // Then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].concessionType", is(ConcessionType.FEDERALLY_LICENSED_LINE.name())));
    LineVersionV3 member = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(
        LineVersionV3TestData.CONCESSION_MEMBER_SLNID).getFirst();
    assertThat(member.getDescription()).isEqualTo("Updated description");
    assertThat(member.getConcessionType()).isEqualTo(ConcessionType.FEDERALLY_LICENSED_LINE);
  }

  @Test
  void shouldUpdateTechnicalMemberOnGetPutRoundTrip() throws Exception {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());
    ObjectNode readVersion = readFirstVersion(LineVersionV3TestData.CHILD_SLNID);

    // When
    ResultActions result = updateFromReadModel(readVersion, "Updated description");

    // Then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].description", is("Updated description")))
        .andExpect(jsonPath("$[0].concessionType", is(ConcessionType.CANTONALLY_APPROVED_LINE.name())));
    LineVersionV3 member = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID)
        .getFirst();
    assertThat(member.getDescription()).isEqualTo("Updated description");
    assertThat(member.getConcessionType()).isNull();
  }

  private LineVersionV3 findMember() {
    return lineVersionV3Repository.findAll().stream()
        .filter(version -> version.getReferencedLineSlnid() != null)
        .findFirst()
        .orElseThrow();
  }

  private ResultActions create(CreateLineVersionModelV3 model) throws Exception {
    return mvc.perform(post(VERSIONS_URL).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

  private ResultActions update(Long id, UpdateLineVersionModelV3 model) throws Exception {
    return mvc.perform(put(VERSIONS_URL + "/" + id).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

  private ObjectNode readFirstVersion(String slnid) throws Exception {
    String json = mvc.perform(get(VERSIONS_URL + "/" + slnid))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return (ObjectNode) mapper.readTree(json).get(0);
  }

  /**
   * Builds the PUT body the way a client does: take the read model, drop the read-only fields, change a business field.
   */
  private ResultActions updateFromReadModel(ObjectNode readVersion, String description) throws Exception {
    long id = readVersion.get("id").asLong();
    READ_ONLY_FIELDS.forEach(readVersion::remove);
    readVersion.put("description", description);
    return mvc.perform(put(VERSIONS_URL + "/" + id).contentType(contentType).content(mapper.writeValueAsString(readVersion)));
  }

  private static CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> orderlyLineModel(String swissLineNumber) {
    return CreateLineVersionModelV3.builder()
        .lineType(LineType.ORDERLY)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .swissLineNumber(swissLineNumber)
        .number("L1")
        .description("New orderly line")
        .businessOrganisation(SBOID)
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31));
  }

  private static CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> memberModel(LineType lineType) {
    return CreateLineVersionModelV3.builder()
        .lineType(lineType)
        .referencedLineSlnid(LineVersionV3TestData.PARENT_SLNID)
        .description("New member line")
        .businessOrganisation(SBOID)
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31));
  }

  private static UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> updateModelOf(LineVersionV3 version) {
    return UpdateLineVersionModelV3.builder()
        .etagVersion(version.getVersion())
        .description(version.getDescription())
        .longName(version.getLongName())
        .number(version.getNumber())
        .shortNumber(version.getShortNumber())
        .offerCategory(version.getOfferCategory())
        .swissLineNumber(version.getSwissLineNumber())
        .concessionType(version.getConcessionType())
        .linienId(version.getLinienId())
        .businessOrganisation(version.getBusinessOrganisation())
        .meansOfTransport(version.getMeansOfTransport())
        .financing(version.getFinancing())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo());
  }

}
