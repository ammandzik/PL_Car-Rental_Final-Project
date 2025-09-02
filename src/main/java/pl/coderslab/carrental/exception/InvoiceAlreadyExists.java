package pl.coderslab.carrental.exception;

public class InvoiceAlreadyExists extends RuntimeException {
    public InvoiceAlreadyExists(String message) {
        super(message);
    }
}
