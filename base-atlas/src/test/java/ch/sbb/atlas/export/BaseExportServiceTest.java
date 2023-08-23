package ch.sbb.atlas.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class BaseExportServiceTest {

    private URL someUrl;
    private File file;

    private BaseExportService baseExportService = Mockito.mock(
            BaseExportService.class,
            Mockito.CALLS_REAL_METHODS);

    @BeforeEach
    public void setUp() throws IOException {
        someUrl = new URL("http://example.com");
        file = new File("demo.txt");
    }

    @Test
    void shouldExportFullVersions() {
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFullVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putCsvFile(any());
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());

        List<URL> urls = bes.exportFullVersions();
        assertEquals(2, urls.size());
        assertEquals("http://example.com", urls.get(0).toString());
        assertEquals("http://example.com", urls.get(1).toString());
    }

    @Test
    void shouldExportFullVersionsAllFormats() {
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFullVersionsJson();
        Mockito.doReturn(file).when(bes).getFullVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putCsvFile(any());
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());
        Mockito.doReturn(someUrl).when(bes).putGzFile(any());

        List<URL> urls = bes.exportFullVersionsAllFormats();
        assertEquals(3, urls.size());
        assertEquals("http://example.com", urls.get(0).toString());
        assertEquals("http://example.com", urls.get(1).toString());
        assertEquals("http://example.com", urls.get(2).toString());
    }

    @Test
    void shouldExportActualVersions() {
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getActualVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putCsvFile(any());
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());

        List<URL> urls = bes.exportActualVersions();
        assertEquals(2, urls.size());
        assertEquals("http://example.com", urls.get(0).toString());
        assertEquals("http://example.com", urls.get(1).toString());
    }

    @Test
    void shouldExportActualVersionsAllFormats() {
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getActualVersionsCsv();
        Mockito.doReturn(file).when(bes).getActualVersionsJson();
        Mockito.doReturn(someUrl).when(bes).putCsvFile(any());
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());
        Mockito.doReturn(someUrl).when(bes).putGzFile(any());

        List<URL> urls = bes.exportActualVersionsAllFormats();
        assertEquals(3, urls.size());
        assertEquals("http://example.com", urls.get(0).toString());
        assertEquals("http://example.com", urls.get(1).toString());
        assertEquals("http://example.com", urls.get(2).toString());
    }

    @Test
    void shouldExportFutureTimetableVersions() {
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFutureTimetableVersionsCsv();
        Mockito.doReturn(someUrl).when(bes).putCsvFile(any());
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());

        List<URL> urls = bes.exportFutureTimetableVersions();
        assertEquals(2, urls.size());
        assertEquals("http://example.com", urls.get(0).toString());
        assertEquals("http://example.com", urls.get(1).toString());
    }

    @Test
    void shouldExportFutureTimetableVersionsAllFormats() {
        BaseExportService bes = Mockito.spy(baseExportService);

        Mockito.doReturn(file).when(bes).getFutureTimetableVersionsCsv();
        Mockito.doReturn(file).when(bes).getFutureTimetableVersionsJson();
        Mockito.doReturn(someUrl).when(bes).putCsvFile(any());
        Mockito.doReturn(someUrl).when(bes).putZipFile(any());
        Mockito.doReturn(someUrl).when(bes).putGzFile(any());

        List<URL> urls = bes.exportFutureTimetableVersionsAllFormats();
        assertEquals(3, urls.size());
        assertEquals("http://example.com", urls.get(0).toString());
        assertEquals("http://example.com", urls.get(1).toString());
        assertEquals("http://example.com", urls.get(2).toString());
    }

}
