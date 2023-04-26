package ch.sbb.atlas.export;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.exception.ExportException;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.entity.BaseVersion;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseExportService<T extends BaseVersion> {

    private final FileService fileService;
    private final AmazonService amazonService;

    public List<URL> exportFullVersions() {
        List<URL> urls = new ArrayList<>();
        File fullVersionsCsv = getFullVersionsCsv();
        urls.add(putCsvFile(fullVersionsCsv));
        urls.add(putZipFile(fullVersionsCsv));
        return urls;
    }

    public List<URL> exportActualVersions() {
        List<URL> urls = new ArrayList<>();
        File actualVersionsCsv = getActualVersionsCsv();
        urls.add(putCsvFile(actualVersionsCsv));
        urls.add(putZipFile(actualVersionsCsv));
        return urls;
    }

    public List<URL> exportFutureTimetableVersions() {
        List<URL> urls = new ArrayList<>();
        File futureTimetableVersionsCsv = getFutureTimetableVersionsCsv();
        urls.add(putCsvFile(futureTimetableVersionsCsv));
        urls.add(putZipFile(futureTimetableVersionsCsv));
        return urls;
    }

    URL putCsvFile(File csvFile) {
        try {
            URL url = amazonService.putFile(AmazonBucket.EXPORT, csvFile, getDirectory());
            log.info("Export - CSV File {} Successfully Put to the directory {}: {}", csvFile.getName(), getDirectory(), url);
            return url;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ExportException(csvFile, e);
        }
    }

    URL putZipFile(File zipFile) {
        try {
            URL url = amazonService.putZipFile(AmazonBucket.EXPORT, zipFile, getDirectory());
            log.info("Export - ZIP File {} Successfully Put to the directory {}: {}", zipFile.getName(), getDirectory(), url);
            return url;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ExportException(zipFile, e);
        }
    }

    protected File createCsvFile(List<T> versions, ExportType exportType) {

        File csvFile = createFile(exportType);

        List<? extends VersionCsvModel> versionCsvModels = convertToCsvModel(versions);

        ObjectWriter objectWriter = getObjectWriter();
        return CsvExportWriter.writeToFile(csvFile, versionCsvModels, objectWriter);
    }

    protected abstract ObjectWriter getObjectWriter();

    protected abstract List<? extends VersionCsvModel> convertToCsvModel(List<T> versions);

    protected abstract String getDirectory();

    protected abstract File getFullVersionsCsv();

    protected abstract File getActualVersionsCsv();

    protected abstract File getFutureTimetableVersionsCsv();

    protected abstract String getFileName();

    protected File createFile(ExportType exportType) {
        String dir = fileService.getDir();
        String actualDate = LocalDate.now()
            .format(DateTimeFormatter.ofPattern(
                AtlasApiConstants.DATE_FORMAT_PATTERN));
        return new File(dir + exportType.getFilePrefix() + getFileName() + actualDate + ".csv");
    }

}
