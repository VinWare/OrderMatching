package org.cod.ordermatching.controllers;

import java.util.*;

public class RamdomGeneration
{
    public double getBid(double currentPrice)
    {
        //return round(random.normalvariate(current_price,0.15)*20)/20
        return 0.1;
    }

    public double getAsk(double currentPrice)
    {
        //return round(random.normalvariate(current_price,0.15)*20)/20
        return 0.1;
    }


    public void randomGeneration()
    {
        String stock = "MSFT";
        List<Double> prices = new ArrayList<Double>();
        double initialPrice = 100;
        double currentPrice = initialPrice;
        prices.add(currentPrice);
        SortedMap<Double, List<Integer>> bids = new TreeMap<Double, List<Integer>>(Collections.reverseOrder());
        SortedMap<Double, List<Integer>> asks = new TreeMap<Double, List<Integer>>();
        /*
        HashMap<Integer,> rejected_orders = new HashMap<Integer,>();

        successful_orders={}
        successful_trades={}*/
        int orderId = 0;

        List<String> typeList = new ArrayList<String>();
        typeList.add("ask");
        typeList.add("bid");

        List<String> orderTypeList = new ArrayList<String>();
        orderTypeList.add("limit");
        orderTypeList.add("market");

        double openingPrice = currentPrice;
        System.out.println("MSFT Opening Price: " + openingPrice);
        System.out.println("Bids: " + bids);
        System.out.println("Asks: " + asks);
        System.out.println();
        System.out.println("---------------------------------");

        for (int i = 0; i < 100; i++) {
            orderId++;
            Random rand = new Random();
            String choice = typeList.get(rand.nextInt(typeList.size()));
            String choiceType = orderTypeList.get(rand.nextInt(orderTypeList.size()));
            int quantity = rand.nextInt(1000)-10;
            if (choice == "ask") {
                double ask;
                if (choiceType == "limit") {
                    ask = getAsk(currentPrice);
                    if (ask < 0.9 * openingPrice || ask > 1.1 * openingPrice) {
                        //add order in rejected table
                        continue;
                    }
                } else {
                    if (bids.size() != 0)
                        ask = bids.firstKey();
                    else {
                        //add order in rejected table
                        continue;
                    }
                }
                //add order in successful orders table
                System.out.println("NEW ASK {" + choiceType + "}: " + ask + " " + quantity);
                if (asks.containsKey(ask))
                    asks.get(ask).add(quantity);
                else {
                    List<Integer> dummy = new ArrayList<Integer>();
                    dummy.add(quantity);
                    asks.put(ask, dummy);
                }
                Check check = new Check();
                check.checkMatch(ask, bids, asks, currentPrice);
                bids = check.getBids();
                asks = check.getAsks();
                currentPrice = check.getCurrentPrice();
                //bids,asks,current_price=check_match(ask,bids,asks,current_price)

            }
            if (choice == "bid") {
                Double bid;
                if (choiceType == "limit") {
                    bid = getBid(currentPrice);
                    if (bid < 0.9 * openingPrice || bid > 1.1 * openingPrice) {
                        //add order to rejected table
                        continue;
                    }
                } else {
                    if (asks.size() != 0)
                        bid = asks.firstKey();
                    else {
                        //add order to rejected table
                        continue;
                    }
                }
                //add order to successful table
                System.out.println("NEW BID {" + choiceType + "}: " + bid + " " + quantity);
                if (bids.containsKey(bid))
                    bids.get(bid).add(quantity);
                else {
                    List<Integer> dummy = new ArrayList<Integer>();
                    dummy.add(quantity);
                    bids.put(bid, dummy);
                }
                Check check = new Check();
                //asks,bids,current_price=check_match(bid,asks,bids,current_price)
            }
            prices.add(currentPrice);
            System.out.println();
            System.out.println("MSFT Current Price: " + currentPrice);
            System.out.println("Bids: " + bids);
            System.out.println("Asks: " + asks);
            System.out.println();
            System.out.println("------------------------------------------------");
        }
    }
}
