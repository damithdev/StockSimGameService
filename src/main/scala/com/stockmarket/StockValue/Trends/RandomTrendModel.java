package com.stockmarket.StockValue.Trends;

import com.stockmarket.StockValue.RandomNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomTrendModel {
    public static int RandomTrendArray[];
    public static int RandomTrend ;
    public static int i =0;

//    public RandomTrendModel(){
//        RandomTrendArray = new int[turns];
//
//        if(turns<=0)
//            throw new RuntimeException("Invalid number of turns");
//        else
//            updateRandomTrendArray();
//    }

    public static List<Map<String,Map<String,Integer>>> getMockStockValueList(Map<String,List<String>> stockMap,int turns){

        List<Map<String,Map<String,Integer>>> list = new ArrayList<>();

        for (;i < turns;i++){
            Map<String,Map<String,Integer>> outerMap = new HashMap<>();
            stockMap.forEach((k,v) ->{

                Map<String,Integer> innerMap = new HashMap<>();
                v.forEach(stockname->{
                    innerMap.put(stockname,updateRandomTrendArray());
                });
                outerMap.put(k,innerMap);
            });
            list.add(outerMap);
        }

        return list;
    }


    public static int updateRandomTrendArray(){

            RandomNumber obj = new RandomNumber();
            int y = obj.getNumber(1, 2, -2,1,1,2);
            RandomTrend = y;
            return RandomTrend;
    }
}
