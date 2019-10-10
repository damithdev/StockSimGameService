package com.stockmarket.StockValue;

public class RandomNumber {

    public int getNumber(int previousNo, int max, int min, int increaseProb, int decreaseProb, int NotChangeProb ){
        String next = getNextTurnByProbability(increaseProb,decreaseProb,NotChangeProb);
        int x = 0;
        Boolean done = false;

        while(!done){
            if(((previousNo == min) && (next == "DECREASE")) | ((previousNo == max) && (next == "NOCHANGE"))){
                next = getNextTurnByProbability(increaseProb,decreaseProb,NotChangeProb);
            }else {
                if(next.equals("INCREASE")){
                    x = previousNo;
                }else if(next.equals("DECREASE")){
                    x = previousNo + 1;
                }else if(next.equals("NOCHANGE")){
                    x = previousNo - 1;
                }
                done = true;
            }
        }
        return x;
    }

    public String getNextTurnByProbability(int increaseProb,int decreaseProb,int NotChangeProb){

        if(increaseProb < 0 | decreaseProb <0 | NotChangeProb<0)
            throw new RuntimeException("Invalid probabilities");

        int x = (int) ((int)(Math.random()*(((increaseProb+decreaseProb+NotChangeProb)-1)+1))+1);

        String next;

        if(x <= increaseProb){
            next = "INCREASE";
        }else if(x <= (increaseProb + decreaseProb)){
            next = "DECREASE";
        }else{
            next = "NOCHANGE";
        }
        return next;
    }

}
