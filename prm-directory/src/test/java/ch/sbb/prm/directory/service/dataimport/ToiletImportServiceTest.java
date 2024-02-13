package ch.sbb.prm.directory.service.dataimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.ToiletCsvTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.service.PrmLocationService;
import ch.sbb.prm.directory.service.StopPointService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ToiletImportServiceTest {

  public static final String SLOID = "ch:1:sloid:76646";
  @MockBean
  private StopPointService stopPointService;

  @MockBean
  private PrmLocationService prmLocationService;

  private final ToiletRepository toiletRepository;
  private final ToiletImportService toiletImportService;

  @Autowired
  ToiletImportServiceTest(ToiletRepository toiletRepository,
      ToiletImportService toiletImportService) {
    this.toiletRepository = toiletRepository;
    this.toiletImportService = toiletImportService;
  }

  @Test
  void shouldImportWhenToiletDoesNotExists() {
    //when
    List<ItemImportResult> result = toiletImportService.importToiletPoints(
        List.of(ToiletCsvTestData.getContainer()));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8576646");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    verify(prmLocationService, times(1)).allocateSloid(any(ToiletVersion.class), eq(SloidType.TOILET));
  }

  @Test
  void shouldImportWhenToiletPointExists() {
    //given
    ToiletCsvModelContainer toiletCsvModelContainer = ToiletCsvTestData.getContainer();
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setSloid(toiletCsvModelContainer.getSloid());
    toiletVersion.setParentServicePointSloid(SLOID);
    toiletVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8576646));
    toiletRepository.saveAndFlush(toiletVersion);
    doNothing().when(stopPointService).checkStopPointExists(any());
    //when
    List<ItemImportResult> result = toiletImportService.importToiletPoints(List.of(toiletCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8576646");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    verify(prmLocationService, never()).allocateSloid(any(), any());
  }

}
