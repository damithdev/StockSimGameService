package com.stockmarket.StockValue.Trends;

import java.util.Arrays;

public class EventModel {


    public boolean eventOccurred[];
    public static String eventTypeOccurred[];
    public int eventModel[];
    public int eventOccurrenceProbability = 0;
    public int eventDuration;
    public int eventRange;
    public int turns = 20;

    public EventModel(int t){
        turns = t;
        eventOccurred = new boolean[turns];
        eventTypeOccurred = new String[turns];

        eventModel = new int[turns];
        Arrays.fill(eventModel,0);
        UpdateEventArrays();
    }


    public String[] UpdateEventArrays(){
        String eventType;
        for(int i =0; i<turns; i++){
            eventOccurred[i] =  getEventOccurrenceProbability(i);
            eventOccurrenceProbability = eventOccurrenceProbability + 1;

            if(eventOccurred[i] == true){

                eventType = getEventType();

                if(eventType=="SECTOR"){
                    eventDuration = getRandomIntegerBetweenRange(2,5);
                    eventType = getSectorEventType();
                }else if(eventType=="STOCK"){
                    eventDuration = getRandomIntegerBetweenRange(1,7);
                    eventType = getStockEventType();
                }

                for(int j = i; ((j<(i+eventDuration)) && (j<turns)); j++){
                    eventOccurred[j] = true;
                    eventModel[j] = eventRange;
                    eventTypeOccurred[j] = eventType;
                }
                i = i + eventDuration - 1;
            }
        }
        return eventTypeOccurred;
    }



    public boolean getEventOccurrenceProbability(int turn){
        boolean event = false;
        if(turn == 0){
            eventOccurrenceProbability = 0;
        }else if(eventOccurred[turn-1] == true){
            eventOccurrenceProbability = 0;
        }

        int x = getRandomIntegerBetweenRange(1,10);


        if(x <= eventOccurrenceProbability){
            event = true;
        }else{
            event = false;
        }
        return event;
    }


    public String getEventType(){
        int SectorEvent = 33;
        int StockEvent = 67;

        String eventType;

        int x = getRandomIntegerBetweenRange(1,100);

        if(x <= SectorEvent){
            eventType = "SECTOR";
        }else{
            eventType = "STOCK";
        }
        return eventType;
    }


    public String getSectorEventType(){
        int BOOM = 1;
        int BUST = 1;

        String eventType;

        int x = getRandomIntegerBetweenRange(1,BOOM+BUST);

        if(x <= BOOM){
            eventType = "BOOM";
            eventRange = getRandomIntegerBetweenRange(1,5);
        }else{
            eventType = "BUST";
            eventRange = getRandomIntegerBetweenRange(-5,-1);
        }
        return eventType;
    }


    public String getStockEventType(){
        int PROFIT_WARNING = 2;
        int TAKE_OVER = 1;
        int SCANDAL = 1;

        String eventType;

        int x = getRandomIntegerBetweenRange(1,PROFIT_WARNING + TAKE_OVER + SCANDAL);

        if(x <= SCANDAL){
            eventType = "SCANDAL";
            eventRange = getRandomIntegerBetweenRange(-6,-3);
        }else if (x <= (SCANDAL + TAKE_OVER)){
            eventType = "TAKE_OVER";
            eventRange = getRandomIntegerBetweenRange(-5,-1);
        }else{
            eventType = "PROFIT_WARNING";
            eventRange = getRandomIntegerBetweenRange(2,3);
        }
        return eventType;
    }

    public int getRandomIntegerBetweenRange(int min, int max){
        int x = (int) ((int)(Math.random()*((max-min)+1))+min);
        return x;
    }
}
