package parkinglot.exception;

public class CarHasNotRegisteredYetException extends IllegalArgumentException {

    public CarHasNotRegisteredYetException() {
        super("Car has not registered yet");
    }
}
