package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;

@Service
@Slf4j
public class ContactPointCsvService extends PrmCsvService<ContactPointCsvModel>{
    private String filename;
    private String jobName;

    protected ContactPointCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
        super(fileHelperService, jobHelperService);
    }

    @Override
    protected CsvFileNameModel csvFileNameModel() {
        return CsvFileNameModel.builder()
                .fileName(filename)
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
        return jobName;
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

    public List<ContactPointCsvModelContainer> mapToContactPointCsvModelContainers(List<ContactPointCsvModel> contactPointCsvModels) {
        Map<String, List<ContactPointCsvModel>> groupedContactPoints = filterForActive(contactPointCsvModels).stream()
                .collect(Collectors.groupingBy(ContactPointCsvModel::getSloid));
        List<ContactPointCsvModelContainer> result = new ArrayList<>(
                groupedContactPoints.entrySet().stream().map(toContainer()).toList());
        mergeContactPoints(result);
        return result;
    }

    private void mergeContactPoints(List<ContactPointCsvModelContainer> contactPointCsvModelContainers) {
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

    public List<ContactPointCsvModel> loadFileFromS3(String filename, String jobname, ContactPointType type){
        setFilename(filename);
        setJobname(jobname);
        List<ContactPointCsvModel> actualContactPointCsvModels = getActualCsvModelsFromS3();

        setContactPointType(actualContactPointCsvModels, type);

        return actualContactPointCsvModels;
    }

    private void setContactPointType(List<ContactPointCsvModel> actualContactPointCsvModels, ContactPointType type){
        for (ContactPointCsvModel contactPoint : actualContactPointCsvModels) {
            contactPoint.setType(type);
        }
    }

    public List<ContactPointCsvModel> loadFromFile(File file, ContactPointType type){
        List<ContactPointCsvModel> actualContactPointCsvModels = getActualCsvModels(file);
        setContactPointType(actualContactPointCsvModels, type);
        return actualContactPointCsvModels;
    }

    public void setFilename(String fileName) {
        this.filename = fileName;
    }
    public void setJobname(String jobName) {
        this.jobName = jobName;
    }
}
