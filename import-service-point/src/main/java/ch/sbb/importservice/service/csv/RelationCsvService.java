package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
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
public class RelationCsvService extends PrmCsvService<RelationCsvModel>{

    public static final String PRM_RELATION_FILE_NAME = "PRM_CONNECTIONS";

    protected RelationCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
        super(fileHelperService, jobHelperService);
    }

    @Override
    protected CsvFileNameModel csvFileNameModel() {
        return CsvFileNameModel.builder()
                .fileName(PRM_RELATION_FILE_NAME)
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
    protected Class<RelationCsvModel> getType() {
        return RelationCsvModel.class;
    }

    public List<RelationCsvModelContainer> mapToRelationCsvModelContainers(List<RelationCsvModel> relationCsvModels) {
        Map<String, List<RelationCsvModel>> groupedRelations = filterForActive(relationCsvModels).stream()
                .collect(Collectors.groupingBy(RelationCsvModel::getSloid));
        List<RelationCsvModelContainer> result = new ArrayList<>(
                groupedRelations.entrySet().stream().map(toContainer()).toList());
        mergeRelations(result);
        return result;
    }

    private void mergeRelations(List<RelationCsvModelContainer> relationCsvModelContainers) {
        mergeSequentialEqualsVersions(relationCsvModelContainers);
        mergeEqualsVersions(relationCsvModelContainers);
    }

    private static Function<Map.Entry<String, List<RelationCsvModel>>, RelationCsvModelContainer> toContainer() {
        return entry -> RelationCsvModelContainer.builder()
                .sloid(entry.getKey())
                .csvModels(entry.getValue())
                .build();
    }

    private void mergeSequentialEqualsVersions(List<RelationCsvModelContainer> csvModelContainers) {
        log.info("Starting checking sequential equals ReferencePoint versions...");
        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<RelationCsvModel> prmCsvMergeResult = mergeSequentialEqualVersions(
                            container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });
        log.info("Total merged sequential ReferencePoint versions {}", mergedSloids.size());
        log.info("Merged ReferencePoint Sloids {}", mergedSloids);
    }

    private void mergeEqualsVersions(List<RelationCsvModelContainer> csvModelContainers) {
        log.info("Starting checking equals ReferencePoint versions...");

        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<RelationCsvModel> prmCsvMergeResult = mergeEqualVersions(container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });

        log.info("Total Merged equals ReferencePoint versions {}", mergedSloids.size());
        log.info("Merged equals ReferencePoint Sloids {}", mergedSloids);
    }

}
