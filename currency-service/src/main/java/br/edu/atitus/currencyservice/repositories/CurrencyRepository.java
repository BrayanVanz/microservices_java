package br.edu.atitus.currencyservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.atitus.currencyservice.entities.CurrencyEntity;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    Optional<CurrencyEntity> findBySourceCurrencyAndTargetCurrency(
        String sourceCurrency, 
        String targetCurrency
    );
}
