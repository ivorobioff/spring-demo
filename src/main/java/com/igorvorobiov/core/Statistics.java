package com.igorvorobiov.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;

/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */

public class Statistics {
    @Id
    private String id;
    public void setId(String id) { this.id = id; }

    @JsonIgnore
    public String getId() { return id; }

    private String bannerId;

    public void setBannerId(String id) { bannerId = id; }

    @JsonIgnore
    public String getBannerId(){ return bannerId; }

    private int cost = 0;
    public void setCost(int price){ cost = price; }
    public int getCost(){ return cost; }
}
