package parkinglot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Supplier;

import parkinglot.exception.CarAlreadyRegisteredException;
import parkinglot.impl.MyParkingService;
import parkinglot.model.ParkingEvent;

public class ParkingServiceTest {

    public static final String NUMBER = "ОО001ОРУС";
    public static final Integer SLOTS_COUNT = 5;
    private ParkingService parkingService;
    private final LocalDateTime closeTime = LocalDateTime.of(2000,
            2,
            2,
            2,
            2,
            2,
            2);
    private Supplier<LocalDateTime> timeGenerator = () -> LocalDateTime.of(2000,
            1,
            1,
            1,
            1,
            1,
            1);

    @Test
    void should_successfully_register_car() {
        var car = new Car(NUMBER);
        var map = new HashMap<Car, LinkedList<ParkingEvent>>();
        var expectedList = getExpectedEvents();

        parkingService = new MyParkingService(SLOTS_COUNT, map, timeGenerator);

        assertThatNoException().isThrownBy(() -> parkingService.register(car));
        assertThat(map).containsExactly(
                entry(car, expectedList)
        );
    }

    @Test
    void should_reject_already_registered_car() {
        var car = new Car(NUMBER);
        var map = new HashMap<Car, LinkedList<ParkingEvent>>();
        var events = new LinkedList<ParkingEvent>();
        events.add(new ParkingEvent(timeGenerator.get()));
        map.put(car, events);

        parkingService = new MyParkingService(SLOTS_COUNT, map, timeGenerator);

        assertThatExceptionOfType(CarAlreadyRegisteredException.class)
                .isThrownBy(() -> parkingService.register(car))
                .withMessage("Car is already registered");
    }


    @Test
    void should_close_parking_event() {
        var car = new Car(NUMBER);
        var map = getSourceCarEvents(car);
        var expectedEvents = expectedEventsAfterClose();
        timeGenerator = () -> closeTime;
        parkingService = new MyParkingService(SLOTS_COUNT, map, timeGenerator);

        parkingService.out(car);

        assertThat(map).containsExactly(
                entry(car, expectedEvents)
        );
    }

    private LinkedList<ParkingEvent> getExpectedEvents() {
        LinkedList<ParkingEvent> parkingEvents = new LinkedList<>();
        ParkingEvent expectedParkingEvent = new ParkingEvent(timeGenerator.get());
        parkingEvents.add(expectedParkingEvent);
        return parkingEvents;
    }

    private LinkedList<ParkingEvent> expectedEventsAfterClose() {
        ParkingEvent expectedEvent = new ParkingEvent(timeGenerator.get());
        expectedEvent.close(closeTime);
        LinkedList<ParkingEvent> parkingEvents = new LinkedList<>();
        parkingEvents.add(expectedEvent);
        return parkingEvents;
    }

    private HashMap<Car, LinkedList<ParkingEvent>> getSourceCarEvents(Car car) {
        HashMap<Car, LinkedList<ParkingEvent>> map = new HashMap<>();
        var events = new LinkedList<ParkingEvent>();
        ParkingEvent inEnterEvent = new ParkingEvent(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1));
        events.add(inEnterEvent);
        map.put(car, events);
        return map;
    }

    @Test
    void should_return_busy_slots_count() {
        long expectedBusySlots = 2;
        var map = getCarEventsWithThreeEvents();
        timeGenerator = () -> LocalDateTime.of(2000, 1, 1, 1, 1, 1, 3);
        parkingService = new MyParkingService(10, map, timeGenerator);
        long busySlotsCount = parkingService.getBusySlotsCount();

        assertThat(busySlotsCount).isEqualTo(expectedBusySlots);

    }

    @Test
    void should_return_expected_hits_in_interval() {
        var map = getCarEventsWithThreeEvents();
        parkingService = new MyParkingService(10, map, timeGenerator);
        var from = LocalDateTime.of(2000, 1, 1, 1, 1, 1, 2);
        var to = LocalDateTime.of(2000, 1, 1, 1, 1, 1, 4);

        long hitsInInterval = parkingService.getHitsInInterval(from, to);

        //or 3
        assertThat(hitsInInterval).isEqualTo(4);
    }

    private HashMap<Car, LinkedList<ParkingEvent>> getCarEventsWithThreeEvents() {
        HashMap<Car, LinkedList<ParkingEvent>> carEvents = new HashMap<>();
        for (int i = 1; i < 4; i++) {
            var car = new Car(String.valueOf(i));
            // time: 3
            // car 1: enter1 exit2 | enter3
            // car 2: enter2 exit3 | enter4
            // car 3: enter3 exit4 | enter5
            var enterTime = LocalDateTime.of(2000, 1, 1, 1, 1, 1, i);
            var event = new ParkingEvent(enterTime);
            event.close(enterTime.plusNanos(1));
            var secondEvent = new ParkingEvent(enterTime.plusNanos(2));
            var events = new LinkedList<ParkingEvent>();
            events.add(event);
            events.add(secondEvent);
            carEvents.put(car, events);
        }

        return carEvents;
    }
}