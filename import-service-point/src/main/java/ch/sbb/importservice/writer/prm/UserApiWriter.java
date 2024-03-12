package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.api.user.administration.CountryPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.PermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.imports.user.UserCsvModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.importservice.writer.BaseApiWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class UserApiWriter extends BaseApiWriter implements ItemWriter<UserCsvModel> {

  public static final String COMMA_SEPARATOR = ",";
  public static final String WILDCARD = "*";

  @Override
  public void write(Chunk<? extends UserCsvModel> userCsvModels) {
    List<UserCsvModel> models = new ArrayList<>(userCsvModels.getItems());
    List<UserPermissionCreateModel> userPermissionCreateModels = models.stream().map(this::mapToUserPermissionCreateModel)
        .toList();
    Long stepExecutionId = stepExecution.getId();

    userPermissionCreateModels.forEach(model -> {
      UserModel userAlreadyExists = userClient.userAlreadyExists(model.getSbbUserId());
      if (userAlreadyExists == null) {
        log.info("CREATE user: {} with permissions: {}", model.getSbbUserId(), model.getPermissions());
        UserModel user = userClient.createUser(model);
        saveItemProcessed(stepExecutionId, user.getUserId(), ItemImportResponseStatus.SUCCESS, "new user added");
      } else {
        log.info("UPDATE user: {} with permissions: {}", model.getSbbUserId(), model.getPermissions());
        UserModel user = userClient.updateUser(model);
        saveItemProcessed(stepExecutionId, user.getUserId(), ItemImportResponseStatus.SUCCESS, "user updated");
      }
    });
  }

  private UserPermissionCreateModel mapToUserPermissionCreateModel(UserCsvModel userCsvModel) {
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
