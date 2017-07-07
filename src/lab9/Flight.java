package lab9;

import lab9.GMTtime;

public class Flight {
    public String airline;
    public int flightNum;
    public String departureAirportCode;
    public GMTtime departureTime;
    public String arrivalAirportCode;
    public GMTtime arrivalTime;

    public String toString() {
	return ("Flight from " + departureAirportCode + " at " +
		departureTime + " to " + arrivalAirportCode +
		" at " + arrivalTime);
    }
}