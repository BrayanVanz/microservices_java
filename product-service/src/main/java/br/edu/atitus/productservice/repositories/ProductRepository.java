package br.edu.atitus.productservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.atitus.productservice.entities.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findByIsActiveTrue(Pageable pageable);

    Page<ProductEntity> findByGenreIgnoreCase(String genre, Pageable pageable);

    Page<ProductEntity> findByCategoryIgnoreCase(String category, Pageable pageable);
}
