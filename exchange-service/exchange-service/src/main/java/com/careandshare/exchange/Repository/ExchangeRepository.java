package com.careandshare.exchange.Repository;


import com.careandshare.exchange.Model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findByRequesterId(Long requesterId);
    List<Exchange> findByReceiverId(Long receiverId);
}
