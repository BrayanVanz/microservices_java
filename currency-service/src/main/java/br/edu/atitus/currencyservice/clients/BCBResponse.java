package br.edu.atitus.currencyservice.clients;

import java.util.List;

public record BCBResponse(List<BCBCurrencies> values) {

    public record BCBCurrencies(Double cotacaoVenda){}
}
