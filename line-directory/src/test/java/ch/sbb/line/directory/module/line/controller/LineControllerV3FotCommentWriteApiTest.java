package ch.sbb.line.directory.module.line.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.node.ObjectNode;

/**
 * ATLAS-3606.subtask-2: the fotComment (BAV comment) is a regular, versioned field of the generic create
 * ({@code POST v3/lines/versions}) and update ({@code PUT v3/lines/versions/{id}}) endpoints. Only a Supervisor (ATLAS admin or
 * LIDI supervisor) may change it; everybody else has to send the stored value back unchanged.
 */
@MockitoBean(types = SharedBusinessOrganisationService.class)
class LineControllerV3FotCommentWriteApiTest extends BaseControllerApiTest {

  private static final String VERSIONS_URL = "/v3/lines/internal/versions";
  private static final String SBOID = "ch:1:sboid:1100048";
  private static final String STORED_FOT_COMMENT = "X";
  private static final String STORED_COMMENT = "TU comment";
  private static final LocalDate STORED_FROM = LocalDate.of(2026, 1, 1);
  private static final LocalDate STORED_TO = LocalDate.of(2026, 12, 31);

  private final LineVersionV3Repository lineVersionV3Repository;
  private final PermissionRepository permissionRepository;

  @Autowired
  LineControllerV3FotCommentWriteApiTest(LineVersionV3Repository lineVersionV3Repository,
      PermissionRepository permissionRepository) {
    this.lineVersionV3Repository = lineVersionV3Repository;
    this.permissionRepository = permissionRepository;
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
    permissionRepository.deleteAll();
  }

  // AC1: Supervisor
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldCreateLineWithFotCommentAsSupervisor() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);

    // When
    ResultActions result = create(createModel().comment(STORED_COMMENT).fotComment("BAV comment").build());

    // Then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("$.fotComment", is("BAV comment")))
        .andExpect(jsonPath("$.comment", is(STORED_COMMENT)));
    assertThat(lineVersionV3Repository.findAll()).extracting(LineVersionV3::getComment, LineVersionV3::getFotComment)
        .containsExactly(tuple(STORED_COMMENT, "BAV comment"));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldSetFotCommentOnStoredNullWithUpdateAsSupervisor() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(null);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("BAV comment").build());

    // Then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].fotComment", is("BAV comment")))
        .andExpect(jsonPath("$[0].comment", is(STORED_COMMENT)));
    LineVersionV3 reloaded = lineVersionV3Repository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getFotComment()).isEqualTo("BAV comment");
    assertThat(reloaded.getComment()).isEqualTo(STORED_COMMENT);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldChangeStoredFotCommentWithUpdateAsSupervisor() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Changed").build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$[0].fotComment", is("Changed")));
    assertThat(storedFotComment(saved.getId())).isEqualTo("Changed");
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldSetFotCommentOfSublineAsSupervisorWithoutTouchingTheLine() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 line = saveLine("line fot");
    LineVersionV3 subline = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .fotComment(null)
        .validFrom(STORED_FROM)
        .validTo(STORED_TO)
        .build());

    // When
    ResultActions result = update(subline.getId(), updateModelOf(subline).fotComment("subline fot").build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$[0].fotComment", is("subline fot")));
    assertThat(storedFotComment(subline.getId())).isEqualTo("subline fot");
    assertThat(storedFotComment(line.getId())).isEqualTo("line fot");
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldKeepFotCommentWhenSupervisorSendsItBackUnchanged() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).comment("New TU comment").build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$[0].fotComment", is(STORED_FOT_COMMENT)));
    assertThat(storedFotComment(saved.getId())).isEqualTo(STORED_FOT_COMMENT);
  }

  // AC2: ATLAS admin (default mock user)
  @Test
  void shouldCreateLineWithFotCommentAsAtlasAdmin() throws Exception {
    // When
    ResultActions result = create(createModel().fotComment("Admin comment").build());

    // Then
    result.andExpect(status().isCreated()).andExpect(jsonPath("$.fotComment", is("Admin comment")));
    assertThat(lineVersionV3Repository.findAll()).extracting(LineVersionV3::getFotComment).containsExactly("Admin comment");
  }

  @Test
  void shouldChangeFotCommentWithUpdateAsAtlasAdmin() throws Exception {
    // Given
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Changed by admin").build());

    // Then
    result.andExpect(status().isOk());
    assertThat(storedFotComment(saved.getId())).isEqualTo("Changed by admin");
  }

  // AC3: SUPER_USER is not enough
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotChangeFotCommentWithUpdateAsSuperUser() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPER_USER);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Changed").build());

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotCreateLineWithFotCommentAsSuperUser() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPER_USER);

    // When
    ResultActions result = create(createModel().fotComment("BAV comment").build());

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  // AC4: WRITER must not change the fotComment
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotCreateLineWithFotCommentAsWriter() throws Exception {
    // Given
    givenWriterPermissionOnSboid();

    // When
    ResultActions result = create(createModel().fotComment("BAV comment").build());

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldCreateLineWithoutEffectiveFotCommentAsWriter(String blank) throws Exception {
    // Given
    givenWriterPermissionOnSboid();

    // When
    ResultActions result = create(createModel().comment(STORED_COMMENT).fotComment(blank).build());

    // Then
    result.andExpect(status().isCreated()).andExpect(jsonPath("$.fotComment").value(nullValue()));
    assertThat(lineVersionV3Repository.findAll()).extracting(LineVersionV3::getComment, LineVersionV3::getFotComment)
        .containsExactly(tuple(STORED_COMMENT, null));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotSetFotCommentOnStoredNullAsWriter() throws Exception {
    // Given
    givenWriterPermissionOnSboid();
    LineVersionV3 saved = saveLine(null);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("New").build());

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, null);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotChangeStoredFotCommentAsWriter() throws Exception {
    // Given
    givenWriterPermissionOnSboid();
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Changed").build());

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotClearStoredFotCommentAsWriter(String blank) throws Exception {
    // Given
    givenWriterPermissionOnSboid();
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment(blank).build());

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  // Known and accepted consequence: a missing fotComment is a change to null. API clients have to send the stored value back
  // (the frontend always does); otherwise the writer is rejected instead of silently deleting the BAV comment.
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectWriterOmittingTheStoredFotCommentBecauseMissingValueIsAChangeToNull() throws Exception {
    // Given
    givenWriterPermissionOnSboid();
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);
    ObjectNode body = mapper.valueToTree(updateModelOf(saved).comment("New TU comment").build());
    body.remove("fotComment");

    // When
    ResultActions result = mvc.perform(put(VERSIONS_URL + "/" + saved.getId()).contentType(contentType)
        .content(mapper.writeValueAsString(body)));

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  // AC5: the TU (Writer) keeps editing the TU comment and sends the stored fotComment back
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldChangeCommentAsWriterAndKeepStoredFotComment() throws Exception {
    // Given
    givenWriterPermissionOnSboid();
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).comment("New TU comment").build());

    // Then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].comment", is("New TU comment")))
        .andExpect(jsonPath("$[0].fotComment", is(STORED_FOT_COMMENT)));
    LineVersionV3 reloaded = lineVersionV3Repository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getComment()).isEqualTo("New TU comment");
    assertThat(reloaded.getFotComment()).isEqualTo(STORED_FOT_COMMENT);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldTreatBlankFotCommentAsUnchangedWhenWriterEditsLineWithStoredNull(String blank) throws Exception {
    // Given
    givenWriterPermissionOnSboid();
    LineVersionV3 saved = saveLine(null);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).comment("New TU comment").fotComment(blank).build());

    // Then
    result.andExpect(status().isOk());
    LineVersionV3 reloaded = lineVersionV3Repository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getComment()).isEqualTo("New TU comment");
    assertThat(reloaded.getFotComment()).isNull();
  }

  // AC6
  @ParameterizedTest
  @EnumSource(value = ApplicationRole.class, names = {"READER", "EXPLICIT_READER"})
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotChangeFotCommentAsReader(ApplicationRole role) throws Exception {
    // Given
    givenPermission(role);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Changed").build());

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotChangeFotCommentWithoutAnyPermission() throws Exception {
    // Given
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Changed").build());

    // Then
    result.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotCreateLineWithFotCommentAsReader() throws Exception {
    // Given
    givenPermission(ApplicationRole.READER);

    // When
    ResultActions result = create(createModel().fotComment("BAV comment").build());

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  // AC7: a caller without credentials never reaches the service. The real filter chain rejects the state-changing request without
  // bearer token already at the CSRF filter (403, CSRF is only ignored for bearer-token requests) - not with 401.
  @Test
  @WithAnonymousUser
  void shouldRejectCallerWithoutCredentialsBeforeReachingService() throws Exception {
    // Given
    MockMvc securedMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions createResult = securedMvc.perform(post(VERSIONS_URL).contentType(contentType)
        .content(mapper.writeValueAsString(createModel().fotComment("BAV comment").build())));
    ResultActions updateResult = securedMvc.perform(put(VERSIONS_URL + "/" + saved.getId()).contentType(contentType)
        .content(mapper.writeValueAsString(updateModelOf(saved).fotComment("Changed").build())));

    // Then
    createResult.andExpect(status().isForbidden());
    updateResult.andExpect(status().isForbidden());
    assertUnchanged(saved, STORED_FOT_COMMENT);
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  // AC7: once the CSRF filter is passed, the authorization filter answers an anonymous caller with 401 (bearer entry point).
  @Test
  @WithAnonymousUser
  void shouldRejectAnonymousCallerWith401AtAuthorizationFilter() throws Exception {
    // Given
    MockMvc securedMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions updateResult = securedMvc.perform(put(VERSIONS_URL + "/" + saved.getId()).with(csrf()).contentType(contentType)
        .content(mapper.writeValueAsString(updateModelOf(saved).fotComment("Changed").build())));

    // Then
    updateResult.andExpect(status().isUnauthorized());
    assertUnchanged(saved, STORED_FOT_COMMENT);
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  // AC8
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectFotCommentWith1501CharactersOnUpdate() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);
    String tooLong = "a".repeat(AtlasFieldLengths.LENGTH_1500 + 1);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment(tooLong).build());

    // Then
    result.andExpect(status().isBadRequest());
    assertUnchanged(saved, STORED_FOT_COMMENT);
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectFotCommentWith1501CharactersOnCreate() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);

    // When
    ResultActions result = create(createModel().fotComment("a".repeat(AtlasFieldLengths.LENGTH_1500 + 1)).build());

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  // AC9
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectFotCommentWithCharacterOutsideIso88591OnUpdate() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment("Preis \u20AC").build());

    // Then
    result.andExpect(status().isBadRequest());
    assertUnchanged(saved, STORED_FOT_COMMENT);
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectFotCommentWithCharacterOutsideIso88591OnCreate() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);

    // When
    ResultActions result = create(createModel().fotComment("Preis \u20AC").build());

    // Then
    result.andExpect(status().isBadRequest());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  // AC10
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldPersistFotCommentWithExactly1500CharactersOnUpdate() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(null);
    String maxLength = "\u00E4".repeat(AtlasFieldLengths.LENGTH_1500);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment(maxLength).build());

    // Then
    result.andExpect(status().isOk());
    assertThat(storedFotComment(saved.getId())).hasSize(AtlasFieldLengths.LENGTH_1500).isEqualTo(maxLength);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldPersistFotCommentWithExactly1500CharactersOnCreate() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    String maxLength = "\u00E4".repeat(AtlasFieldLengths.LENGTH_1500);

    // When
    ResultActions result = create(createModel().fotComment(maxLength).build());

    // Then
    result.andExpect(status().isCreated());
    assertThat(lineVersionV3Repository.findAll()).extracting(LineVersionV3::getFotComment).containsExactly(maxLength);
  }

  // AC11
  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldStoreNullForBlankFotCommentAsSupervisor(String blank) throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).fotComment(blank).build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$[0].fotComment").value(nullValue()));
    assertThat(storedFotComment(saved.getId())).isNull();
  }

  // AC12: the fotComment is versioned like every other field
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldSplitVersionWhenSupervisorChangesFotCommentForPartOfValidity() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine("A");

    // When
    ResultActions result = update(saved.getId(),
        updateModelOf(saved).fotComment("B").validFrom(LocalDate.of(2026, 7, 1)).validTo(STORED_TO).build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    List<LineVersionV3> versions = sortedVersions();
    assertThat(versions).extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getFotComment)
        .containsExactly(
            tuple(STORED_FROM, LocalDate.of(2026, 6, 30), "A"),
            tuple(LocalDate.of(2026, 7, 1), STORED_TO, "B"));
    assertThat(versions).extracting(LineVersionV3::getBusinessOrganisation).containsOnly(SBOID);
    assertThat(versions).extracting(LineVersionV3::getComment).containsOnly(STORED_COMMENT);
  }

  @Test
  void shouldKeepFotCommentInAllVersionsWhenAdminSplitsTheVersionWithoutChangingIt() throws Exception {
    // Given
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(), updateModelOf(saved).comment("Changed for second half")
        .validFrom(LocalDate.of(2026, 7, 1)).build());

    // Then
    result.andExpect(status().isOk());
    assertThat(sortedVersions()).extracting(LineVersionV3::getValidFrom, LineVersionV3::getComment,
            LineVersionV3::getFotComment)
        .containsExactly(
            tuple(STORED_FROM, STORED_COMMENT, STORED_FOT_COMMENT),
            tuple(LocalDate.of(2026, 7, 1), "Changed for second half", STORED_FOT_COMMENT));
  }

  // Validity shortening: a fotComment change together with a narrower validity is a business change and must NOT shorten the
  // members (it is not "only the validity shortened").
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotShortenMembersWhenFotCommentIsChangedTogetherWithNarrowerValidity() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 line = saveLine("A");
    LineVersionV3 member = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .validFrom(STORED_FROM)
        .validTo(STORED_TO)
        .build());

    // When
    ResultActions result = update(line.getId(), updateModelOf(line).fotComment("B").validTo(LocalDate.of(2026, 6, 30)).build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    assertThat(versionsOf(LineVersionV3TestData.PARENT_SLNID))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getFotComment)
        .containsExactly(
            tuple(STORED_FROM, LocalDate.of(2026, 6, 30), "B"),
            tuple(LocalDate.of(2026, 7, 1), STORED_TO, "A"));
    LineVersionV3 reloadedMember = lineVersionV3Repository.findById(member.getId()).orElseThrow();
    assertThat(reloadedMember.getValidFrom()).isEqualTo(STORED_FROM);
    assertThat(reloadedMember.getValidTo()).isEqualTo(STORED_TO);
  }

  // Validity-only shortening with an unchanged fotComment behaves exactly as before (members are shortened together).
  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldShortenMembersWhenOnlyValidityIsShortenedAndFotCommentIsUnchanged() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 line = saveLine("A");
    LineVersionV3 member = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .validFrom(STORED_FROM)
        .validTo(STORED_TO)
        .build());

    // When
    ResultActions result = update(line.getId(), updateModelOf(line).validTo(LocalDate.of(2026, 6, 30)).build());

    // Then
    result.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    assertThat(versionsOf(LineVersionV3TestData.PARENT_SLNID))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getFotComment)
        .containsExactly(tuple(STORED_FROM, LocalDate.of(2026, 6, 30), "A"));
    assertThat(lineVersionV3Repository.findById(member.getId()).orElseThrow().getValidTo()).isEqualTo(LocalDate.of(2026, 6, 30));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectStaleEtagWith412() throws Exception {
    // Given
    givenPermission(ApplicationRole.SUPERVISOR);
    LineVersionV3 saved = saveLine(STORED_FOT_COMMENT);

    // When
    ResultActions result = update(saved.getId(),
        updateModelOf(saved).fotComment("Changed").etagVersion(saved.getVersion() + 1).build());

    // Then
    result.andExpect(status().isPreconditionFailed());
    assertUnchanged(saved, STORED_FOT_COMMENT);
  }

  private void givenPermission(ApplicationRole role) {
    permissionRepository.saveAndFlush(Permission.builder()
        .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
        .application(ApplicationType.LIDI)
        .role(role)
        .build());
  }

  private void givenWriterPermissionOnSboid() {
    Permission permission = Permission.builder()
        .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
        .application(ApplicationType.LIDI)
        .role(ApplicationRole.WRITER)
        .build();
    permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .permission(permission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction(SBOID)
        .build()));
    permissionRepository.saveAndFlush(permission);
  }

  private LineVersionV3 saveLine(String fotComment) {
    return lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .comment(STORED_COMMENT)
        .fotComment(fotComment)
        .validFrom(STORED_FROM)
        .validTo(STORED_TO)
        .build());
  }

  private String storedFotComment(Long id) {
    return lineVersionV3Repository.findById(id).orElseThrow().getFotComment();
  }

  private List<LineVersionV3> sortedVersions() {
    return lineVersionV3Repository.findAll().stream().sorted(Comparator.comparing(LineVersionV3::getValidFrom)).toList();
  }

  private List<LineVersionV3> versionsOf(String slnid) {
    return lineVersionV3Repository.findAllBySlnidOrderByValidFrom(slnid);
  }

  private void assertUnchanged(LineVersionV3 original, String expectedFotComment) {
    LineVersionV3 reloaded = lineVersionV3Repository.findById(original.getId()).orElseThrow();
    assertThat(reloaded.getFotComment()).isEqualTo(expectedFotComment);
    assertThat(reloaded.getComment()).isEqualTo(original.getComment());
    assertThat(reloaded.getDescription()).isEqualTo(original.getDescription());
    assertThat(reloaded.getBusinessOrganisation()).isEqualTo(original.getBusinessOrganisation());
    assertThat(reloaded.getValidFrom()).isEqualTo(original.getValidFrom());
    assertThat(reloaded.getValidTo()).isEqualTo(original.getValidTo());
    assertThat(reloaded.getVersion()).isEqualTo(original.getVersion());
  }

  private ResultActions create(CreateLineVersionModelV3 model) throws Exception {
    return mvc.perform(post(VERSIONS_URL).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

  private ResultActions update(Long id, UpdateLineVersionModelV3 model) throws Exception {
    return mvc.perform(put(VERSIONS_URL + "/" + id).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

  private static CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> createModel() {
    return CreateLineVersionModelV3.builder()
        .lineType(LineType.ORDERLY)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .swissLineNumber("90.100")
        .number("L1")
        .offerCategory(OfferCategory.B)
        .description("New orderly line")
        .businessOrganisation(SBOID)
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .validFrom(STORED_FROM)
        .validTo(STORED_TO);
  }

  /**
   * Update body as the frontend sends it: all stored values, including the stored fotComment, are sent back.
   */
  private static UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> updateModelOf(LineVersionV3 version) {
    return UpdateLineVersionModelV3.builder()
        .etagVersion(version.getVersion())
        .description(version.getDescription())
        .longName(version.getLongName())
        .number(version.getNumber())
        .offerCategory(version.getOfferCategory())
        .swissLineNumber(version.getSwissLineNumber())
        .concessionType(version.getConcessionType())
        .linienId(version.getLinienId())
        .businessOrganisation(version.getBusinessOrganisation())
        .meansOfTransport(version.getMeansOfTransport())
        .financing(version.getFinancing())
        .comment(version.getComment())
        .fotComment(version.getFotComment())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo());
  }

}
