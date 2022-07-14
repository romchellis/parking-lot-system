package parkinglot.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

import parkinglot.Car;
import parkinglot.ParkingService;
import parkinglot.exception.CarAlreadyRegisteredException;
import parkinglot.exception.CarHasNotRegisteredYetException;
import parkinglot.model.ParkingEvent;
import parkinglot.validation.Validator;

public class MyParkingService implements ParkingService {

    private final int slots; // TODO do not forget to check if more than available slots
    private final Map<Car, LinkedList<ParkingEvent>> carEvents;
    private final Supplier<LocalDateTime> timeGenerator;

    public MyParkingService(int slots,
                            Map<Car, LinkedList<ParkingEvent>> carEvents,
                            Supplier<LocalDateTime> timeGenerator) {
        this.slots = slots;
        this.carEvents = carEvents;
        this.timeGenerator = timeGenerator;
    }

    @Override
    public void register(Car car) {
        Validator.validateNotNull(car);

        carEvents.compute(car, (existingCar, parkingEvents) -> {
            var time = timeGenerator.get();
            if (parkingEvents == null) {
                parkingEvents = new LinkedList<>();
                parkingEvents.add(new ParkingEvent(time));
                return parkingEvents;
            }

            ParkingEvent last = parkingEvents.getLast();

            if (last.isClosed()) {
                parkingEvents.add(new ParkingEvent(time));
            } else {
                throw new CarAlreadyRegisteredException();
            }

            return parkingEvents;
        });
    }

    @Override
    public void out(Car car) {
        Validator.validateNotNull(car);

        carEvents.compute(car, (existingCar, parkingEvents) -> {
            var time = timeGenerator.get();
            if (parkingEvents == null) {
                throw new CarHasNotRegisteredYetException();
            }

            ParkingEvent last = parkingEvents.getLast();

            if (!last.isClosed()) {
                last.close(time);
            } else {
                throw new CarHasNotRegisteredYetException();
            }

            return parkingEvents;
        });
    }

    @Override
    public long getBusySlotsCount() {
        return carEvents.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .map(LinkedList::getLast)
                .filter(this::beforeNowAndNotClosed)
                .count();
    }

    private boolean beforeNowAndNotClosed(ParkingEvent event) {
        LocalDateTime now = timeGenerator.get();
        LocalDateTime enterTime = event.getEnterTime();
        return now.isBefore(enterTime) && !event.isClosed();
    }

    @Override
    public long getHitsInInterval(LocalDateTime from, LocalDateTime to) {
        return carEvents.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(ParkingEvent::getEnterTime)
                .filter(entertime -> isBeforeInclusive(to, entertime) && isAfterInclusive(from, entertime))
                .count();
    }

    private boolean isAfterInclusive(LocalDateTime from, LocalDateTime entertime) {
        return entertime.compareTo(from) >= 0;
    }

    private boolean isBeforeInclusive(LocalDateTime to, LocalDateTime entertime) {
        return entertime.compareTo(to) <= 0;
    }

}
