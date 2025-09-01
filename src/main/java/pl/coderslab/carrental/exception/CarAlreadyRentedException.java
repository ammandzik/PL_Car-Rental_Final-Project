package pl.coderslab.carrental.exception;

public class CarAlreadyRentedException extends RuntimeException {

    public CarAlreadyRentedException(String message) {
        super(message);
    }
}
