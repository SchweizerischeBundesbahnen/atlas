package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.SpExportFileName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class NotAllowedExportFileException extends AtlasException {


    private SpExportFileName exportFileName;
    private ExportType exportType;

    public NotAllowedExportFileException(SpExportFileName exportFileName, ExportType exportType) {
        this.exportFileName = exportFileName;
        this.exportType = exportType;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Download file [" + exportFileName + "] with export type [" + exportType + "] not allowed!")
                .error("To download the file [" + exportFileName + "] are only allowed the following export types: " + ExportType.getWorldOnly())
                .build();
    }
}
