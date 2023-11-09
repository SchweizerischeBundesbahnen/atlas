package ch.sbb.atlas.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BaseExportServiceTest {

    private URL someUrl;
    private File file;

    private BaseExportService<?> baseExportService = Mockito.mock(
            BaseExportService.class,
            Mockito.CALLS_REAL_METHODS);

    @BeforeEach
    void setUp() throws IOException {
        someUrl = new URL("http://example.com");
        file = new File("demo.txt");
    }

    @Test
    void shouldExportFullVersions() {
        BaseExportService<?> bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFullVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());

        List<URL> urls = bes.exportFullVersions();
        assertEquals(1, urls.size());
    }

    @Test
    void shouldExportFullVersionsAllFormats() {
        BaseExportService<?> bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFullVersionsJson();
        Mockito.doReturn(file).when(bes).getFullVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());
        Mockito.doReturn(someUrl).when(bes).putGzFile(any());

        List<URL> urls = bes.exportFullVersionsAllFormats();
        assertEquals(2, urls.size());
    }

    @Test
    void shouldExportActualVersions() {
        BaseExportService<?> bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getActualVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());

        List<URL> urls = bes.exportActualVersions();
        assertEquals(1, urls.size());
    }

    @Test
    void shouldExportActualVersionsAllFormats() {
        BaseExportService<?> bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getActualVersionsCsv();
        Mockito.doReturn(file).when(bes).getActualVersionsJson();
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());
        Mockito.doReturn(someUrl).when(bes).putGzFile(any());

        List<URL> urls = bes.exportActualVersionsAllFormats();
        assertEquals(2, urls.size());
    }

    @Test
    void shouldExportFutureTimetableVersions() {
        BaseExportService<?> bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFutureTimetableVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());

        List<URL> urls = bes.exportFutureTimetableVersions();
        assertEquals(1, urls.size());
    }

    @Test
    void shouldExportFutureTimetableVersionsAllFormats() {
        BaseExportService<?> bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFutureTimetableVersionsCsv();
        Mockito.doReturn(file).when(bes).getFutureTimetableVersionsJson();
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());
        Mockito.doReturn(someUrl).when(bes).putGzFile(any());

        List<URL> urls = bes.exportFutureTimetableVersionsAllFormats();
        assertEquals(2, urls.size());
    }

    @Test
    void shouldCreateJsonFile() throws IOException {
        File jsonFile = new File("demo.json");
        List<BaseVersionModel> baseVersionModelList = new ArrayList<>();
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(baseVersionModelList).when(bes).convertToJsonModel(any());
        Mockito.doReturn(jsonFile).when(bes).createFile(any(), any());

        File jsonFileResult = bes.createJsonFile(baseVersionModelList, ExportType.FULL);
        assertNotNull(jsonFileResult);
        assertEquals(jsonFile, jsonFileResult);
        Files.delete(jsonFile.toPath());
    }

    @Test
    void shouldCreateCsvFile() throws IOException {
        File csvFile = new File("demo.csv");
        List<VersionCsvModel> versionCsvModels = new ArrayList<>();
        BaseExportService bes = Mockito.spy(baseExportService);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer();

        Mockito.doReturn(versionCsvModels).when(bes).convertToCsvModel(any());
        Mockito.doReturn(csvFile).when(bes).createFile(any(), any());
        Mockito.doReturn(writer).when(bes).getObjectWriter();

        File csvFileResult = bes.createCsvFile(versionCsvModels, ExportType.FULL);
        assertNotNull(csvFileResult);
        assertEquals(csvFile, csvFileResult);
        Files.delete(csvFile.toPath());
    }

}
