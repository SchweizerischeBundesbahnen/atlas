package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.export.ExportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessOrganisationAmazonService {

    private static final int OUT_BUFFER = 4096;
    private static final int IN_BUFFER = 1024;

    private final AmazonService amazonService;

    public StreamingResponseBody streamingJsonFile(ExportType exportType) {
        String fileToDownload = getJsonFileToDownload(exportType);
        try {
            File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
            byte[] bytes = decompressGzipToBytes(file.toPath());
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return writeOutputStream(file, inputStream);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

//    public StreamingResponseBody streamingGzipFile(ExportType exportType, ExportFileName exportFileName) {
    public StreamingResponseBody streamingGzipFile(ExportType exportType) {
        String fileToDownload = getJsonFileToDownload(exportType);
        try {
            File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
            InputStream inputStream = new FileInputStream(file);
            return writeOutputStream(file, inputStream);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    byte[] decompressGzipToBytes(Path source) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(source.toFile()))) {
            byte[] buffer = new byte[IN_BUFFER];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
        }
        return output.toByteArray();
    }

    private StreamingResponseBody writeOutputStream(File file, InputStream inputStream) {
        return outputStream -> {
            int len;
            byte[] data = new byte[OUT_BUFFER];
            while ((len = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, len);
            }
            inputStream.close();
            file.delete();
        };
    }

    private String getJsonFileToDownload(ExportType exportType) {
        return "business_organisation/full_business_organisation_versions_2023-08-14.json.gz";
//        return exportFileName.getBaseDir()
//                + "/"
//                + exportType.getDir()
//                + "/"
//                + getBaseFileName(exportType, exportFileName)
//                + ".json.gz";
    }

}