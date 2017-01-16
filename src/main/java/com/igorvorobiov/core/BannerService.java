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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */

@Service
public class BannerService {

    private final ConcurrentHashMap<String, AtomicLong> statisticsStorage = new ConcurrentHashMap<>();

    private MongoTemplate mongo;

    @Autowired
    public BannerService(MongoTemplate mongo){
        this.mongo = mongo;
    }

    public void registerClick(String bannerId, Click click){

        statisticsStorage.putIfAbsent(bannerId, new AtomicLong(0));

        statisticsStorage.get(bannerId).getAndAdd(click.getCost());
    }

    @Scheduled(fixedDelay = 5000)
    public void flushStatistics(){

        if (statisticsStorage.isEmpty()){
            return ;
        }

        HashMap<String, Long> localStatistics = new HashMap<>();

        for (String bannerId : new HashSet<>(statisticsStorage.keySet())){
            AtomicLong v = statisticsStorage.remove(bannerId);

            if (v != null){
                localStatistics.put(bannerId, v.get());
            }
        }

        BulkOperations ops = mongo.bulkOps(BulkMode.UNORDERED, Statistics.class);

        for (String bannerId : localStatistics.keySet()){

            Long cost = localStatistics.get(bannerId);

            ops.upsert(
                    new Query(Criteria.where("bannerId").is(bannerId)),
                    new Update().inc("cost", cost)
            );
        }

        ops.execute();
    }

    public Statistics getStatistics(String bannerId){

        Statistics statistics = mongo.findOne(new Query(Criteria.where("bannerId").is(bannerId)), Statistics.class);

        if (statistics == null){
            statistics = new Statistics();
            statistics.setBannerId(bannerId);
        }

        statistics.setCost((int) (statistics.getCost()
                + statisticsStorage.getOrDefault(bannerId, new AtomicLong(0)).get()));

        return statistics;
    }
}
