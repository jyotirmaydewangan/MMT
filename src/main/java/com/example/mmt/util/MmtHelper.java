package com.example.mmt.util;

import com.example.mmt.entity.Flight;
import com.example.mmt.model.PostRequest;
import com.example.mmt.service.MinTimeJourneyEngine;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MmtHelper {

    private static final String UTF8_BOM = "\uFEFF";
    private static final int FLIGHT_GAP = 120;

    public static void updateListOfSchedule(String src, String dest, List<String> codeList, Map<String, String> countryMapping, Map<Pair<String, String>, TreeSet<Flight>> map) {

        for(String hop : codeList) {
            if(hop.equalsIgnoreCase(src) ||  hop.equalsIgnoreCase(dest))
                continue;

            if(countryMapping.get(src).equalsIgnoreCase(countryMapping.get(dest))
                    && !countryMapping.get(src).equalsIgnoreCase(countryMapping.get(hop)))
                continue;

            TreeSet<Flight> firstLeg = map.get(new Pair(src, hop));
            TreeSet<Flight> secondLeg = map.get(new Pair(hop, dest));

            if(firstLeg == null || secondLeg == null)
                continue;

            long minDuration = Long.MAX_VALUE;

            Flight hopFlight = new Flight();
            boolean foundHop = false;

            for(Flight firstFlight : firstLeg) {
                if(!firstFlight.isDirect()) continue;
                for(Flight secondFlight : secondLeg) {
                    if(!secondFlight.isDirect()) continue;
                    long gap = timeDuration(firstFlight.getEndTime(), secondFlight.getStartTime());
                    if(gap >= FLIGHT_GAP) {

                        long duration = firstFlight.getDurationInMinute() + secondFlight.getDurationInMinute() + gap;

                        if(minDuration > duration) {
                            minDuration = duration;
                            foundHop = true;
                            hopFlight.setFlightNumber(firstFlight.getFlightNumber() + "_" + secondFlight.getFlightNumber());
                            hopFlight.setSource(src + "_" + hop);
                            hopFlight.setDestination(dest);
                            hopFlight.setStartTime(firstFlight.getStartTime());
                            hopFlight.setEndTime(secondFlight.getEndTime());
                            hopFlight.setDirect(false);
                            hopFlight.setDurationInMinute(duration);
                        }
                    }
                }
            }

            if(!foundHop)
                continue;

            TreeSet<Flight> schedules = null;

            if(map.containsKey(new Pair(src, dest))) {
                schedules = map.get(new Pair(src, dest));
                schedules.add(hopFlight);

            } else {
                schedules = new TreeSet<Flight>();
                schedules.add(hopFlight);
                map.put(new Pair(src, dest), schedules);
            }
        }

    }

    public static long timeDuration(String startTime, String endTime) {

        startTime = make4Length(startTime);
        endTime   = make4Length(endTime);

        String time1 =  startTime.substring(0, 2)   + ":" + startTime.substring(2)   + ":00";
        String time2 =  endTime.substring(0, 2)   + ":" + endTime.substring(2)   + ":00";

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        long timeDiff = date2.getTime() - date1.getTime();

        if(timeDiff > 0)
            return (timeDiff)/(1000*60);
        else
            return (86400000L + timeDiff)/(1000*60);
    }

    public static String make4Length(String startTime) {
        if(startTime.length() == 3) {
            startTime = "0" + startTime;
        } else if(startTime.length() == 2) {
            startTime = "00" + startTime;
        } else if(startTime.length() == 1) {
            startTime = "000" + startTime;
        }
        return startTime;
    }

    public static Map<String, String> getCountryMapping(String filePath) {
        Object obj = null;
        Map<String, String> result = new HashMap<>();

        try {
            obj = new JSONParser().parse(new FileReader(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = (JSONObject) obj;

        for(Object key : jsonObject.keySet()) {
            result.put((String) key, (String) jsonObject.get(key));
        }
        return result;
    }

    public static List getAllCodes(List<Flight> flightList) {
        Set<String> codeList = new HashSet<>();

        for(Flight schedule : flightList){
            codeList.add(schedule.getSource());
            codeList.add(schedule.getDestination());
        }

        return new ArrayList(codeList);
    }

    public static List read(String csvFile){
        List<Flight> flightList = new ArrayList<>();
        try {
            File file = new File(csvFile);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null) {
                flightList.add(fromCSV(line));
            }
            br.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        return flightList;
    }

    public static Map buildAdjacencyList(List<Flight> flightList) {
        Map<Pair<String, String>, TreeSet<Flight>> map = new HashMap<>();
        for(Flight schedule : flightList) {
            Pair pair = new Pair(schedule.getSource(), schedule.getDestination());
            TreeSet<Flight> scheduleList = map.get(pair);
            if(scheduleList != null) {
                scheduleList.add(schedule);
                map.put(pair, scheduleList);
            } else {
                scheduleList = new TreeSet<>();
                scheduleList.add(schedule);
                map.put(pair, scheduleList);
            }
        }
        return map;
    }

    public static Flight fromCSV(String csvLine) {
        String[] values = csvLine.split(",");
        Flight schedule = new Flight();
        schedule.setFlightNumber(removeUTF8BOM(values[0].trim()));
        schedule.setSource(values[1]);
        schedule.setDestination(values[2]);
        schedule.setStartTime(values[3]);
        schedule.setEndTime(values[4]);
        schedule.setDirect(true);
        schedule.setDurationInMinute(timeDuration(values[3], values[4]));

        return schedule;
    }

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static List buildOutputList(PostRequest inputPayload) {
        Map<Pair<String, String>, TreeSet<Flight>> map = MinTimeJourneyEngine.getMap();
        TreeSet<Flight> list = map.get(new Pair<String, String>(inputPayload.getSrc(), inputPayload.getDest()));

        List<Map<String, Map<String, Long>>> result = new ArrayList<>();

        if(list != null) {
            for(Flight schedule : list) {
                Map<String, Map<String, Long>> node = new HashMap<>();
                String key = schedule.getSource() + "_" + schedule.getDestination();
                Map<String, Long> value = new HashMap<String, Long>() {{ put(schedule.getFlightNumber(), schedule.getDurationInMinute());}};
                node.put(key, value);
                result.add(node);
            }
        }

        return result;
    }
}
