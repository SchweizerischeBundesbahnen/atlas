package ch.sbb.line.directory.service.hearing;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class TikaService {

    private final Tika tika = new Tika();

    public Optional<String> checkForPdf(File file) {
        String detectedMediaType;
        try {
            detectedMediaType = tika.detect(file);
        } catch (IOException e) {
            throw new IllegalStateException("PDF media type detection failed ", e);
        }
        if (!detectedMediaType.equals(MediaType.APPLICATION_PDF_VALUE)) {
            return Optional.of(file.getName());
        }
        return Optional.empty();
    }

}
