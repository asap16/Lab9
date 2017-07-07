package lab9;

import lab9.Flight;
import lab9.GMTtime;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.PriorityQueue;
import java.util.Comparator;

public class ShortestFlight {

    private static Map<String, Integer> airportTimeZoneMap = new HashMap<String, Integer>();
    private static List<Flight> flights = new ArrayList<Flight>();

    private static Map<String, List<Flight>> adjacencyMap = new HashMap<String, List<Flight>>();
    

    private static void findBestRoute(String airportOrigin,
				      String airportDestination,
				      GMTtime leaveTime) {
    	for (Flight f : flights){
    		if (!adjacencyMap.containsKey(f.departureAirportCode)){
    		    List<Flight> adjacentList = new ArrayList<Flight>();
    			adjacentList.add(f);
    			adjacencyMap.put(f.departureAirportCode, adjacentList);
    		}
    		else if (adjacencyMap.containsKey(f.departureAirportCode)){
    			List<Flight> adjList = adjacencyMap.get(f.departureAirportCode);
    			adjList.add(f);
    		}
    	}
    	Map<String, Flight> airportFlightIn = new HashMap<String, Flight>();
    	Map<String, Integer> airportTimeOrigin = new HashMap<String, Integer>();
    	
    	Comparator<String> airportComparator = new Comparator<String>() {
    		public int compare(String airport1, String airport2) {
    			if (!airportTimeOrigin.containsKey(airport1)) {
    				if (!airportTimeOrigin.containsKey(airport2)) {
    					return 0;
    				}
    				return 1;
    			}else {
    				if (!airportTimeOrigin.containsKey(airport2)) {
    					return -1;
    				}
    				return (airportTimeOrigin.get(airport1) - airportTimeOrigin.get(airport2));

    			}
    		}
    	};

    	PriorityQueue<String> q = new PriorityQueue<String>(adjacencyMap.get(airportOrigin).size(), airportComparator);
    	
    	airportFlightIn.put(airportOrigin, null);
    	q.add(airportOrigin);
    	airportTimeOrigin.put(airportOrigin, 0);
    	int source = leaveTime.gmtInMinutes;
    	int min = 2147483647;
    	int count = 0;
    	String airport = "";
    	while (q.size() > 0 && (airport != airportDestination)){
    		System.out.println("out");
    		airport = q.poll();

    		if (count != 0){
    			source = airportFlightIn.get(airport).arrivalTime.gmtInMinutes;	
    		}
    		List<Flight> origin = adjacencyMap.get(airport);

    		for (Flight fs : origin){
    			if (fs.departureTime.gmtInMinutes - source >= 60){
    				int val = fs.arrivalTime.gmtInMinutes - leaveTime.gmtInMinutes;
    				if (val < min && airportFlightIn.containsKey(fs.arrivalAirportCode)){
    					airportFlightIn.put(fs.arrivalAirportCode, fs);
    					airportTimeOrigin.put(fs.arrivalAirportCode, val);
    					q.add(fs.arrivalAirportCode);
    					min = val;
    				}else if (!airportFlightIn.containsKey(fs.arrivalAirportCode)){
    					airportFlightIn.put(fs.arrivalAirportCode, fs);
    					airportTimeOrigin.put(fs.arrivalAirportCode, val);
    					q.add(fs.arrivalAirportCode);
    					min = fs.arrivalTime.gmtInMinutes - leaveTime.gmtInMinutes; 
    				}
    			}
    		}
    		count++;

    	}
    	String air = "";
    	String result = "";
    	String destiny = airportDestination;
    	while (air != airportOrigin){
    		Flight seeFlight = airportFlightIn.get(destiny);
    		air = seeFlight.departureAirportCode;
    		destiny = air;
    		result = air + " " + result;
    	}
    	System.out.println(result);



	// TODO: Implement here your code to find the earliest arrival route from
	// origin to destination.
	// Remember the constraints: the flight must leave no eariler than 1 hour
	// after leave time, it must have at least one layover, and each layover
	// needs to be at least 1 hour.
    }

    public static void main(String[] args) {
	if (args.length != 4) {
	    System.out.println("Usage: AirportOrigin AirportDestination Time A/P");
	    return;
	}
	readAirportData();
	readFlightData(airportTimeZoneMap);

	String airportOrigin = args[0];
	String airportDestination = args[1];
	GMTtime leaveTime = new GMTtime(Integer.parseInt(args[2]),
					airportTimeZoneMap.get(airportOrigin),
					args[3].startsWith("A"));

	findBestRoute(airportOrigin, airportDestination, leaveTime);
    }

    private static void readAirportData() {
	BufferedReader r;
	try {
	    InputStream is = new FileInputStream("input/airport-data.txt");
	    r = new BufferedReader(new InputStreamReader(is));
	} catch (IOException e) {
	    System.out.println("IOException while opening airport-data.txt\n" + e);
	    return;
	}
	try {
	    String nextline = r.readLine();
	    StringTokenizer st = new StringTokenizer(nextline);
	    int numAirports = Integer.parseInt(st.nextToken());
	    for (int i = 0; i < numAirports; i++){
		nextline = r.readLine();
		st = new StringTokenizer(nextline);
		String airportCode = st.nextToken();
		int gmtConv = Integer.parseInt(st.nextToken());
		airportTimeZoneMap.put(airportCode, gmtConv);
	    }
	} catch (IOException e) {
	    System.out.println("IOException while reading sequence from " +
			       "airport-data.txt\n" + e);
	    return;
	}
    }

    private static void readFlightData(Map<String, Integer> airportTimeZoneMap) {
	BufferedReader r;
	try {
	    InputStream is = new FileInputStream("input/flight-data.txt");
	    r = new BufferedReader(new InputStreamReader(is));
	} catch (IOException e) {
	    System.out.println("IOException while opening flight-data.txt\n" + e);
	    return;
	}
	try {
	    String nextline = r.readLine();
	    while (nextline != null && !nextline.trim().equals("")) { // not end of file or an empty line
		StringTokenizer st = new StringTokenizer(nextline);
		Flight flight = new Flight();
		flight.airline = st.nextToken();
		flight.flightNum = Integer.parseInt(st.nextToken());
		flight.departureAirportCode = st.nextToken();
		int localDepartureTime = Integer.parseInt(st.nextToken()); 
		boolean amDeparture = st.nextToken().equals("A");
		flight.departureTime = new GMTtime(localDepartureTime, airportTimeZoneMap.get(flight.departureAirportCode), amDeparture);
		flight.arrivalAirportCode = st.nextToken();
		int localArrivalTime = Integer.parseInt(st.nextToken());
		boolean amArrival = st.nextToken().equals("A");
		flight.arrivalTime = new GMTtime(localArrivalTime, airportTimeZoneMap.get(flight.arrivalAirportCode), amArrival);
		nextline = r.readLine();
		flights.add(flight);
	    }
	} catch (IOException e) {
	    System.out.println("IOException while reading sequence from " +
			       "flight-data.txt\n" + e);
	    return;
	}
    }
}
