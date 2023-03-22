package ch.sbb.line.directory.service.upload;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.line.directory.service.exception.PdfUploadException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimetableHearingPdfsUploadService {

    private final AmazonService amazonService;

    public List<URL> uploadPdfFile(List<File> files, String dirName) {
        List<URL> urls = new ArrayList<>();
        files.forEach(file -> {
            try {
                URL url = amazonService.putFile(AmazonBucket.HEARING_DOCUMENT, file, dirName);
                urls.add(url);
            } catch (IOException e) {
                throw new PdfUploadException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.HEARING_DOCUMENT, e);
            }
        });
        return urls;
    }

    public void deletePdfFile(String dirName, String fileName) {
        amazonService.deleteFile(AmazonBucket.HEARING_DOCUMENT, dirName + "/" +fileName);
    }

    public File downloadPdfFile(String dirName, String fileName) {
        try {
            return amazonService.pullFile(AmazonBucket.HEARING_DOCUMENT, dirName + "/" +fileName);
        } catch (IOException e) {
            throw new PdfUploadException("Error downloading file: " + fileName + " to bucket: " + AmazonBucket.HEARING_DOCUMENT, e);
        }

    }

}
