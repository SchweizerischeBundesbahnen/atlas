package ch.sbb.atlas.imports.servicepoint.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicePointCsvModelContainerTest {

  @Test
  void shouldMergeVersionsWithIsNotVirtualAndHasNotGeolocation() {
    //given
    ServicePointCsvModel withGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .didokCode(123)
        .eLv03(0.12345)
        .nLv03(0.12345)
        .eLv95(0.12345)
        .nLv95(0.12345)
        .eWgs84(0.12345)
        .nWgs84(0.12345)
        .isVirtuell(false)
        .build();
    ServicePointCsvModel notVirtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .didokCode(123)
        .isVirtuell(false)
        .build();
    ServicePointCsvModel virtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .build();
    ServicePointCsvModel notVirtualWithoutGeolocation2 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .didokCode(123)
        .isVirtuell(false)
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withGeolocation);
    modelList.add(notVirtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation);
    modelList.add(notVirtualWithoutGeolocation2);
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(123);

    //when
    container.mergeVersionsIsNotVirtualAndHasNotGeolocation();

    //then
    assertThat(container.getServicePointCsvModelList()).hasSize(2);
    container.getServicePointCsvModelList().sort(Comparator.comparing(ServicePointCsvModel::getValidFrom));
    assertThat(container.getServicePointCsvModelList().get(0)).isEqualTo(withGeolocation);
    assertThat(container.getServicePointCsvModelList().get(1).getValidFrom()).isEqualTo(
        notVirtualWithoutGeolocation.getValidFrom());
    assertThat(container.getServicePointCsvModelList().get(1).getValidTo()).isEqualTo(notVirtualWithoutGeolocation2.getValidTo());
    assertThat(container.isHasMergedVersionNotVirtualWithoutGeolocation()).isTrue();
    assertThat(container.getDidokCode()).isEqualTo(12);
  }

  @Test
  void shouldNotMergeVersionsWithIsNotVirtualAndHasNotGeolocationAreNotSequential() {
    //given
    ServicePointCsvModel withGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .didokCode(123)
        .eLv03(0.12345)
        .nLv03(0.12345)
        .eLv95(0.12345)
        .nLv95(0.12345)
        .eWgs84(0.12345)
        .nWgs84(0.12345)
        .isVirtuell(false)
        .build();
    ServicePointCsvModel notVirtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 30))
        .didokCode(123)
        .isVirtuell(false)
        .build();
    ServicePointCsvModel virtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withGeolocation);
    modelList.add(notVirtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation);
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(123);

    //when
    container.mergeVersionsIsNotVirtualAndHasNotGeolocation();

    //then
    assertThat(container.getServicePointCsvModelList()).hasSize(3);
    container.getServicePointCsvModelList().sort(Comparator.comparing(ServicePointCsvModel::getValidFrom));
    assertThat(container.getServicePointCsvModelList().get(0)).isEqualTo(withGeolocation);
    assertThat(container.getServicePointCsvModelList().get(1)).isEqualTo(notVirtualWithoutGeolocation);
    assertThat(container.getServicePointCsvModelList().get(2)).isEqualTo(virtualWithoutGeolocation);
    assertThat(container.isHasMergedVersionNotVirtualWithoutGeolocation()).isFalse();
    assertThat(container.getDidokCode()).isEqualTo(12);
  }

  @Test
  void shouldNotMergeVersionsWithIsNotVirtualAndHasNotGeolocationWhenAbkuerzungIsDifferent() {
    //given
    ServicePointCsvModel withGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .didokCode(123)
        .eLv03(0.12345)
        .nLv03(0.12345)
        .eLv95(0.12345)
        .nLv95(0.12345)
        .eWgs84(0.12345)
        .nWgs84(0.12345)
        .isVirtuell(false)
        .build();
    ServicePointCsvModel notVirtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .didokCode(123)
        .abkuerzung("ab")
        .isVirtuell(false)
        .build();
    ServicePointCsvModel virtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withGeolocation);
    modelList.add(notVirtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation);
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(123);

    //when
    container.mergeVersionsIsNotVirtualAndHasNotGeolocation();

    //then
    assertThat(container.getServicePointCsvModelList()).hasSize(3);
    container.getServicePointCsvModelList().sort(Comparator.comparing(ServicePointCsvModel::getValidFrom));
    assertThat(container.getServicePointCsvModelList().get(0)).isEqualTo(withGeolocation);
    assertThat(container.getServicePointCsvModelList().get(1)).isEqualTo(notVirtualWithoutGeolocation);
    assertThat(container.getServicePointCsvModelList().get(2)).isEqualTo(virtualWithoutGeolocation);
    assertThat(container.isHasMergedVersionNotVirtualWithoutGeolocation()).isFalse();
    assertThat(container.getDidokCode()).isEqualTo(12);
  }

  @Test
  void shouldMergeNotHasBezeichnungDiff() {
    //given
    ServicePointCsvModel withBezeichnung17 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .bezeichnung17("BEZ")
        .build();
    ServicePointCsvModel withoutBezeichnung = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .build();
    ServicePointCsvModel withBezeichnung17Second = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .bezeichnung17("BEZ")
        .build();
    ServicePointCsvModel withAbkuerzung = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .didokCode(123)
        .isVirtuell(true)
        .abkuerzung("ab")
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withBezeichnung17);
    modelList.add(withoutBezeichnung);
    modelList.add(withBezeichnung17Second);
    modelList.add(withAbkuerzung);
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(123);

    //when
    container.mergeHasNotBezeichnungDiff();

    //then
    assertThat(container.getServicePointCsvModelList()).hasSize(2);
    container.getServicePointCsvModelList().sort(Comparator.comparing(ServicePointCsvModel::getValidFrom));
    assertThat(container.getServicePointCsvModelList().get(0).getValidFrom()).isEqualTo(withBezeichnung17.getValidFrom());
    assertThat(container.getServicePointCsvModelList().get(0).getValidTo()).isEqualTo(withBezeichnung17Second.getValidTo());
    assertThat(container.getServicePointCsvModelList().get(1).getValidFrom()).isEqualTo(withAbkuerzung.getValidFrom());
    assertThat(container.getServicePointCsvModelList().get(1).getValidTo()).isEqualTo(withAbkuerzung.getValidTo());
    assertThat(container.isHasMergedVersionNotVirtualWithoutGeolocation()).isFalse();
    assertThat(container.isHasJustBezeichnungDiffMerged()).isTrue();
    assertThat(container.getDidokCode()).isEqualTo(12);
  }

  @Test
  void shouldNotMergeWhenBezeichnungAreDifferent() {
    //given
    ServicePointCsvModel withBezeichnung17 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .didokCode(123)
        .isVirtuell(false)
        .bezeichnung17("BEZ")
        .build();
    ServicePointCsvModel withoutBezeichnung = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .didokCode(123)
        .bezeichnung17("ZEB")
        .isVirtuell(false)
        .build();
    ServicePointCsvModel withAbkuerzung = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .didokCode(123)
        .isVirtuell(false)
        .abkuerzung("ab")
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withBezeichnung17);
    modelList.add(withoutBezeichnung);
    modelList.add(withAbkuerzung);
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(123);

    //when
    container.mergeHasNotBezeichnungDiff();

    //then
    assertThat(container.getServicePointCsvModelList()).hasSize(3);
    assertThat(container.getDidokCode()).isEqualTo(12);
  }

}
