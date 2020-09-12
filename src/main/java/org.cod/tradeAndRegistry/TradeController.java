package org.cod.tradeAndRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @RequestMapping("/start")
    public void generateTrades()
    {
        tradeService.start();
    }
    @RequestMapping("/Orders/BuyOrders")
    public List<TradeOrder> getAllBuyorders()
    {
        return tradeService.getAllBuyOrders();
    }

    @RequestMapping("/Orders/SellOrders")
    public List<TradeOrder> getAllSellOrders()
    {
        return tradeService.getAllSellOrders();
    }

    @RequestMapping("/Orders/AllOrders")
    public List<TradeOrder> getAllOrders()
    {
        return tradeService.getAllOrders();
    }

    @RequestMapping("/Orders/SUCCESS")
    public List<TradeOrder> getSuccess()
    {
        return tradeService.getAllSuccess("SUCCESSFUL");
    }

    @RequestMapping("/Orders/PARTIAL")
    public List<TradeOrder> getPartial()
    {
        return tradeService.getAllSuccess("PARTIAL");
    }

    @RequestMapping("/Orders/PENDING")
    public List<TradeOrder> getPending()
    {
        return tradeService.getAllSuccess("PENDING");
    }

    @RequestMapping("/Orders/CANCELLED")
    public List<TradeOrder> getCancelled()
    {
        return tradeService.getAllSuccess("CANCELLED");
    }

    @RequestMapping(method = RequestMethod.POST , value = "/Orders/AddOrder")
    public void addCourse(@RequestBody TradeOrder tradeOrder){
        tradeService.addTrade(tradeOrder);
    }


}
