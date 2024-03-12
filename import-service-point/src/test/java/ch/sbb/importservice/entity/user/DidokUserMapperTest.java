package ch.sbb.importservice.entity.user;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import org.junit.jupiter.api.Test;

class DidokUserMapperTest {

  @Test
  void shouldMapWriterToUserPermissionCreateModel(){
    //given
    UserCsvModel e123456 = UserCsvModel.builder()
        .applicationType(ApplicationType.SEPODI)
        .role("WRITER")
        .userid("e123456")
        .sboids("ch:1:sboid:100001,ch:1:sboid:100029,ch:1:sboid:100070,ch:1:sboid:100095,ch:1:sboid:100544,ch:1:sboid:100650")
        .countries("85,14,87")
        .build();
    //when
    UserPermissionCreateModel result = DidokUserMapper.mapToUserPermissionCreateModel(e123456);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getSbbUserId()).isEqualTo("e123456");
    assertThat(result.getPermissions()).hasSize(1);
    assertThat(result.getPermissions().get(0)).isNotNull();
    assertThat(result.getPermissions().get(0).getPermissionRestrictions()).hasSize(9);
  }

  @Test
  void shouldMapSuperVisorToUserPermissionCreateModel(){
    //given
    UserCsvModel e123456 = UserCsvModel.builder()
        .applicationType(ApplicationType.SEPODI)
        .role("SUPERVISOR")
        .userid("e123456")
        .sboids("ch:1:sboid:100001,ch:1:sboid:100029,ch:1:sboid:100070,ch:1:sboid:100095,ch:1:sboid:100544,ch:1:sboid:100650")
        .countries("85,14,87")
        .build();
    //when
    UserPermissionCreateModel result = DidokUserMapper.mapToUserPermissionCreateModel(e123456);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getSbbUserId()).isEqualTo("e123456");
    assertThat(result.getPermissions()).hasSize(1);
    assertThat(result.getPermissions().get(0)).isNotNull();
    assertThat(result.getPermissions().get(0).getPermissionRestrictions()).hasSize(0);
  }

}