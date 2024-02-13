package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.atlas.testdata.prm.ParkingLotCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ParkingLotCsvServiceTest {

    private ParkingLotCsvService parkingLotCsvService;

    @Mock
    private FileHelperService fileHelperService;

    @Mock
    private JobHelperService jobHelperService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        parkingLotCsvService = new ParkingLotCsvService(fileHelperService, jobHelperService);
    }

    @Test
    void shouldHaveCorrectFileName() {
        CsvFileNameModel csvFileName = parkingLotCsvService.csvFileNameModel();
        assertThat(csvFileName.getFileName()).startsWith("PRM_PARKING_LOTS");
    }

    @Test
    void shouldMergeSequentialEqualsParkingLots() {
        // given
        ParkingLotCsvModel parkingLotCsvModel1 = ParkingLotCsvTestData.getCsvModel();
        ParkingLotCsvModel parkingLotCsvModel2 = ParkingLotCsvTestData.getCsvModel();
        parkingLotCsvModel2.setValidFrom(LocalDate.of(2026, 1, 1));
        parkingLotCsvModel2.setValidTo(LocalDate.of(2026, 12, 31));
        List<ParkingLotCsvModel> csvModels = List.of(parkingLotCsvModel1, parkingLotCsvModel2);

        // when
        List<ParkingLotCsvModelContainer> result = parkingLotCsvService.mapToParkingLotCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(parkingLotCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(parkingLotCsvModel2.getValidTo());
    }

    @Test
    void shouldMergeEqualsParkingLots() {
        // given
        ParkingLotCsvModel parkingLotCsvModel1 = ParkingLotCsvTestData.getCsvModel();
        ParkingLotCsvModel parkingLotCsvModel2 = ParkingLotCsvTestData.getCsvModel();
        List<ParkingLotCsvModel> csvModels = List.of(parkingLotCsvModel1, parkingLotCsvModel2);

        // when
        List<ParkingLotCsvModelContainer> result = parkingLotCsvService.mapToParkingLotCsvModelContainers(
                csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(parkingLotCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(parkingLotCsvModel2.getValidTo());
    }

    @Test
    void shouldReturnOnlyActiveParkingLots() {
        // given
        ParkingLotCsvModel parkingLotCsvModel1 = ParkingLotCsvTestData.getCsvModel();
        parkingLotCsvModel1.setStatus(1);
        ParkingLotCsvModel parkingLotCsvModel2 = ParkingLotCsvTestData.getCsvModel();
        parkingLotCsvModel2.setValidFrom(parkingLotCsvModel1.getValidTo().plusDays(1));
        parkingLotCsvModel2.setValidTo(parkingLotCsvModel1.getValidTo().plusYears(1));
        parkingLotCsvModel2.setStatus(0);
        List<ParkingLotCsvModel> csvModels = List.of(parkingLotCsvModel1,parkingLotCsvModel2);

        // when
        List<ParkingLotCsvModelContainer> result = parkingLotCsvService.mapToParkingLotCsvModelContainers(
                csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getStatus()).isEqualTo(1);
    }

}
