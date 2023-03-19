package ch.sbb.line.directory.service.exception;

public class PdfDocumentConstraintViolationException extends RuntimeException {

    public PdfDocumentConstraintViolationException(String message) { super(message);}

    public PdfDocumentConstraintViolationException(Exception exception) {
        super(exception);
    }

}
