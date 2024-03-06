package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;
import static java.util.Comparator.comparing;

import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.importservice.entity.RelationKeyId;
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
        return JobDescriptionConstants.IMPORT_RELATION_CSV_JOB_NAME;
    }

    @Override
    protected Class<RelationCsvModel> getType() {
        return RelationCsvModel.class;
    }

    public List<RelationCsvModelContainer> mapToRelationCsvModelContainers(List<RelationCsvModel> relationCsvModels) {

        Map<RelationKeyId, List<RelationCsvModel>> groupedRelations = filterForActive(relationCsvModels).stream()
                .collect(Collectors.groupingBy(model -> new RelationKeyId(model.getRpSloid(), model.getSloid())));

        List<RelationCsvModelContainer> result = new ArrayList<>(
                groupedRelations.entrySet().stream().map(toContainer()).toList());
        mergeRelations(result);
        return result;
    }

    private void mergeRelations(List<RelationCsvModelContainer> relationCsvModelContainers) {
        mergeSequentialEqualsVersions(relationCsvModelContainers);
        mergeEqualsVersions(relationCsvModelContainers);
    }

    private static Function<Map.Entry<RelationKeyId, List<RelationCsvModel>>, RelationCsvModelContainer> toContainer() {
        return entry -> RelationCsvModelContainer.builder()
                .sloid(entry.getKey().getSloid())
                .csvModels(entry.getValue())
                .build();
    }

    private void mergeSequentialEqualsVersions(List<RelationCsvModelContainer> relationCsvModelContainers) {
        log.info("Starting checking sequential equals Relation versions...");
        List<String> mergedSloids = new ArrayList<>();
        relationCsvModelContainers.forEach(
                container -> container.setCsvModels(mergeSequentialEqualsRelationVersions(container.getCsvModels(),
                        mergedSloids)));
        log.info("Total Merged sequential Relation versions {}", mergedSloids.size());
        log.info("Merged Relation Didok numbers {}", mergedSloids);
    }

    private void mergeEqualsVersions(List<RelationCsvModelContainer> relationCsvModelContainers) {
        log.info("Starting checking equals Relation versions...");
        List<String> mergedSloids = new ArrayList<>();
        relationCsvModelContainers.forEach(
                container -> container.setCsvModels(mergeEqualsRelationVersions(container.getCsvModels(),
                        mergedSloids)));
        log.info("Total Merged equals Relation versions {}", mergedSloids.size());
        log.info("Merged equals Relation sloids {}", mergedSloids);
    }

    private List<RelationCsvModel> mergeSequentialEqualsRelationVersions(List<RelationCsvModel> relationCsvModels,
                                                                           List<String> mergedSloids) {
        List<RelationCsvModel> relationCsvModelListMerged = new ArrayList<>();
        if (relationCsvModels.size() == 1) {
            return relationCsvModels;
        }
        if (relationCsvModels.size() > 1) {
            relationCsvModels.sort(comparing(RelationCsvModel::getValidFrom));
            relationCsvModelListMerged = new ArrayList<>(List.of(relationCsvModels.get(0)));
            for (int currentIndex = 1; currentIndex < relationCsvModels.size(); currentIndex++) {
                final RelationCsvModel previous = relationCsvModelListMerged.get(relationCsvModelListMerged.size() - 1);
                final RelationCsvModel current = relationCsvModels.get(currentIndex);
                if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom())
                        && current.equals(previous)) {
                    removeCurrentVersionIncreaseNextValidTo(previous, current);
                    mergedSloids.add(current.getSloid());
                } else {
                    relationCsvModelListMerged.add(current);
                }
            }
        }
        return relationCsvModelListMerged;
    }

    private List<RelationCsvModel> mergeEqualsRelationVersions(List<RelationCsvModel> relationCsvModels,
                                                                 List<String> mergedSloids) {
        List<RelationCsvModel> relationCsvModelListMerged = new ArrayList<>();
        if (relationCsvModels.size() == 1) {
            return relationCsvModels;
        }
        if (relationCsvModels.size() > 1) {
            relationCsvModels.sort(comparing(RelationCsvModel::getValidFrom));
            relationCsvModelListMerged = new ArrayList<>(
                    List.of(relationCsvModels.get(0))
            );
            for (int currentIndex = 1; currentIndex < relationCsvModels.size(); currentIndex++) {
                final RelationCsvModel previous = relationCsvModelListMerged.get(relationCsvModelListMerged.size() - 1);
                final RelationCsvModel current = relationCsvModels.get(currentIndex);
                if (current.getValidFrom().isEqual(previous.getValidFrom()) && current.getValidTo().isEqual(previous.getValidTo())
                        && current.equals(previous)) {
                    log.info("Found duplicated version with Sloid {}", previous.getSloid());
                    log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
                    log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
                    mergedSloids.add(current.getSloid());
                } else {
                    relationCsvModelListMerged.add(current);
                }
            }
        }
        return relationCsvModelListMerged;
    }

    private void removeCurrentVersionIncreaseNextValidTo(RelationCsvModel previous,
                                                         RelationCsvModel current) {
        log.info("Found versions to merge with sloid {}", previous.getSloid());
        log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
        log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
        previous.setValidTo(current.getValidTo());
        log.info("Version merged [{}]-[{}]", previous.getValidFrom(), current.getValidTo());
    }
}
