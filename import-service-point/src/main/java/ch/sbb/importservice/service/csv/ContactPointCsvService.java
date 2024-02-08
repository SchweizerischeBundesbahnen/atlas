package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
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
public class ContactPointCsvService extends PrmCsvService<ContactPointCsvModel>{
    //TODO: Check if Filenames are correct
    public static final String PRM_CONTACT_POINT_FILE_NAME_TICKET_COUNTER = "PRM_TICKET_COUNTER";
    public static final String PRM_CONTACT_POINT_FILE_NAME_INFO_DESK = "PRM_INFO_DESK";

    protected ContactPointCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
        super(fileHelperService, jobHelperService);
    }

    @Override
    protected CsvFileNameModel csvFileNameModel() {
        return CsvFileNameModel.builder()
                .fileName(PRM_CONTACT_POINT_FILE_NAME_TICKET_COUNTER)
                .fileName(PRM_CONTACT_POINT_FILE_NAME_INFO_DESK)
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
        return JobDescriptionConstants.IMPORT_CONTACT_POINT_CSV_JOB_NAME;
    }

    @Override
    protected Class<ContactPointCsvModel> getType() {
        return ContactPointCsvModel.class;
    }

    private static Function<Map.Entry<String, List<ContactPointCsvModel>>, ContactPointCsvModelContainer> toContainer() {
        return entry -> ContactPointCsvModelContainer.builder()
                .sloid(entry.getKey())
                .csvModels(entry.getValue())
                .build();
    }

    public List<ContactPointCsvModelContainer> mapToReferencePointCsvModelContainers(List<ContactPointCsvModel> contactPointCsvModels) {
        Map<String, List<ContactPointCsvModel>> groupedContactPoints = filterForActive(contactPointCsvModels).stream()
                .collect(Collectors.groupingBy(ContactPointCsvModel::getSloid));
        List<ContactPointCsvModelContainer> result = new ArrayList<>(
                groupedContactPoints.entrySet().stream().map(toContainer()).toList());
        mergeReferencePoints(result);
        return result;
    }

    private void mergeReferencePoints(List<ContactPointCsvModelContainer> contactPointCsvModelContainers) {
        mergeSequentialEqualsVersions(contactPointCsvModelContainers);
        mergeEqualsVersions(contactPointCsvModelContainers);
    }

    private void mergeSequentialEqualsVersions(List<ContactPointCsvModelContainer> csvModelContainers) {
        log.info("Starting checking sequential equals ContactPoint versions...");
        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<ContactPointCsvModel> prmCsvMergeResult = mergeSequentialEqualVersions(
                            container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });
        log.info("Total merged sequential ContactPoint versions {}", mergedSloids.size());
        log.info("Merged Contact Point Sloids {}", mergedSloids);
    }

    private void mergeEqualsVersions(List<ContactPointCsvModelContainer> csvModelContainers) {
        log.info("Starting checking equals ContactPoint versions...");

        List<String> mergedSloids = new ArrayList<>();
        csvModelContainers.forEach(
                container -> {
                    PrmCsvMergeResult<ContactPointCsvModel> prmCsvMergeResult = mergeEqualVersions(container.getCsvModels());
                    container.setCsvModels(prmCsvMergeResult.getVersions());
                    mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
                });

        log.info("Total Merged equals ContactPoint versions {}", mergedSloids.size());
        log.info("Merged equals ContactPoint Sloids {}", mergedSloids);
    }
}
