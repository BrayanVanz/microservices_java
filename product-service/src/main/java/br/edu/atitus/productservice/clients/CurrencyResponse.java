package br.edu.atitus.productservice.clients;

public record CurrencyResponse(    
    Long id,
    String sourceCurrency,
    String targetCurrency,
    Double conversionRate,
    String environment) {

}
