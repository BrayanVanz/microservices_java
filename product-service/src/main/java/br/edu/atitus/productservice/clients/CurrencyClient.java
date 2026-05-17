package br.edu.atitus.productservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "currency-service", fallback = CurrencyClientFallback.class)
public interface CurrencyClient {

    @GetMapping("/currency/convert")
    @Retry(name = "currency-service")
    CurrencyResponse getCurrency(@RequestParam String source, @RequestParam String target);
}
