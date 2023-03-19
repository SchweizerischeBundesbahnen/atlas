package ch.sbb.line.directory.service.hearing;

import ch.sbb.line.directory.service.exception.FileException;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class TikaService {

    private final Tika tika = new Tika();

    /**
     * @param file which will be checked if it is a pdf
     * @return the file name if check was unsuccessful, otherwise empty optional
     * @throws FileException if something with parsing the file went wrong
     */
    public Optional<String> checkForPdf(File file) {
        String detectedMediaType = null;
        try {
            detectedMediaType = tika.detect(file);
        } catch (IOException e) {
            throw new FileException("PDF media type detection failed ", e);
        }
        if (!detectedMediaType.equals(MediaType.APPLICATION_PDF_VALUE)) {
            return Optional.of(file.getName());
        }
        return Optional.empty();
    }

}
