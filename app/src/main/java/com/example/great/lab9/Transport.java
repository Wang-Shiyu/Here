package com.example.great.lab9;

/**
 * Created by wonggwan on 2018/1/6.
 */

import java.io.Serializable;
import java.util.List;

public class Transport implements Serializable {
    public List<Double> param;

    public Transport( List<Double> param) {
        this.param = param;
    }
}

