package br.edu.atitus.productservice.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.productservice.clients.CurrencyClient;
import br.edu.atitus.productservice.clients.CurrencyResponse;
import br.edu.atitus.productservice.dtos.ProductDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;
    private final CurrencyClient currencyClient;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductRepository repository, CurrencyClient currencyClient) {
        this.repository = repository;
        this.currencyClient = currencyClient;
    }

    @GetMapping("/{idproduct}")
    public ResponseEntity<ProductDTO> getProducts(@PathVariable Long idproduct, 
        @RequestParam String targetCurrency) throws Exception {

        targetCurrency = targetCurrency.toUpperCase();

        ProductEntity product = repository
            .findById(idproduct)
            .orElseThrow( () -> new Exception("Product not found"));
        
        Double convertedPrice = null;
        String environment = "Product-service running on Port: " + port;

        if (targetCurrency.equals(product.getCurrency())) {
            convertedPrice = product.getPrice();
        } else {
            CurrencyResponse currency = currencyClient.getCurrency(product.getCurrency(), targetCurrency);
            convertedPrice = product.getPrice() * currency.conversionRate();
            environment = environment + " - " + currency.environment();
        }

        ProductDTO dto = new ProductDTO(
            product.getId(),
            product.getDescription(),
            product.getBrand(),
            product.getModel(),
            product.getPrice(),
            product.getCurrency(),
            product.getStock(),
            environment,
            convertedPrice,
            targetCurrency
        );

        return ResponseEntity.ok(dto);
    }
}
