package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;

@Service
@Slf4j
public class ReferencePointCsvService extends PrmCsvService<ReferencePointCsvModel> {

    public static final String PRM_REFERENCE_POINT_FILE_NAME = "PRM_REFERENCE_POINTS";

    protected ReferencePointCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
        super(fileHelperService, jobHelperService);
    }

    @Override
    protected CsvFileNameModel csvFileNameModel() {
        return CsvFileNameModel.builder()
                .fileName(PRM_REFERENCE_POINT_FILE_NAME)
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
        return JobDescriptionConstants.IMPORT_REFERENCE_POINT_CSV_JOB_NAME;
    }

    @Override
    protected Class<ReferencePointCsvModel> getType() {
        return ReferencePointCsvModel.class;
    }

    public List<ReferencePointCsvModelContainer> mapToReferencePointCsvModelContainers(List<ReferencePointCsvModel> referencePointCsvModels) {
        Map<String, List<ReferencePointCsvModel>> groupedReferencePoints = filterForActive(referencePointCsvModels).stream()
                .collect(Collectors.groupingBy(ReferencePointCsvModel::getSloid));
        List<ReferencePointCsvModelContainer> result = new ArrayList<>(
                groupedReferencePoints.entrySet().stream().map(toContainer()).toList());
        mergeReferencePoints(result);
        return result;
    }

    private void mergeReferencePoints(List<ReferencePointCsvModelContainer> referencePointCsvModelContainers) {
        mergeSequentialEqualsVersions(referencePointCsvModelContainers);
        mergeEqualsVersions(referencePointCsvModelContainers);
    }

    private static Function<Map.Entry<String, List<ReferencePointCsvModel>>, ReferencePointCsvModelContainer> toContainer() {
        return entry -> ReferencePointCsvModelContainer.builder()
                .sloid(entry.getKey())
                .csvModels(entry.getValue())
                .build();
    }

    private void mergeSequentialEqualsVersions(List<ReferencePointCsvModelContainer> csvModelContainers) {
        log.info("Starting checking sequential equals ReferencePoint versions...");
        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<ReferencePointCsvModel> prmCsvMergeResult = mergeSequentialEqualVersions(
                            container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });
        log.info("Total merged sequential ReferencePoint versions {}", mergedSloids.size());
        log.info("Merged ReferencePoint Sloids {}", mergedSloids);
    }

    private void mergeEqualsVersions(List<ReferencePointCsvModelContainer> csvModelContainers) {
        log.info("Starting checking equals ReferencePoint versions...");

        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<ReferencePointCsvModel> prmCsvMergeResult = mergeEqualVersions(container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });

        log.info("Total Merged equals ReferencePoint versions {}", mergedSloids.size());
        log.info("Merged equals ReferencePoint Sloids {}", mergedSloids);
    }

}
