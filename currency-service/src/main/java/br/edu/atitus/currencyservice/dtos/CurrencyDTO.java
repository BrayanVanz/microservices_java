package br.edu.atitus.currencyservice.dtos;

public record CurrencyDTO(
    Long id,
    String sourceCurrency,
    String targetCurrency,
    Double conversionRate,
    String environment) {

}
