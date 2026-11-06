package ch.sbb.line.directory.module.line.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
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
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Covers the fields reserved for supervisors (financing, cableway fields, fotComment) by the {@code @PreAuthorize} expressions
 * on {@code LineVersionV3Service}. Runs as a standard internal user instead of the default admin; authorisation is expressed
 * exclusively through the LIDI permission stored for that user, so every test acts either as writer on the business
 * organisation, as writer without permission on it, or as supervisor.
 */
@MockitoBean(types = SharedBusinessOrganisationService.class)
@WithMockJwtAuthentication(role = MockRole.STANDARD)
class LineControllerV3FieldPermissionApiTest extends BaseControllerApiTest {

  private static final String VERSIONS_URL = "/v3/lines/internal/versions";
  private static final String SBOID = "ch:1:sboid:1100048";
  private static final String OTHER_SBOID = "ch:1:sboid:1100049";
  private static final String STORED_FOT_COMMENT = "BAV comment";

  private final LineVersionV3Repository lineVersionV3Repository;
  private final PermissionRepository permissionRepository;

  @Autowired
  LineControllerV3FieldPermissionApiTest(LineVersionV3Repository lineVersionV3Repository,
      PermissionRepository permissionRepository) {
    this.lineVersionV3Repository = lineVersionV3Repository;
    this.permissionRepository = permissionRepository;
  }

  @BeforeEach
  void grantWriterPermissionOnBusinessOrganisation() {
    saveLidiPermission(ApplicationRole.WRITER, SBOID);
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Test
  void shouldNotCreateLineWithRestrictedFieldsAsWriter() throws Exception {
    // Given
    CreateLineVersionModelV3 model = lineModel().financing(Financing.RPV).cablewayNumber("3521").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldCreateLineWithoutRestrictedFieldsAsWriter() throws Exception {
    // Given
    CreateLineVersionModelV3 model = lineModel().build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated());
    assertThat(lineVersionV3Repository.findAll()).singleElement()
        .satisfies(version -> {
          assertThat(version.getFinancing()).isNull();
          assertThat(version.getCablewayNumber()).isNull();
          assertThat(version.getCablewayType()).isNull();
          assertThat(version.getCablewayName()).isNull();
          assertThat(version.getFotComment()).isNull();
        });
  }

  @Test
  void shouldNotUpdateRestrictedFieldAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved).financing(Financing.RPV).build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getFinancing())
        .isEqualTo(Financing.SELF_FINANCED);
  }

  @Test
  void shouldUpdateOtherFieldsWhileResendingTheUnchangedRestrictedFieldsAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = savedLineWithFotComment();
    UpdateLineVersionModelV3 model = updateModelOf(saved).description("Updated description").build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isOk());
    LineVersionV3 reloaded = lineVersionV3Repository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getDescription()).isEqualTo("Updated description");
    assertThat(reloaded.getFotComment()).isEqualTo(STORED_FOT_COMMENT);
  }

  /**
   * The restricted cableway fields are strings, so this verifies that an unchanged value is recognised by equality and not by
   * identity, which would reject every update carrying a restricted string field.
   */
  @Test
  void shouldUpdateOtherFieldsWhileResendingTheUnchangedCablewayStringsAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.cableway());
    UpdateLineVersionModelV3 model = updateModelOf(saved).description("Updated description").build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isOk());
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getCablewayNumber()).isEqualTo("3521");
  }

  @Test
  void shouldNotUpdateCablewayFieldAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.cableway());
    UpdateLineVersionModelV3 model = updateModelOf(saved).cablewayName("Renamed cableway").build();

    // When & Then
    update(saved.getId(), model).andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getCablewayName())
        .isEqualTo("Luftseilbahn Wengen - Maennlichen");
  }

  @Test
  void shouldDenyUpdateWithoutWritePermissionOnBusinessOrganisation() throws Exception {
    // Given
    grantWriterPermissionOnOtherBusinessOrganisation();
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved).financing(Financing.RPV).build();

    // When & Then
    update(saved.getId(), model).andExpect(status().isForbidden());
  }

  @Test
  void shouldCreateLineWithRestrictedFieldsAsSupervisor() throws Exception {
    // Given
    grantSupervisorPermission();
    CreateLineVersionModelV3 model = lineModel()
        .meansOfTransport(Set.of(LineMeanOfTransport.CABLE_RAILWAY))
        .financing(Financing.RPV)
        .cablewayNumber("3521")
        .fotComment(STORED_FOT_COMMENT)
        .build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated());
    assertThat(lineVersionV3Repository.findAll()).singleElement()
        .satisfies(version -> {
          assertThat(version.getFinancing()).isEqualTo(Financing.RPV);
          assertThat(version.getCablewayNumber()).isEqualTo("3521");
          assertThat(version.getFotComment()).isEqualTo(STORED_FOT_COMMENT);
        });
  }

  @Test
  void shouldUpdateRestrictedFieldAsSupervisor() throws Exception {
    // Given
    grantSupervisorPermission();
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved).financing(Financing.RPV).fotComment(STORED_FOT_COMMENT).build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isOk());
    LineVersionV3 reloaded = lineVersionV3Repository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getFinancing()).isEqualTo(Financing.RPV);
    assertThat(reloaded.getFotComment()).isEqualTo(STORED_FOT_COMMENT);
  }

  @Test
  void shouldNotCreateLineWithFotCommentAsWriter() throws Exception {
    // Given
    CreateLineVersionModelV3 model = lineModel().fotComment(STORED_FOT_COMMENT).build();

    // When & Then
    create(model).andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldNotUpdateFotCommentAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = savedLineWithFotComment();
    UpdateLineVersionModelV3 model = updateModelOf(saved).fotComment("Changed").build();

    // When & Then
    update(saved.getId(), model).andExpect(status().isForbidden());
    assertThat(storedFotComment(saved.getId())).isEqualTo(STORED_FOT_COMMENT);
  }

  @Test
  void shouldDenyCreateWithoutWritePermissionOnBusinessOrganisation() throws Exception {
    // Given
    grantWriterPermissionOnOtherBusinessOrganisation();

    // When & Then
    create(lineModel().build()).andExpect(status().isForbidden());
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  private void grantWriterPermissionOnOtherBusinessOrganisation() {
    permissionRepository.deleteAll();
    saveLidiPermission(ApplicationRole.WRITER, OTHER_SBOID);
  }

  private void grantSupervisorPermission() {
    permissionRepository.deleteAll();
    saveLidiPermission(ApplicationRole.SUPERVISOR, null);
  }

  private void saveLidiPermission(ApplicationRole role, String restrictedSboid) {
    Permission permission = Permission.builder()
        .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
        .application(ApplicationType.LIDI)
        .role(role)
        .build();
    if (restrictedSboid != null) {
      permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
          .permission(permission)
          .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
          .restriction(restrictedSboid)
          .build()));
    }
    permissionRepository.saveAndFlush(permission);
  }

  private CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> lineModel() {
    return CreateLineVersionModelV3.builder()
        .lineType(LineType.ORDERLY)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .number("L1")
        .offerCategory(OfferCategory.B)
        .swissLineNumber("90.100")
        .description("Field permission test")
        .businessOrganisation(SBOID)
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN))
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31));
  }

  private UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> updateModelOf(LineVersionV3 version) {
    return UpdateLineVersionModelV3.builder()
        .etagVersion(version.getVersion())
        .concessionType(version.getConcessionType())
        .number(version.getNumber())
        .swissLineNumber(version.getSwissLineNumber())
        .offerCategory(version.getOfferCategory())
        .longName(version.getLongName())
        .description(version.getDescription())
        .businessOrganisation(version.getBusinessOrganisation())
        .financing(version.getFinancing())
        .cablewayNumber(version.getCablewayNumber())
        .cablewayType(version.getCablewayType())
        .cablewayName(version.getCablewayName())
        .fotComment(version.getFotComment())
        .meansOfTransport(version.getMeansOfTransport())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo());
  }

  private LineVersionV3 savedLineWithFotComment() {
    return lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().fotComment(STORED_FOT_COMMENT).build());
  }

  private String storedFotComment(Long id) {
    return lineVersionV3Repository.findById(id).orElseThrow().getFotComment();
  }

  private ResultActions create(CreateLineVersionModelV3 model) throws Exception {
    return mvc.perform(post(VERSIONS_URL).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

  private ResultActions update(Long id, UpdateLineVersionModelV3 model) throws Exception {
    return mvc.perform(put(VERSIONS_URL + "/" + id).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

}
