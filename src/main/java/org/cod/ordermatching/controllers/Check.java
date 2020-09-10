package org.cod.ordermatching.controllers;

import java.util.List;
import java.util.SortedMap;

public class Check
{
    private SortedMap<Double, List<Integer>> asks, bids;
    private double currentPrice;
/*
    def clean_orders(orders):
    new_orders=orders.copy()
        for k in orders.keys():
            if orders[k]==[]:
            del new_orders[k]
        return new_orders*/

    public SortedMap<Double, List<Integer>> cleanOrders(SortedMap<Double, List<Integer>> orders)
    {
        for(int i=0;i<orders.size();i++)
        {
            if(orders.get(i).size() == 0)
                orders.remove(i);
        }
        return orders;
    }

    public Check checkMatch(double ask, SortedMap<Double, List<Integer>> bids, SortedMap<Double, List<Integer>> asks, double current_price)
    {
        int bid_quantity, ask_quantity;
        if(bids.containsKey(ask) && bids.get(ask).size()!=0)
        {
            while(true)
            {
                bid_quantity = bids.get(ask).get(0);
                ask_quantity = asks.get(ask).get(0);
                if(ask_quantity < bid_quantity)
                {
                    bid_quantity -= ask_quantity;
                    asks.remove(0);
                    System.out.println("Order matched: " + ask + " " + ask_quantity);
                    break;
                }
                if(ask_quantity>bid_quantity)
                {
                    ask_quantity-=bid_quantity;
                    bids.remove(0);
                    System.out.println("Order matched: " + ask + " " + bid_quantity);
                }
                if(ask_quantity == bid_quantity)
                {
                    asks.remove(0);
                    bids.remove(0);
                    System.out.println("Order matched: " + ask + " " + bid_quantity);
                    break;
                }
                if(bids.get(ask).size()==0)
                    break;
            }
            current_price=ask;
        }
        else
            System.out.println("No match");
        //return null;
        this.bids=cleanOrders(bids);
        this.asks=cleanOrders(asks);
        this.currentPrice=currentPrice;
        return this;
    }

    public SortedMap<Double, List<Integer>> getAsks() {
        return asks;
    }

    public SortedMap<Double, List<Integer>> getBids() {
        return bids;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }
}



