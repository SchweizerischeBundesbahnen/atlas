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

    public List<URL> uploadPdfFile(List<File> files) {
        List<URL> urls = new ArrayList<>();
        files.forEach(file -> {
            try {
                URL url = amazonService.putFile(AmazonBucket.HEARING_DOCUMENT, file, "timetablehearing");
                urls.add(url);
            } catch (IOException e) {
                throw new PdfUploadException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.HEARING_DOCUMENT, e);
            }
        });
        return urls;
    }

}
