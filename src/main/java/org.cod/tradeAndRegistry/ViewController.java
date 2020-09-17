package org.cod.tradeAndRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @Autowired
    private TradeService tradeService;

    void latest(final Model model) {
        final var buy = tradeService.getAllLimitByTradeType("BUY");
        final var sell = tradeService.getAllLimitByTradeType("SELL");
        final var bid = buy == null || buy.size() == 0? 0 : buy.get(buy.size()-1).getPrice();
        final var ask = sell == null || sell.size() == 0 ? 0 : sell.get(sell.size()-1).getPrice();
        final var current = tradeService.getLast();
//        System.out.println(last.get(last.size()-1));
        model.addAttribute("bid", bid);
        model.addAttribute("ask", ask);
        model.addAttribute("current", current);
    }
    @GetMapping("/newOrder")
    String newOrder(final Model model) {
        latest(model);
        return "order_entry_final";
    }
    @GetMapping("/pendingOrders")
    String pendingOrders(final Model model){
        model.addAttribute("orders", tradeService.getAllSuccess("PENDING"));
        latest(model);
        return "pending_orders";
    }
    @GetMapping("/rejectedOrders")
    String rejectedOrders(final Model model) {
        model.addAttribute("orders", tradeService.getAllSuccess("CANCELLED"));
        latest(model);
        return "rejected_orders";
    }
    @GetMapping("/successfulOrders")
    String successfulOrders(final Model model) {
        model.addAttribute("orders", tradeService.getAllSuccess("SUCCESSFUL"));
        latest(model);
        return "successful_trades";
    }
}
