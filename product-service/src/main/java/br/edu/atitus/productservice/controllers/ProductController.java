package br.edu.atitus.productservice.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final CacheManager cacheManager;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductRepository repository, CurrencyClient currencyClient, CacheManager cacheManager) {
        this.repository = repository;
        this.currencyClient = currencyClient;
        this.cacheManager = cacheManager;
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
            String nameCache = "ConvertedValue";
            String keyCache = product.getCurrency() + "-" + targetCurrency;

            // Disable caching
            //Double convertedValue = cacheManager.getCache(nameCache).get(keyCache, Double.class);
            Double convertedValue = null;

            if (convertedValue == null) {
                CurrencyResponse currency = currencyClient.getCurrency(product.getCurrency(), targetCurrency);

                if (currency != null) {
                    convertedPrice = product.getPrice() * currency.conversionRate();
                    environment = environment + " - " + currency.environment();
                    cacheManager.getCache(nameCache).put(keyCache, currency.conversionRate());
                } else {
                    convertedPrice = -1.0;
                    environment = environment + " - Currency Fallback";
                }
            } //else {
            //    convertedPrice = convertedValue * product.getPrice();
            //    environment = environment + " - Currency in cache";
            //}
        }

        ProductDTO dto = new ProductDTO(
            product.getId(),
            product.getDescription(),
            product.getBrand(),
            product.getModel(),
            product.getPrice(),
            product.getCurrency(),
            product.getStock(),
            product.getImageURL(),
            environment,
            convertedPrice,
            targetCurrency
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/noconverter/{idProduct}")
    public ResponseEntity<ProductDTO> getProductNoConverter(@PathVariable Long idProduct) throws Exception {
        var product = repository.findById(idProduct)
                .orElseThrow(() -> new Exception("Produto não encontrado!"));

        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getDescription(),
                product.getBrand(),
                product.getModel(),
                product.getPrice(),
                product.getCurrency(),
                product.getStock(),
                product.getImageURL(),
                "Product-service running on port: " + port,
                -1.,
                null
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
        @RequestParam String targetCurrency,
        @PageableDefault(
                page = 0,
                size = 5,
                sort = "description",
                direction = Sort.Direction.ASC
        ) Pageable pageable) throws Exception {

        Page<ProductEntity> products = repository.findAll(pageable);

        Page<ProductDTO> productDTOs = products.map(product -> {

            String environment = "Product-service running on port: " + port;
            Double convertedPrice = null;

            if (targetCurrency.equals(product.getCurrency())) {

                convertedPrice = product.getPrice();

            } else {

                String nameCache = "ConvertedValue";
                String keyCache = product.getCurrency() + "-" + targetCurrency;

                Double convertedValue = null;

                if (convertedValue == null) {

                    CurrencyResponse currency =
                            currencyClient.getCurrency(
                                    product.getCurrency(),
                                    targetCurrency
                            );

                    if (currency != null) {

                        convertedPrice =
                                currency.conversionRate() *
                                product.getPrice();

                        environment =
                                environment +
                                " - " +
                                currency.environment();

                        cacheManager
                                .getCache(nameCache)
                                .put(keyCache, currency.conversionRate());

                    } else {

                        convertedPrice = -1.0;
                        environment =
                                environment +
                                " - Currency Fallback";
                    }

                } //else {

                //     convertedPrice =
                //             convertedValue *
                //             product.getPrice();

                //     environment =
                //             environment +
                //             " - Currency in cache";
                // }
            }

            return new ProductDTO(
                    product.getId(),
                    product.getDescription(),
                    product.getBrand(),
                    product.getModel(),
                    product.getPrice(),
                    product.getCurrency(),
                    product.getStock(),
                    product.getImageURL(),
                    environment,
                    convertedPrice,
                    targetCurrency
            );
        });

        return ResponseEntity.ok(productDTOs);
    }
}
