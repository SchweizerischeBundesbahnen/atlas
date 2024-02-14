package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;

import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParkingLotCsvService extends PrmCsvService<ParkingLotCsvModel> {

    public static final String PRM_PARKING_LOT_FILE_NAME = "PRM_PARKING_LOTS";

    protected ParkingLotCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
        super(fileHelperService, jobHelperService);
    }

    @Override
    protected CsvFileNameModel csvFileNameModel() {
        return CsvFileNameModel.builder()
                .fileName(PRM_PARKING_LOT_FILE_NAME)
                .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
                .addDateToPostfix(true)
                .build();
    }

    @Override
    protected String getModifiedDateHeader() {
        return EDITED_AT_COLUMN_NAME_PRM;
    }

    @Override
    protected String getImportCsvJobName() {
        return JobDescriptionConstants.IMPORT_PARKING_LOT_CSV_JOB_NAME;
    }

    @Override
    protected Class<ParkingLotCsvModel> getType() {
        return ParkingLotCsvModel.class;
    }

    public List<ParkingLotCsvModelContainer> mapToParkingLotCsvModelContainers(List<ParkingLotCsvModel> parkingLotCsvModels) {
        Map<String, List<ParkingLotCsvModel>> groupedParkingLots = filterForActive(parkingLotCsvModels).stream()
                .collect(Collectors.groupingBy(ParkingLotCsvModel::getSloid));
        List<ParkingLotCsvModelContainer> result = new ArrayList<>(
                groupedParkingLots.entrySet().stream().map(toContainer()).toList());
        mergeParkingLots(result);
        return result;
    }

    private void mergeParkingLots(List<ParkingLotCsvModelContainer> parkingLotCsvModelContainers) {
        mergeSequentialEqualsVersions(parkingLotCsvModelContainers);
        mergeEqualsVersions(parkingLotCsvModelContainers);
    }

    private static Function<Map.Entry<String, List<ParkingLotCsvModel>>, ParkingLotCsvModelContainer> toContainer() {
        return entry -> ParkingLotCsvModelContainer.builder()
                .sloid(entry.getKey())
                .csvModels(entry.getValue())
                .build();
    }

    private void mergeSequentialEqualsVersions(List<ParkingLotCsvModelContainer> csvModelContainers) {
        log.info("Starting checking sequential equals ParkingLot versions...");
        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<ParkingLotCsvModel> prmCsvMergeResult = mergeSequentialEqualVersions(
                            container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });
        log.info("Total merged sequential ParkingLot versions {}", mergedSloids.size());
        log.info("Merged ParkingLots Sloids {}", mergedSloids);
    }

    private void mergeEqualsVersions(List<ParkingLotCsvModelContainer> csvModelContainers) {
        log.info("Starting checking equals ParkingLots versions...");

        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<ParkingLotCsvModel> prmCsvMergeResult = mergeEqualVersions(container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });

        log.info("Total Merged equals ParkingLots versions {}", mergedSloids.size());
        log.info("Merged equals ParkingLots Sloids {}", mergedSloids);
    }

}
