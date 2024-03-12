package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.entity.user.UserCsvModel;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DidokUserCsvServiceTest {

  private DidokUserCsvService didokUserCsvService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    didokUserCsvService = new DidokUserCsvService();
  }

  @Test
  void shouldGetSePoDiUserCsvModels(){
    //given
    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("SePoDi_Test.csv")).getFile());
    //when
    List<UserCsvModel> result = didokUserCsvService.getUserCsvModels(file, ApplicationType.SEPODI);

    //then
    assertThat(result).hasSize(338);
  }

  @Test
  void shouldGetPrmUserCsvModels(){
    //given
    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_Test.csv")).getFile());
    //when
    List<UserCsvModel> result = didokUserCsvService.getUserCsvModels(file, ApplicationType.PRM);

    //then
    assertThat(result).hasSize(291);

  }

}