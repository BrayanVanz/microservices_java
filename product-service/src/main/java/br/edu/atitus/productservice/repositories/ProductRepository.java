package br.edu.atitus.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.atitus.productservice.entities.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
