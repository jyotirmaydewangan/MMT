package com.example.mmt.service;

import com.example.mmt.entity.Flight;
import com.example.mmt.util.MmtHelper;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Component
public class MinTimeJourneyEngine {
    private static final String csvFilePath = "src/main/resources/static/ivtest-sched.csv";
    private static final String jsonFilePath = "src/main/resources/static/airports-ivtest-countries.json";

    private List<Flight> flightList = null;
    private Map<String, String> countryMapping = null;
    private List<String> codeList = null;
    private static Map<Pair<String, String>, TreeSet<Flight>> map ;

    @PostConstruct
    public void init(){
        flightList = MmtHelper.read(csvFilePath);
        countryMapping = MmtHelper.getCountryMapping(jsonFilePath);
        codeList = MmtHelper.getAllCodes(flightList);
        map = MmtHelper.buildAdjacencyList(flightList);

        for(String src : codeList) {
            for(String dest : codeList) {
                MmtHelper.updateListOfSchedule(src, dest, codeList, countryMapping, map);
            }
        }
    }

    public static Map<Pair<String, String>, TreeSet<Flight>> getMap() {
        return map;
    }
}
