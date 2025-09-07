package pl.coderslab.carrental.exception;

public class ReservationEditNotAllowed extends RuntimeException {
    public ReservationEditNotAllowed(String message) {
        super(message);
    }
}
