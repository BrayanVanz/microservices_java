package br.edu.atitus.currencyservice.clients;

public class BCBClientFallback implements BCBClient {

    @Override
    public BCBResponse getBCBCurrency(String moeda) {
        return null;
    }

}
