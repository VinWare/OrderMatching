package org.cod.tradeAndRegistry;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public  class TradeOrder implements Cloneable {

    @Id
    private int orderId;
    //private List<Integer> tradeId = new ArrayList<>();
    private String orderTime;
    private int quantity;
    private String tradeType;
    private String orderType;
    private double price;
    private String status;
    private String tradeId = "";

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String orderToString() {
        if(!this.orderType.equals("MARKET"))
            return "Order[" +
                "orderId='" + getOrderId() + '\'' +
                ", tradeId='" + getTradeId() + '\'' +
                ", tradeType='" + getTradeType() + '\'' +
                ", orderTime='" + getOrderTime() + '\'' +
                ", quantity=" + getQuantity() +
                ", orderType='" + getOrderType() + '\'' +
                ", price='" + getPrice() + '\'' +
                " ]";
        else
            return "Order[" +
                    "orderId='" + getOrderId() + '\'' +
                   ", tradeId='" + getTradeId() + '\'' +
                    ", tradeType='" + getTradeType() + '\'' +
                    ", orderTime='" + getOrderTime() + '\'' +
                    ", quantity=" + getQuantity() +
                    ", orderType='" + getOrderType() + '\'' +
                    " ]";

    }

    @Override
    public String toString() {
        return "Trade"+orderToString();
    }

    public TradeOrder() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

//    public List<Integer> getTradeId() {
//        return this.tradeId;
//    }
//
//    public void setTradeId(int tradeId) {
//        this.tradeId.add(tradeId);
//    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
