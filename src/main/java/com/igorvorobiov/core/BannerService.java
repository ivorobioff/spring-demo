package com.igorvorobiov.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Hashtable;

/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */

@Service
public class BannerService {

    private final Hashtable<String, Integer> statisticsStorage = new Hashtable<>();

    private MongoTemplate mongo;

    @Autowired
    public BannerService(MongoTemplate mongo){
        this.mongo = mongo;
    }

    public void registerClick(String bannerId, Click click){

        synchronized (statisticsStorage){
            statisticsStorage.put(bannerId, statisticsStorage.getOrDefault(bannerId, 0) + click.getCost());
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void flushStatistics(){

        if (statisticsStorage.isEmpty()){
            return ;
        }

        synchronized (statisticsStorage){

            BulkOperations ops = mongo.bulkOps(BulkMode.UNORDERED, Statistics.class);

            for (String bannerId : statisticsStorage.keySet()){

                Integer cost = statisticsStorage.get(bannerId);

                ops.upsert(
                        new Query(Criteria.where("bannerId").is(bannerId)),
                        new Update().inc("cost", cost)
                );
            }

            ops.execute();

            statisticsStorage.clear();
        }
    }

    public Statistics getStatistics(String bannerId){

        Statistics statistics = mongo.findOne(new Query(Criteria.where("bannerId").is(bannerId)), Statistics.class);

        if (statistics == null){
            statistics = new Statistics();
            statistics.setBannerId(bannerId);
        }

        statistics.setCost(statistics.getCost() + statisticsStorage.getOrDefault(bannerId, 0));

        return statistics;
    }
}
