package br.edu.atitus.currencyservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.atitus.currencyservice.entities.CurrencyEntity;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    Optional<CurrencyEntity> findBySourceCurrencyAndTargetCurrency(
        String sourceCurrency, 
        String targetCurrency
    );
}
