package br.edu.atitus.order_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.atitus.order_service.entities.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>{
	Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);
}
