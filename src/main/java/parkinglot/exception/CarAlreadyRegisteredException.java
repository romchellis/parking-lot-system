package parkinglot.exception;

public class CarAlreadyRegisteredException extends IllegalArgumentException {

    public CarAlreadyRegisteredException() {
        super("Car is already registered");
    }

}
