package com.stockmarket.StockValue.Trends;

import com.stockmarket.StockValue.RandomNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketTrendModel {
    public static int[] MarketTrend;
    public static int turns = 20;
    public static int y;

//    public MarketTrendModel(){
//        MarketTrend = new int[turns];
//
//        if(turns<=0)
//            throw new RuntimeException("Invalid number of turns");
//        else
//            updateMarketTrendArray();
//    }

    MarketTrendModel market = new MarketTrendModel();
    SectorTrendModel sector = new SectorTrendModel();
    RandomTrendModel random = new RandomTrendModel();
    EventModel event = new EventModel(20);

//    public static List<Integer> getMockStockValueList(int turns){
//        SectorTrendModel sd = new SectorTrendModel();
//        // getEventModel();
//        List<Map<String,Map<String,Integer>>> list = new ArrayList<>();
//        String name[]=sd.event.UpdateEventArrays();
//        for (int i=0;i < turns;i++){
//            //int val =i;
//            Map<String,Integer>outerMap = new HashMap<>();
//
//            outerMap.put(name[i],updateMarketTrendArray());
//
//            stockMap.forEach((k,v) ->{
//               // k = Integer.toString(val);
//                //v = List.of(Integer.toString(updateSectorTrendArray()));
//            });
//            //list.add(outerMap);
//        }
//
//        return list;
//    }

    public static List<Integer>  updateMarketTrendArray(int t){
//        MarketTrend[0] = 0;
        List<Integer> l = new ArrayList<>();

        turns = t;
        for(int i =0; i<(turns-1); i++){
            RandomNumber obj = new RandomNumber();
            y = obj.getNumber(1, 3, -3, 1,1,2);
            l.add(y);
        }
        return l;
    }
}
