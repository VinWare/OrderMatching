package org.cod.tradeAndRegistry;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Registry {
        private Map<String, TradeOrder> Trades = new HashMap<>();

        public Registry() {
            loadTrades();
        }

        public TradeOrder createTrade (String type, String ordertype) {
            TradeOrder TradeOrder = null;

            try {
                TradeOrder = (TradeOrder)(Trades.get(type)).clone();
                TradeOrder.setOrderType(ordertype);
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return TradeOrder;
        }

        private void loadTrades() {
            //BUY ORDER DEFAULT
            TradeOrder buyOrder = new TradeOrder();
            buyOrder.setOrderId(0);
            //buyOrder.setTradeId(0);
            buyOrder.setOrderTime(LocalTime.now().toString());
            buyOrder.setPrice(1);
            buyOrder.setQuantity(1);
            buyOrder.setTradeType("BUY");
            buyOrder.setStatus("PENDING");
            Trades.put("BUY", buyOrder);

            //SELL ORDER DEFAULT
            TradeOrder sellOrder = new TradeOrder();
            sellOrder.setOrderId(0);
            //sellOrder.setTradeId(0);
            sellOrder.setOrderTime(LocalTime.now().toString());
            sellOrder.setPrice(1);
            sellOrder.setQuantity(1);
            sellOrder.setTradeType("SELL");
            sellOrder.setStatus("PENDING");
            Trades.put("SELL", sellOrder);
        }
}
