package com.example.mmt.entity;

public class Flight implements Comparable<Flight>{

    private String flightNumber;
    private String source;
    private String destination;
    private String startTime;
    private String endTime;
    private long durationInMinute;
    private boolean isDirect;

    @Override
    public int compareTo(Flight o) {

        if(this.isDirect && !o.isDirect) {
            return -1;
        } else if(!this.isDirect && o.isDirect) {
            return 1;
        } else {
            return ((this.durationInMinute - o.durationInMinute) > 0 ? 1 : -1);
        }
    }

    public boolean equals(Object o) {
        if(o == this)
            return true;

        if (!(o instanceof Flight))
            return false;

        Flight other = (Flight)o;
        boolean flightNumberEquals = (this.flightNumber != null && other.flightNumber != null)
                && (this.flightNumber.equalsIgnoreCase(other.flightNumber));
        return flightNumberEquals;
    }

    public final int hashCode() {
        int result = 17;

        if (flightNumber != null) {
            result = 31 * result + flightNumber.hashCode();
        }

        if (source != null) {
            result = 31 * result + source.hashCode();
        }

        if (destination != null) {
            result = 31 * result + destination.hashCode();
        }

        return result;
    }


    public String toString() {
        return flightNumber + " " + source + " " + destination + " " + startTime + " " + endTime + " " + isDirect;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getDurationInMinute() {
        return durationInMinute;
    }

    public void setDurationInMinute(long durationInMinute) {
        this.durationInMinute = durationInMinute;
    }

}
