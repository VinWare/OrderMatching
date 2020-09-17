package org.cod.tradeAndRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class TradeService {


    @Autowired
    private TradeRepository tradeRepository;


    //TRADE SYSTEM AND ORDER-HELPERS
    private NewMatchingSystem m = new NewMatchingSystem();
    private SortedMap<Double, List<NewMatchingSystem.mapper>> buy_Orders = new TreeMap<Double, List<NewMatchingSystem.mapper>>(Collections.reverseOrder());
    private SortedMap<Double, List<NewMatchingSystem.mapper>> sell_Orders = new TreeMap<Double, List<NewMatchingSystem.mapper>>();
    private final SortedMap<Integer,TradeOrder> allOrders = new TreeMap<>();
    private SortedMap<Integer,TradeOrder> successfullOrders = new TreeMap<>();
    private SortedMap<Integer,TradeOrder> unsuccessfullOrders = new TreeMap<>();
    private double last = 100d;
    private double SD = 0.5d;
    private static int TRADE_ID=10000;
    public Quantity quantity = new Quantity();
    public static class Quantity{
        int buyQuantity = 0;
        int sellQuantity = 0;
    }

    public double getLast() {
        return last;
    }

    public List<TradeOrder> getAllLimitByTradeType(String tradeType) {

        return tradeRepository.
                findByTradeType(tradeType).
                stream().
                filter(tradeOrder -> tradeOrder.getOrderType().equals("LIMIT")).
                collect(Collectors.toUnmodifiableList());
    }


    public List<TradeOrder> getAllBuyOrders() {
        List<TradeOrder> l = new ArrayList<>();
        tradeRepository.findByTradeType("BUY").forEach(l::add);

        return l;
    }

    public List<TradeOrder> getAllSellOrders() {
        List<TradeOrder> l = new ArrayList<>();
        tradeRepository.findByTradeType("SELL").forEach(l::add);
        return l;
    }

    public List<TradeOrder> getAllOrders() {
        List<TradeOrder> l = new ArrayList<>();
        tradeRepository.findAll().forEach(l::add);
        return l;
    }

    public void addTrade(TradeOrder t) {
        tradeRepository.save(t);
        if (tradeVaildator(t)) {
            quantityUpdater(t);
            last = matcherTrade(t);
        }
        else
            cancelTrade(t);
        for (int tr: allOrders.keySet()) {
            tradeRepository.save(allOrders.get(tr));
            System.out.println(allOrders.get(tr).getOrderId());
        }
        //l.add(tradeOrder);
    }

    public List<TradeOrder> getAllSuccess(String s) {
        List<TradeOrder> l = new ArrayList<>();
        tradeRepository.findByStatus(s).forEach(l::add);
        return l;
    }

    public void start(){
        for (int i = 0; i < 10; i++) {
            TradeOrder t = TradeGenerator(m, i);
            if (tradeVaildator(t)) {
                quantityUpdater(t);
                last = matcherTrade(t);
            }
            else
                cancelTrade(t);

            addTrade(t);

        }
    }

    public double matcherTrade(TradeOrder t)
    {
        return m.matchTrade(buy_Orders,sell_Orders,allOrders,t,t.getPrice(),last,TRADE_ID++,successfullOrders,quantity);
    }

    private boolean tradeVaildator(TradeOrder t) {
        return m.validateTradeForChecks(quantity,t,last);
    }

    private void quantityUpdater(TradeOrder t) {
        m.quantityUpdate(quantity,t);
    }

    private TradeOrder TradeGenerator(NewMatchingSystem m,int i) {
        return m.generate(i,last,SD);
    }

    public void cancelTrade(TradeOrder t)
    {
        m.cancelOrderUpdateInMap(t,unsuccessfullOrders,allOrders);
    }

}
