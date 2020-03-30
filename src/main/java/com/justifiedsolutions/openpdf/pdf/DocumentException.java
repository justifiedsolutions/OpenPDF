package com.justifiedsolutions.openpdf.pdf;

/**
 * An {@link Exception} thrown when some illegal action or state is triggered with a {@link
 * Document}.
 */
public class DocumentException extends Exception {

    public DocumentException() {
        super();
    }

    public DocumentException(String message) {
        super(message);
    }

    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentException(Throwable cause) {
        super(cause);
    }
}
