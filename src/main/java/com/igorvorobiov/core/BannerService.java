package com.igorvorobiov.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */

@Service
public class BannerService {

    private final Hashtable<String, ArrayList<Click>> clicksStorage = new Hashtable<>();

    private MongoTemplate mongo;

    @Autowired
    public BannerService(MongoTemplate mongo){
        this.mongo = mongo;
    }

    public void registerClick(String bannerId, Click click){
        click.setBannerId(bannerId);

        if (!clicksStorage.containsKey(bannerId)){
            clicksStorage.put(bannerId, new ArrayList<>());
        }

        clicksStorage.get(bannerId).add(click);
    }

    @Scheduled(fixedDelay = 5000)
    public void flushClicks(){

        if (clicksStorage.isEmpty()){
            return ;
        }

        synchronized (clicksStorage){
            ArrayList<Click> clicks = new ArrayList<>();

            for (ArrayList<Click> c : clicksStorage.values() ){
                clicks.addAll(c);
            }

            mongo.insertAll(clicks);

            clicksStorage.clear();
        }
    }

    public Statistics getStatistics(String bannerId){

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("bannerId").is(bannerId)),
                Aggregation.group().sum("cost").as("cost"));

        List<Statistics> data = mongo.aggregate(aggregation, Click.class, Statistics.class).getMappedResults();

        Statistics statistics;

        if (data.size() == 1){
            statistics = data.get(0);
        } else {
            statistics = new Statistics();
        }

        int sum = statistics.getCost();

        if (clicksStorage.containsKey(bannerId)){
            for (Click click : clicksStorage.get(bannerId)){
                sum += click.getCost();
            }
        }

        statistics.setCost(sum);

        return statistics;
    }
}
