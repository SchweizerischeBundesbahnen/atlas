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
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.ParkingLotCsvTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
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
class ParkingLotImportServiceTest {

  public static final String SLOID = "ch:1:sloid:76646";
  @MockBean
  private StopPointService stopPointService;

  @MockBean
  private PrmLocationService prmLocationService;

  private final ParkingLotRepository parkingLotRepository;
  private final ParkingLotImportService parkingLotImportService;

  @Autowired
  ParkingLotImportServiceTest(ParkingLotRepository parkingLotRepository,
      ParkingLotImportService parkingLotImportService) {
    this.parkingLotRepository = parkingLotRepository;
    this.parkingLotImportService = parkingLotImportService;
  }

  @Test
  void shouldImportWhenParkingLotDoesNotExists() {
    //when
    List<ItemImportResult> result = parkingLotImportService.importParkingLots(
        List.of(ParkingLotCsvTestData.getContainer()));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    verify(prmLocationService, times(1)).allocateSloid(any(ParkingLotVersion.class), eq(SloidType.PARKING_LOT));
  }

  @Test
  void shouldImportWhenParkingLotPointExists() {
    //given
    ParkingLotCsvModelContainer parkingLotCsvModelContainer = ParkingLotCsvTestData.getContainer();
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setSloid(parkingLotCsvModelContainer.getSloid());
    parkingLotVersion.setParentServicePointSloid(SLOID);
    parkingLotVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8576646));
    parkingLotRepository.saveAndFlush(parkingLotVersion);
    doNothing().when(stopPointService).checkStopPointExists(any());
    //when
    List<ItemImportResult> result = parkingLotImportService.importParkingLots(List.of(parkingLotCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    verify(prmLocationService, never()).allocateSloid(any(), any());
  }

}
