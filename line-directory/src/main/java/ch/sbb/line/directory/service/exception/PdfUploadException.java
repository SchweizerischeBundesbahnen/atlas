package ch.sbb.line.directory.service.exception;

public class PdfUploadException extends RuntimeException {

    public PdfUploadException(String message) { super(message);}

    public PdfUploadException(Exception exception) {
        super(exception);
    }

    public PdfUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
