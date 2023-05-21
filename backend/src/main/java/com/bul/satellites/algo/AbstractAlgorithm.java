package com.bul.satellites.algo;

import com.bul.satellites.model.DurationDataset;

interface Algorithm {
    DurationDataset apply(AlgorithmGiven given);
}
