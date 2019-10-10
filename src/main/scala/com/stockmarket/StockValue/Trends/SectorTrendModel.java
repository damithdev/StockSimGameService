package com.stockmarket.StockValue.Trends;

import com.stockmarket.StockValue.RandomNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectorTrendModel {
    public  static int SectorTrend[];
    public static int turns = 20;
    public  static String eventType[];

//    public SectorTrendModel(){
//        SectorTrend = new int[turns];
//
//        if(turns<=0)
//            throw new RuntimeException("Invalid number of turns");
//        else
//            updateSectorTrendArray();
//    }

    EventModel event = new EventModel(20);
    int eventSize= event.eventTypeOccurred.length;

    public static String[] getEventModel(){
        SectorTrendModel STM = new SectorTrendModel();
        for(int i=0; i<=STM.eventSize;i++)
        {
            eventType[i] = STM.event.eventTypeOccurred[i];
        }
        return STM.eventType;
    }

    public static List<Map<String,Integer>> getMockStockValueList(Map<String,List<String>> stockMap, int turns){
        SectorTrendModel sd = new SectorTrendModel();
       // getEventModel();
        List<Map<String,Integer>> list = new ArrayList<>();

        for (int i=0;i < turns;i++){
            int val =i;
            Map<String,Integer> outerMap = new HashMap<>();
             //outerMap.get(Integer.toString(i)).put(eventType[i],updateSectorTrendArray());

            stockMap.forEach((k,v) ->{
                outerMap.put(k,updateSectorTrendArray());
//                k = Integer.toString(val);
//                v = List.of(Integer.toString(updateSectorTrendArray()));
//                Map<String,Integer> innerMap = new HashMap<>();
//                v.forEach(stockname->{
//                   // innerMap.put(stockname,updateRandomTrendArray());
//                });
//                outerMap.put(k,innerMap);
            });
            list.add(outerMap);
        }

        return list;
    }


    public static int  updateSectorTrendArray(){
       //SectorTrend[0] = 0;
        int y=0;
        for(int i =0; i<(turns-1); i++){
            RandomNumber obj = new RandomNumber();
            y= obj.getNumber(1, 3, -3,1,1,2);
            //SectorTrend[i+1] = y;
        }
        return  y;
    }
}