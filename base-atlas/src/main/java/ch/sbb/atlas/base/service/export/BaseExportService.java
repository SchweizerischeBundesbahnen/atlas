package ch.sbb.atlas.base.service.export;

import ch.sbb.atlas.base.service.amazon.service.AmazonService;
import ch.sbb.atlas.base.service.amazon.service.FileService;
import ch.sbb.atlas.base.service.export.model.VersionCsvModel;
import ch.sbb.atlas.base.service.model.api.AtlasApiConstants;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.exception.ExportException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseExportService<T extends BaseVersion> {

    private static final char UTF_8_BYTE_ORDER_MARK = '\uFEFF';

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
            URL url = amazonService.putFile(csvFile, getDirectory());
            log.info("Export - CSV File {} Successfully Put to the directory {}: {}", csvFile.getName(), getDirectory(), url);
            return url;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ExportException(csvFile, e);
        }
    }

    URL putZipFile(File zipFile) {
        try {
            URL url = amazonService.putZipFile(zipFile, getDirectory());
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
        try (BufferedWriter bufferedWriter = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8));
            SequenceWriter sequenceWriter = objectWriter.writeValues(bufferedWriter)) {
            bufferedWriter.write(UTF_8_BYTE_ORDER_MARK);
            sequenceWriter.writeAll(versionCsvModels);
            return csvFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ExportException(csvFile, e);
        }
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

    @Getter
    public static class AtlasCsvMapper {

        private final ObjectWriter objectWriter;

        public AtlasCsvMapper(Class<?> aClass) {
            CsvMapper csvMapper = createCsvMapper();
            CsvSchema csvSchema = csvMapper.schemaFor(aClass).withHeader().withColumnSeparator(';');
            this.objectWriter = csvMapper.writerFor(aClass).with(csvSchema);
        }

        private CsvMapper createCsvMapper() {
            CsvMapper mapper = new CsvMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper;
        }

    }

}
