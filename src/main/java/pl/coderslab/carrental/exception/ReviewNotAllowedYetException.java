package pl.coderslab.carrental.exception;

public class ReviewNotAllowedYetException extends RuntimeException {
    public ReviewNotAllowedYetException(String message) {
        super(message);
    }
}
