package ch.sbb.importservice.entity.user;

import ch.sbb.atlas.api.user.administration.CountryPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.PermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.servicepoint.Country;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DidokUserMapper {

  public static final String COMMA_SEPARATOR = ",";
  public static final String WILDCARD = "*";

  public UserPermissionCreateModel mapToUserPermissionCreateModel(UserCsvModel userCsvModel) {
    List<PermissionRestrictionModel<?>> permissionRestrictionModels = new ArrayList<>();
    ApplicationType applicationType = userCsvModel.getApplicationType();
    if (ApplicationRole.SUPERVISOR.name().equals(userCsvModel.getRole())) {
      return mapForSuperVisor(userCsvModel, applicationType);
    }
    mapRestrictions(userCsvModel, permissionRestrictionModels, applicationType);
    PermissionModel permissionModel = PermissionModel.builder()
        .application(applicationType)
        .role(ApplicationRole.valueOf(userCsvModel.getRole()))
        .permissionRestrictions(permissionRestrictionModels)
        .build();
    return UserPermissionCreateModel.builder()
        .sbbUserId(userCsvModel.getUserid())
        .permissions(List.of(permissionModel))
        .build();
  }

  private static void mapRestrictions(UserCsvModel userCsvModel, List<PermissionRestrictionModel<?>> permissionRestrictionModels,
      ApplicationType applicationType) {
    if (ApplicationType.SEPODI == applicationType) {
      List<CountryPermissionRestrictionModel> countryPermissionRestrictionModels = getCountryPermissionRestrictionModels(
          userCsvModel);
      permissionRestrictionModels.addAll(countryPermissionRestrictionModels);
    }
    List<SboidPermissionRestrictionModel> sboidPermissionRestrictionModels = getSboidPermissionRestrictionModels(
        userCsvModel);
    permissionRestrictionModels.addAll(sboidPermissionRestrictionModels);
  }

  private static UserPermissionCreateModel mapForSuperVisor(UserCsvModel userCsvModel, ApplicationType applicationType) {
    PermissionModel permissionModel = PermissionModel.builder()
        .application(applicationType)
        .role(ApplicationRole.valueOf(userCsvModel.getRole()))
        .build();
    return UserPermissionCreateModel.builder()
        .sbbUserId(userCsvModel.getUserid())
        .permissions(List.of(permissionModel))
        .build();
  }

  private static List<CountryPermissionRestrictionModel> getCountryPermissionRestrictionModels(UserCsvModel userCsvModel) {
    List<Country> countries;
    if (userCsvModel.getCountries().equals(WILDCARD)) {
      countries = Arrays.stream(Country.values()).toList();
    } else {
      String countriesAsString = userCsvModel.getCountries();
      countries = Arrays.stream(countriesAsString.split(COMMA_SEPARATOR)).toList().stream()
          .map(s -> Country.from(Integer.valueOf(s))).toList();
    }
    List<CountryPermissionRestrictionModel> countryPermissionRestrictionModels = new ArrayList<>();
    countries.forEach(country -> countryPermissionRestrictionModels.add(CountryPermissionRestrictionModel.builder()
        .type(PermissionRestrictionType.COUNTRY)
        .value(country)
        .build()));
    return countryPermissionRestrictionModels;
  }

  private static List<SboidPermissionRestrictionModel> getSboidPermissionRestrictionModels(UserCsvModel userCsvModel) {

    List<SboidPermissionRestrictionModel> sboidPermissionRestrictionModels = new ArrayList<>();
    if (userCsvModel.getSboids() != null) {
      String sboidsAsString = userCsvModel.getSboids();
      List<String> sboids = Arrays.stream(sboidsAsString.split(COMMA_SEPARATOR)).toList();
      sboids.forEach(sboid -> sboidPermissionRestrictionModels.add(SboidPermissionRestrictionModel.builder()
          .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
          .value(sboid)
          .build()));
      return sboidPermissionRestrictionModels;
    }
    return sboidPermissionRestrictionModels;
  }


}
