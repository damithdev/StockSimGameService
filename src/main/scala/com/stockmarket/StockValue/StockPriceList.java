package com.stockmarket.StockValue;


import com.stockmarket.StockValue.Trends.EventModel;
import com.stockmarket.StockValue.Trends.MarketTrendModel;
import com.stockmarket.StockValue.Trends.RandomTrendModel;
import com.stockmarket.StockValue.Trends.SectorTrendModel;

import java.util.*;

public class StockPriceList {
    public static int StockValueArray[];

    public StockPriceList(){

    }

    public static List<Map<String,Map<String,Integer>>> getStockValueList(Map<String,List<String>> stockMap,int turns){



        List<Map<String,Map<String,Integer>>> list = new ArrayList<>();

        MarketTrendModel market = new MarketTrendModel();
        SectorTrendModel sector = new SectorTrendModel();
        RandomTrendModel random = new RandomTrendModel();
        EventModel event = new EventModel(20);


        for (int i =0 ; i < turns;i++){
            Map<String,Map<String,Integer>> outerMap = new HashMap<>();
            //get market trend
            outerMap.get(Integer.toString(i)).put(event.eventTypeOccurred[i],market.MarketTrend[i]);

            int val = i;
            stockMap.forEach((k,v) ->{

                //get sector trend
                //stockMap.put(Integer.toString(i),sector.SectorTrend[i]);
                k = Integer.toString(val);
                v = List.of(Integer.toString(sector.SectorTrend[val]));

                Map<String,Integer> innerMap = new HashMap<>();
                v.forEach(stockname->{
                    //random trend
                    innerMap.put(stockname,random.RandomTrendArray[val]);
                });
                outerMap.put(k,innerMap);
            });
            list.add(outerMap);

        }


        return list;

        //List<Map<Sector,Map<Stock,Value>>> sample_list
        // -> means a map
        // - means a list
        //
        //        Sample Input
        //        Technology ->
        //              -  google
        //              -  yahoo
        //        Manufacturing ->
        //              -  company
        //              -  company

        //        Sample Retun
        //        Technology ->
        //              -  google -> 3
        //              -  yahoo -> 2
        //        Manufacturing ->
        //              -  company1 -> 0
        //              -  company2 -> 1







//        int StockPrice = 0;

//        RandomTrendModel random = new RandomTrendModel();
//        MarketTrendModel market = new MarketTrendModel();
//        SectorTrendModel sector = new SectorTrendModel();
//        EventModel event = new EventModel();
//
//        int StockValue = 0;
//        int precentage = 0;
//
//        for(int i =0; i<20; i++){
//            StockValue = random.RandomTrendArray[i] + market.MarketTrend[i] + sector.SectorTrend[i] + event.eventModel[i];
//
//            if(StockValue<0)
//                precentage = 0;
//            else
//                precentage = StockValue;
//
//            StockValueArray[i] = precentage;
//        }
//        return map;
    }


    public static List<Map<String,Map<String,Integer>>> getMockStockValueList(Map<String,List<String>> stockMap,int turns){

            List<Map<String,Map<String,Integer>>> list = new ArrayList<>();

        var x  = RandomTrendModel.getMockStockValueList(stockMap,20);
        var z  = MarketTrendModel.updateMarketTrendArray(20);
        var y  = SectorTrendModel.getMockStockValueList(stockMap,20);
        EventModel eventModel = new EventModel(20);

        for (int i =0 ; i < turns;i++){

        Map<String,Map<String,Integer>> outerMap = new HashMap<>();
        int val = i;
        stockMap.forEach((k,v) ->{
            Map<String,Integer> innerMap = new HashMap<>();
            v.forEach(stockname->{

                int randTrendValue = 0;
                int marketTrendValue = 0;
                int sectorTrendValue = 0;
                int eventValue = 0;

                try {
//                    Object a = x.get(val);
//                    Object b = x.get(val).get(k);
                    Object c = x.get(val).get(k).get(stockname);

                    randTrendValue =  Integer.parseInt(c.toString()) < 0 ? 0: Integer.parseInt(c.toString());

                }catch (Exception e){

                }

                try {
                    marketTrendValue =  z.get(val);
                }catch (Exception e){

                }

                try {

                    sectorTrendValue =  y.get(val).get(k);
//                    sectorTrendValue =  y.get(val).get(k).get(stockname);
                }catch (Exception e){

                }

                try {
                    eventValue = eventModel.eventModel[val];
                }catch (Exception e){

                }

                int tot =  randTrendValue+ marketTrendValue + sectorTrendValue + eventValue ;
                if(val == 0){
                    tot = tot< 0? 1: tot;
                }else{
                    tot = tot< 0? 0: tot;
                }

                innerMap.put(stockname,tot);
            });
            outerMap.put(k,innerMap);
        });
        list.add(outerMap);
    }

        return list;
    }

    private static int getMockRand(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        if(x < 0){
            return 0;
        }else{
            return x;
        }
    }

//    List<Map<String,Map<String,Integer>>> list = new ArrayList<>();
//
//
//
//        for (int i =0 ; i < turns;i++){
//        Map<String,Map<String,Integer>> outerMap = new HashMap<>();
//        stockMap.forEach((k,v) ->{
//
//            Map<String,Integer> innerMap = new HashMap<>();
//            v.forEach(stockname->{
//                innerMap.put(stockname,getMockRand());
//            });
//            outerMap.put(k,innerMap);
//        });
//        list.add(outerMap);
//    }
//
//
//        return list;

}
