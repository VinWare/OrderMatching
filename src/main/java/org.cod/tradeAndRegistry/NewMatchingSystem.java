package org.cod.tradeAndRegistry;


import java.time.LocalTime;
import java.util.*;

public class NewMatchingSystem {

    Registry registry = new Registry();
    private final Random r = new Random();

    public class mapper {
        private int q;
        private final int id;

        public int getId() {
            return id;
        }

        public int getQ() {
            return q;
        }

        public mapper(int q, int id) {
            this.q = q;
            this.id = id;
        }

        @Override
        public String toString() {
            return "{" +
                    "q =" + q +
                    ", id =" + id +
                    '}';
        }
    }

    public double randomVariate(double mean, double sd) {
        double v = ((r.nextGaussian() * sd + mean) * 20) / 20;
        int q = (int) (v * 10);
        //int q1 = (int)v;
        double d = (v * 10 - q) >= 0.5 ? 0.5 : 0.0;
        //System.out.println(v+" "+q+" "+d*0.1);
        return Math.round(v * 10) / 10.0;//+d/10.0;
    }

    public int nextOrder() {
        return (r.nextGaussian() * 10) / 10 > 0 ? 1 : 0;
    }

    public int nextQuantity() {
        int low = 1;
        int high = 1001;
        return r.nextInt(high - low) + low;
    }

    public boolean checkerForPrice(double tPrice, double startPrice) {
        return (!(tPrice < 0.9 * startPrice) && !(tPrice > 1.1 * startPrice));
    }

    public TradeOrder generate(int orderId, double lastPrice, double SD) {
        TradeOrder t;
        int sample = nextOrder();
        int q = nextOrder();
        String orderType = q == 1 ? "MARKET" : "LIMIT";
        if (sample == 1) {

            t = registry.createTrade("BUY", orderType);
            t.setQuantity(nextQuantity());
            t.setOrderTime(LocalTime.now().toString());
            t.setOrderId(orderId);
            if (!(q == 1))
                t.setPrice(randomVariate(lastPrice, SD));
            else
                t.setPrice(-1);
        } else {
            t = registry.createTrade("SELL", orderType);
            t.setQuantity(nextQuantity());
            t.setOrderTime(LocalTime.now().toString());
            t.setOrderId(orderId);
            if (!(q == 1))
                t.setPrice(randomVariate(lastPrice, SD));
            else
                t.setPrice(-1);
        }

        return t;
    }

    public boolean validateTradeForChecks(TradeService.Quantity quantity,
                                          TradeOrder tradeOrder,
                                          double startPrice)
    {

        int q = tradeOrder.getOrderType().equals("MARKET")?1:0;
        int q2 = tradeOrder.getTradeType().equals("BUY")?1:0;
        if(q==1)
        {
            if(q2==1)
                return !(tradeOrder.getQuantity()>quantity.sellQuantity);
            else
                return !(tradeOrder.getQuantity()>quantity.buyQuantity);
        }
        else
        {
            return checkerForPrice(tradeOrder.getPrice(),startPrice);
        }

    }

    public void quantityUpdate(TradeService.Quantity quantity,TradeOrder tradeOrder)
    {
        if(!tradeOrder.getOrderType().equals("MARKET"))
        if(tradeOrder.getTradeType().equals("BUY"))
            quantity.buyQuantity+= tradeOrder.getQuantity();
        else
            quantity.sellQuantity+=tradeOrder.getQuantity();
    }
    public void cancelOrderUpdateInMap(  TradeOrder t,
                                         SortedMap<Integer, TradeOrder> unsuccessful ,
                                         SortedMap<Integer,TradeOrder> allOrders )
    {
        t.setStatus("CANCELLED");
        unsuccessful.put(t.getOrderId(),t);
        allOrders.put(t.getOrderId(),t);
    }

    public void addInMap(   SortedMap<Double, List<mapper>> buy_Orders ,
                            SortedMap<Double, List<mapper>> sell_Orders ,
                            SortedMap<Integer,TradeOrder> allOrders,
                            boolean BUY,TradeOrder t)
    {
        if(BUY)
        {
            mapper m = new mapper(t.getQuantity(),t.getOrderId());
            if (buy_Orders.containsKey(t.getPrice()))
                buy_Orders.get(t.getPrice()).add(m);
            else {
                List<mapper> dummy = new ArrayList<>();
                dummy.add(m);
                buy_Orders.put(t.getPrice(), dummy);
            }
            allOrders.put(m.id,t);
        }
        else
        {
            mapper m = new mapper(t.getQuantity(),t.getOrderId());
            if (sell_Orders.containsKey(t.getPrice()))
                sell_Orders.get(t.getPrice()).add(m);
            else
            {
                List<mapper> dummy = new ArrayList<>();
                dummy.add(m);
                sell_Orders.put(t.getPrice(), dummy);
            }
            allOrders.put(m.id,t);
        }

    }

    public double matchTrade(SortedMap<Double, List<mapper>> buy_Orders ,
                             SortedMap<Double, List<mapper>> sell_Orders ,
                             SortedMap<Integer,TradeOrder> allOrders,
                             TradeOrder t,
                             double ask, double lastprice,
                             int TRADE_ID, SortedMap<Integer, TradeOrder> successfulOrders,
                             TradeService.Quantity quantity)
    {
        int q1 = t.getOrderType().equals("MARKET")?1:0;
        int q2 = t.getTradeType().equals("BUY")?1:0;
        double finPrice = lastprice;
        if(q2==1)
        {
            System.out.println(t.toString());
            System.out.println();
            System.out.println("New BuyOrder {" + t.getOrderType() + "}: " + (t.getPrice()==-1?"":t.getPrice())+ " " + t.getQuantity());
            System.out.println("*******************************************");
            System.out.println("Number of BuyStocks-Available "+ quantity.buyQuantity);
            System.out.println("Number of SellStocks-Available "+quantity.sellQuantity);
            System.out.println("*******************************************");
            if(q1==1)
            {
                allOrders.put(t.getOrderId(),t);
                finPrice = matchTradeMARKET(t,q2,quantity,buy_Orders,
                        sell_Orders,allOrders,successfulOrders,lastprice,TRADE_ID);
            }
            else
            {

                addInMap(buy_Orders,sell_Orders,allOrders,true,t);
                finPrice = matchTradeLIMIT(ask,sell_Orders,buy_Orders,allOrders,
                        successfulOrders,lastprice,TRADE_ID);

            }
        }
        else
        {
            System.out.println(t.toString());
            System.out.println();
            System.out.println("NEW SellOrder {" + t.getOrderType() + "}: " + (t.getPrice()==-1?"":t.getPrice())+ " " + t.getQuantity());
            System.out.println("*******************************************");
            System.out.println("Number of BuyStocks-Available "+quantity.buyQuantity);
            System.out.println("Number of SellStocks-Available "+quantity.sellQuantity);
            System.out.println("*******************************************");
            if(q1==1)
            {
                allOrders.put(t.getOrderId(),t);
                finPrice = matchTradeMARKET(t,q2,quantity,buy_Orders,sell_Orders,allOrders,
                        successfulOrders,lastprice,TRADE_ID);
            }
            else
            {

                addInMap(buy_Orders,sell_Orders,allOrders,false,t);
                finPrice = matchTradeLIMIT(ask,buy_Orders,sell_Orders,allOrders,
                        successfulOrders,lastprice,TRADE_ID);

            }
        }
        System.out.println("Bids: " + buy_Orders);
        System.out.println("Asks: " + sell_Orders);
        System.out.println();
        System.out.println("------------------------------------------------");
        return finPrice;
    }
    private double matchTradeMARKET(TradeOrder t,int q, TradeService.Quantity quantity,
                                    SortedMap<Double, List<mapper>> buy_Orders,
                                    SortedMap<Double, List<mapper>> sell_Orders,
                                    SortedMap<Integer,TradeOrder> allOrders,
                                    SortedMap<Integer,TradeOrder> successfullOrders,
                                    double lastPrice, int TRADE_ID)
    {
        if(q==1)
        {
            //quantity.buyQuantity-=t.getQuantity();
            mapper m = new mapper(t.getQuantity(),t.getOrderId());
            int bid_quantity = t.getQuantity(), avail_quantity = quantity.sellQuantity;
            if(bid_quantity==avail_quantity)
            {
                for (double sell: sell_Orders.keySet()) {
                    while (sell_Orders.containsKey(sell) && sell_Orders.get(sell).size() != 0) {

                        System.out.println("Order matched: "+sell+" "+m.getQ()+" ID "+TRADE_ID);
                        mapper remove2 = sell_Orders.get(sell).remove(0);
                        allOrders.get(remove2.getId()).setStatus("SUCCESSFUL");
                        TradeOrder tradeOrder = allOrders.get(remove2.getId());
                        tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                        successfullOrders.put(remove2.id, allOrders.get(remove2.id));
                        lastPrice = sell;
                    }
                }
                quantity.sellQuantity = 0;
            }
            else if(bid_quantity<avail_quantity)
            {
                quantity.sellQuantity = avail_quantity - bid_quantity;
                for (double sell: sell_Orders.keySet()) {
                    while (sell_Orders.containsKey(sell) && sell_Orders.get(sell).size() != 0 && bid_quantity>0) {

                        int curr_quantity = sell_Orders.get(sell).get(0).getQ();
                        if(curr_quantity<=bid_quantity)
                        {
                            bid_quantity -= curr_quantity;

                            mapper remove2 = sell_Orders.get(sell).remove(0);
                            allOrders.get(remove2.getId()).setStatus("SUCCESSFUL");
                            System.out.println("Order matched: "+sell+" "+remove2.getQ()+" ID "+TRADE_ID);
                            TradeOrder tradeOrder = allOrders.get(remove2.getId());
                            tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                            successfullOrders.put(remove2.id, allOrders.get(remove2.id));
                        }
                        else
                        {
                            curr_quantity-=bid_quantity;
                            System.out.println("Order matched: "+sell+" "+bid_quantity+" ID "+TRADE_ID);
                            allOrders.get(sell_Orders.get(sell).get(0).getId()).setStatus("PARTIAL");
                            TradeOrder tradeOrder = allOrders.get(sell_Orders.get(sell).get(0).getId());
                            tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                            sell_Orders.get(sell).get(0).q =curr_quantity;
                            bid_quantity =0;
                        }

                        lastPrice = sell;
                    }
                }

            }
            System.out.println("Order matched: "+"BUY-MARKET"+" "+t.getQuantity()+" ID "+TRADE_ID);
            allOrders.get(m.getId()).setStatus("SUCCESSFUL");
            TradeOrder tradeOrder = allOrders.get(m.getId());
            tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
            successfullOrders.put(m.getId(), allOrders.get(m.getId()));
        }
        else
        {
            //quantity.sellQuantity-=t.getQuantity();
            mapper m = new mapper(t.getQuantity(),t.getOrderId());
            int req_quantity = t.getQuantity(), avail_quantity = quantity.buyQuantity;
            if(req_quantity==avail_quantity)
            {
                for (double buy: buy_Orders.keySet()) {
                    while (buy_Orders.containsKey(buy) && buy_Orders.get(buy).size() != 0) {

                        System.out.println("Order matched: "+buy+" "+m.getQ()+" ID "+TRADE_ID);
                        mapper remove2 = buy_Orders.get(buy).remove(0);
                        allOrders.get(remove2.getId()).setStatus("SUCCESSFUL");
                        TradeOrder tradeOrder = allOrders.get(remove2.getId());
                        tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                        successfullOrders.put(remove2.id, allOrders.get(remove2.id));
                        lastPrice = buy;
                    }
                }
                quantity.buyQuantity = 0;
            }
            else if(req_quantity<avail_quantity)
            {
                quantity.buyQuantity = avail_quantity - req_quantity;
                for (double buy: buy_Orders.keySet()) {
                    while (buy_Orders.containsKey(buy) && buy_Orders.get(buy).size() != 0 && req_quantity>0) {

                        int curr_quantity = buy_Orders.get(buy).get(0).getQ();
                        if(curr_quantity<=req_quantity)
                        {
                            req_quantity -= curr_quantity;

                            mapper remove2 = buy_Orders.get(buy).remove(0);
                            System.out.println("Order matched: "+buy+" "+remove2.getQ()+" ID "+TRADE_ID);
                            allOrders.get(remove2.getId()).setStatus("SUCCESSFUL");
                            TradeOrder tradeOrder = allOrders.get(remove2.getId());
                            tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                            successfullOrders.put(remove2.id, allOrders.get(remove2.id));
                        }
                        else
                        {
                            curr_quantity-=req_quantity;
                            System.out.println("Order matched: "+buy+" "+req_quantity+" ID "+TRADE_ID);
                            buy_Orders.get(buy).get(0).q =curr_quantity;
                            allOrders.get(buy_Orders.get(buy).get(0).getId()).setStatus("PARTIAL");
                            TradeOrder tradeOrder = allOrders.get(buy_Orders.get(buy).get(0).getId());
                            tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");

                            req_quantity =0;
                        }

                        lastPrice = buy;
                    }
                }

            }

            System.out.println("Order matched: "+"SELL-MARKET"+" "+t.getQuantity()+" ID "+TRADE_ID);
            allOrders.get(m.getId()).setStatus("SUCCESSFUL");
            TradeOrder tradeOrder = allOrders.get(m.getId());
            tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
            successfullOrders.put(m.id, allOrders.get(m.id));

        }

        return lastPrice;
    }
    private double matchTradeLIMIT(double ask,
                              SortedMap<Double, List<mapper>> bids,
                              SortedMap<Double, List<mapper>> asks,
                              SortedMap<Integer,TradeOrder> allOrders,
                              SortedMap<Integer,TradeOrder> successfulOrders,
                              double curr_price,int TRADE_ID)
    {
        int bid_quantity, ask_quantity;
        double final_price = curr_price;
        if(bids.containsKey(ask) && bids.get(ask).size()!=0)
        {
            TRADE_ID++;
            while(bids.containsKey(ask) && bids.get(ask).size()!=0)
            {
                bid_quantity = bids.get(ask).get(0).q;
                ask_quantity = asks.get(ask).get(0).q;
                if(ask_quantity<bid_quantity)
                {
                    bids.get(ask).get(0).q -= ask_quantity;
                    allOrders.get(bids.get(ask).get(0).getId()).setStatus("PARTIAL");
                    TradeOrder tradeOrder = allOrders.get(bids.get(ask).get(0).getId());
                    tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                    mapper remove = asks.get(ask).remove(0);
                    System.out.println("Order matched: "+ask+" "+ask_quantity+" ID "+TRADE_ID);
                    allOrders.get(remove.getId()).setStatus("SUCCESSFUL");
                    TradeOrder tradeOrder1 = allOrders.get(remove.getId());
                    tradeOrder1.setTradeId(tradeOrder1.getTradeId()+TRADE_ID+" ");
                    successfulOrders.put(remove.id, allOrders.get(remove.id));
                    break;
                }
                else if(ask_quantity>bid_quantity)
                {
                    asks.get(ask).get(0).q -= bid_quantity;
                    allOrders.get(asks.get(ask).get(0).getId()).setStatus("PARTIAL");
                    TradeOrder tradeOrder = allOrders.get(asks.get(ask).get(0).getId());
                    tradeOrder.setTradeId(tradeOrder.getTradeId()+TRADE_ID+" ");
                    mapper remove = bids.get(ask).remove(0);
                    allOrders.get(remove.getId()).setStatus("SUCCESSFUL");
                    TradeOrder tradeOrder1 = allOrders.get(remove.getId());
                    tradeOrder1.setTradeId(tradeOrder1.getTradeId()+TRADE_ID+" ");
                    successfulOrders.put(remove.id, allOrders.get(remove.id));
                    System.out.println("Order matched: "+ask+" "+bid_quantity+" ID "+TRADE_ID);
                }
                else {
                    mapper remove1 = asks.get(ask).remove(0);
                    mapper remove2 = bids.get(ask).remove(0);
                    System.out.println("Order matched: "+ask+" "+ask_quantity+" ID "+TRADE_ID);
                    allOrders.get(remove1.getId()).setStatus("SUCCESSFUL");
                    TradeOrder tradeOrder1 = allOrders.get(remove1.getId());
                    tradeOrder1.setTradeId(tradeOrder1.getTradeId()+TRADE_ID+" ");
                    allOrders.get(remove2.getId()).setStatus("SUCCESSFUL");
                    TradeOrder tradeOrder2 = allOrders.get(remove2.getId());
                    tradeOrder2.setTradeId(tradeOrder2.getTradeId()+TRADE_ID+" ");
                    successfulOrders.put(remove1.id, allOrders.get(remove1.id));
                    successfulOrders.put(remove2.id, allOrders.get(remove2.id));
                    break;

                }
                if(bids.get(ask).size()==0) break;
            }
            final_price = ask;
        }
        else
            System.out.println("No match");


        return final_price;
    }

}
