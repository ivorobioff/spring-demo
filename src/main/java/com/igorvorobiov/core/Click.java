package com.igorvorobiov.core;

import org.springframework.data.annotation.Id;

/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */
public class Click {

    @Id
    private String id;
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    private String bannerId;
    public void setBannerId(String id) { bannerId = id; }
    public String getBannerId(){ return bannerId; }

    private int cost;
    public void setCost(int price){ cost = price; }
    public int getCost(){ return cost; }
}
