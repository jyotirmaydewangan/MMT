package com.example.mmt.controller;

import com.example.mmt.model.PostRequest;
import com.example.mmt.util.MmtHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;


@RestController
public class WebController {

    @RequestMapping(value = "/getPossibleFlights", method = RequestMethod.POST)
    public List Test(@RequestBody PostRequest inputPayload) {
        return MmtHelper.buildOutputList(inputPayload);
    }
}