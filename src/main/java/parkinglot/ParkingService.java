package parkinglot;

import java.time.LocalDateTime;

/**
 * We have to implement Parking service API which includes:
 * 1) Register car
 * 2) Out car
 * 3) Get count of busy slots in currentMoment
 * 4) Get hits in time interval ( from today 14.30 to 16.00 was 25 hits)
 */
public interface ParkingService {
    void register(Car car);

    void out(Car car);

    long getBusySlotsCount();

    //only enter
    long getHitsInInterval(LocalDateTime from, LocalDateTime to);
}
