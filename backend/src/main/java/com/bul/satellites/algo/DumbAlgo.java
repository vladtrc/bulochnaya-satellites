package com.bul.satellites.algo;

import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;

public class DumbAlgo implements Algorithm {
    @Override
    public Result apply(Given given) {
        return null;
    }

    @Override
    public String name() {
        return "dumb";
    }
}
