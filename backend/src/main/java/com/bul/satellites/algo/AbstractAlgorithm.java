package com.bul.satellites.algo;

import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;

interface Algorithm {
    Result apply(Given given);

    String name();
}
