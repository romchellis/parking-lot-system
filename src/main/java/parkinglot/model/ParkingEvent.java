package parkinglot.model;

import java.time.LocalDateTime;

public class ParkingEvent {

    private final LocalDateTime enterTime;

    private LocalDateTime exitTime;
    public ParkingEvent(LocalDateTime enterTime) {
        this.enterTime = enterTime;
    }

    public void close(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public LocalDateTime getEnterTime() {
        return enterTime;
    }

    public boolean isClosed() {
        return exitTime != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingEvent)) return false;

        ParkingEvent that = (ParkingEvent) o;

        if (enterTime != null ? !enterTime.equals(that.enterTime) : that.enterTime != null)
            return false;
        return exitTime != null ? exitTime.equals(that.exitTime) : that.exitTime == null;
    }

    @Override
    public int hashCode() {
        int result = enterTime != null ? enterTime.hashCode() : 0;
        result = 31 * result + (exitTime != null ? exitTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParkingEvent{" +
                "enterTime=" + enterTime +
                ", exitTime=" + exitTime +
                '}';
    }
}
