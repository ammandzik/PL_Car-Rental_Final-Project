package pl.coderslab.carrental.exception;

public class InvoiceCreationNotAllowed extends RuntimeException {
    public InvoiceCreationNotAllowed(String message) {
        super(message);
    }
}
