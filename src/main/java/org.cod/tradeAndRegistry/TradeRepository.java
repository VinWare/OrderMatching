package org.cod.tradeAndRegistry;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TradeRepository extends CrudRepository<TradeOrder,String> {
    public List<TradeOrder> findByTradeType(String TradeType);

    public List<TradeOrder> findByStatus(String successful);
}
