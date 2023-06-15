package ch.sbb.atlas.user.administration.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.enumeration.Country;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CountryAndBusinessOrganisationBasedUserAdministrationServiceTest {

  @Mock
  private UserPermissionHolder userPermissionHolder;

  private CountryAndBusinessOrganisationBasedUserAdministrationService countryAndBusinessOrganisationBasedUserAdministrationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    countryAndBusinessOrganisationBasedUserAdministrationService =
        new CountryAndBusinessOrganisationBasedUserAdministrationService(
            userPermissionHolder);
    when(userPermissionHolder.isAdmin()).thenReturn(false);
    when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");
  }

  @Test
  void shouldAllowCreateToAdminUser() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldAllowCreateToSupervisor() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.SUPERVISOR)
                .build()))
        .build()));

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldAllowCreateToSuperUser() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.SUPER_USER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .value(Country.SWITZERLAND.name())
                    .restrictionType(PermissionRestrictionType.COUNTRY)
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldNotAllowCreateToSuperUserWithWrongCountry() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.SUPER_USER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .value(Country.GERMANY.name())
                    .restrictionType(PermissionRestrictionType.COUNTRY)
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isFalse();
  }

  @Test
  void shouldNotAllowCreateToWriterWithNoSboids() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .build()))
        .build()));

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isFalse();
  }

  @Test
  void shouldAllowCreateToWriterCorrectSboidsAndCountry() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                        .value("sboid")
                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .build(),
                    UserAdministrationPermissionRestrictionModel.builder()
                        .value(Country.SWITZERLAND.name())
                        .restrictionType(PermissionRestrictionType.COUNTRY)
                        .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldNotAllowCreateToWriterCorrectSboidsAndWrongCountry() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                        .value("sboid")
                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .build(),
                    UserAdministrationPermissionRestrictionModel.builder()
                        .value(Country.FINLAND.name())
                        .restrictionType(PermissionRestrictionType.COUNTRY)
                        .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToCreate = countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(
        BusinessObjectWithCountry.createDummy().build(), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToCreate).isFalse();
  }

  @Test
  void shouldAllowUpdateToAdminUser() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldAllowUpdateToSupervisor() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.SUPERVISOR)
                .build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldAllowUpdateToSuperUser() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.SUPER_USER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .value(Country.SWITZERLAND.name())
                    .restrictionType(PermissionRestrictionType.COUNTRY)
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldNotAllowUpdateToWriterWithoutSboidsOrCountries() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isFalse();
  }

  @Test
  void shouldNotAllowUpdateToWriterWithoutCountries() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .value("sboid")
                    .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isFalse();
  }

  @Test
  void shouldNotAllowUpdateToWriterWithoutSboid() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(
                    UserAdministrationPermissionRestrictionModel.builder()
                        .value(Country.SWITZERLAND.name())
                        .restrictionType(PermissionRestrictionType.COUNTRY)
                        .build()
                )).build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isFalse();
  }

  @Test
  void shouldAllowUpdateToWriterWithSboidAndCountry() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                        .value("sboid")
                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .build(),
                    UserAdministrationPermissionRestrictionModel.builder()
                        .value(Country.SWITZERLAND.name())
                        .restrictionType(PermissionRestrictionType.COUNTRY)
                        .build()
                )).build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy().build(),
            List.of(BusinessObjectWithCountry.createDummy().anotherValue("previousValue").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldNotAllowUpdateToWriterWithOnlyOneOfTwoRequired() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .value("sboid1")
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy()
                .businessOrganisation("sboid1")
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .build(),
            List.of(BusinessObjectWithCountry.createDummy().businessOrganisation("sboid1").build(),
                BusinessObjectWithCountry.createDummy()
                    .businessOrganisation("sboid2")
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .build()), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isFalse();
  }

  @Test
  void shouldAllowUpdateToWriterWithTwoRequiredSboids() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                        .value("sboid1")
                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .build(), UserAdministrationPermissionRestrictionModel.builder()
                        .value("sboid2")
                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .build(),
                    UserAdministrationPermissionRestrictionModel.builder()
                        .value(Country.SWITZERLAND.name())
                        .restrictionType(PermissionRestrictionType.COUNTRY)
                        .build()
                )).build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy()
                .businessOrganisation("sboid1")
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .build(),
            List.of(BusinessObjectWithCountry.createDummy().businessOrganisation("sboid1").build(),
                BusinessObjectWithCountry.createDummy()
                    .businessOrganisation("sboid2")
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .build()), ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldAllowUpdateToWriterHandingOverObjectToOtherBusinessOrganisation() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .value("sboid1")
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsToUpdate =
        countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(
            BusinessObjectWithCountry.createDummy()
                .businessOrganisation("sboid2")
                .validFrom(LocalDate.of(2021, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .build(),
            List.of(BusinessObjectWithCountry.createDummy().businessOrganisation("sboid1").build()),
            ApplicationType.SEPODI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldReturnAtLeastSupervisorForAdmin() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean bodiPermissions = countryAndBusinessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(
        ApplicationType.BODI);

    // Then
    assertThat(bodiPermissions).isTrue();
  }

  @Test
  void shouldReturnAtLeastSupervisorForSupervisor() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(
                    ApplicationType.BODI)
                .role(
                    ApplicationRole.SUPERVISOR)
                .build()))
        .build()));

    // When
    boolean bodiPermissions = countryAndBusinessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(
        ApplicationType.BODI);

    // Then
    assertThat(bodiPermissions).isTrue();
  }

  @Test
  void shouldReturnNoAccessForSuperuser() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(
                    ApplicationType.BODI)
                .role(
                    ApplicationRole.SUPER_USER)
                .build()))
        .build()));

    // When
    boolean bodiPermissions = countryAndBusinessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(
        ApplicationType.BODI);

    // Then
    assertThat(bodiPermissions).isFalse();
  }

  @Test
  void shouldReturnNoAccessForWriter() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(
                    ApplicationType.BODI)
                .role(
                    ApplicationRole.WRITER)
                .build()))
        .build()));

    // When
    boolean bodiPermissions = countryAndBusinessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(
        ApplicationType.BODI);

    // Then
    assertThat(bodiPermissions).isFalse();
  }

  @Test
  void shouldReturnNoAccessForReader() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(
                    ApplicationType.BODI)
                .role(
                    ApplicationRole.READER)
                .build()))
        .build()));

    // When
    boolean bodiPermissions = countryAndBusinessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(
        ApplicationType.BODI);

    // Then
    assertThat(bodiPermissions).isFalse();
  }

  @Test
  void shouldReturnNoAccessForUnknown() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.empty());

    // When
    boolean bodiPermissions = countryAndBusinessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(
        ApplicationType.BODI);

    // Then
    assertThat(bodiPermissions).isFalse();
  }

}