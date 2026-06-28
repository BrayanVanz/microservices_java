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
                .orElseThrow(() -> new Exception("Product not found"));

        ProductDTO dto = buildProductDTO(product, targetCurrency);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/noconverter/{idProduct}")
    public ResponseEntity<ProductDTO> getProductNoConverter(@PathVariable Long idProduct) throws Exception {
        var product = repository.findById(idProduct)
                .orElseThrow(() -> new Exception("Produto não encontrado!"));

        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getArtist(),
                product.getReleaseDate(),
                product.getGenre(),
                product.getIsActive(),
                product.getCategory(),
                product.getPrice(),
                product.getCurrency(),
                product.getStock(),
                product.getImageURL(),
                "Product-service running on port: " + port,
                -1.,
                null,
                product.getDescription());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam String targetCurrency,
            @PageableDefault(page = 0, size = 5, sort = "title", direction = Sort.Direction.ASC) Pageable pageable)
            throws Exception {

        final String currency = targetCurrency.toUpperCase();

        Page<ProductEntity> products = repository.findAll(pageable);

        Page<ProductDTO> productDTOs = products.map(product -> buildProductDTO(product, currency));

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<ProductDTO>> getAllActiveProducts(
            @RequestParam String targetCurrency,
            @PageableDefault(page = 0, size = 6, sort = "title", direction = Sort.Direction.ASC) Pageable pageable)
            throws Exception {

        final String currency = targetCurrency.toUpperCase();

        Page<ProductEntity> products = repository.findByIsActiveTrue(pageable);

        Page<ProductDTO> productDTOs = products.map(product -> buildProductDTO(product, currency));

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<ProductDTO>> getProductsByGenre(
            @PathVariable String genre,
            @RequestParam String targetCurrency,
            @PageableDefault(page = 0, size = 6, sort = "title", direction = Sort.Direction.ASC) Pageable pageable)
            throws Exception {

        final String currency = targetCurrency.toUpperCase();

        Page<ProductEntity> products = repository.findByGenreIgnoreCase(genre, pageable);

        Page<ProductDTO> productDTOs = products.map(product -> buildProductDTO(product, currency));

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam String targetCurrency,
            @PageableDefault(page = 0, size = 6, sort = "title", direction = Sort.Direction.ASC) Pageable pageable)
            throws Exception {

        final String currency = targetCurrency.toUpperCase();

        Page<ProductEntity> products = repository.findByCategoryIgnoreCase(category, pageable);

        Page<ProductDTO> productDTOs = products.map(product -> buildProductDTO(product, currency));

        return ResponseEntity.ok(productDTOs);
    }

    private ProductDTO buildProductDTO(ProductEntity product, String targetCurrency) {

        String environment = "Product-service running on port: " + port;
        Double convertedPrice = null;

        if (targetCurrency.equals(product.getCurrency())) {

            convertedPrice = product.getPrice();

        } else {

            String nameCache = "ConvertedValue";
            String keyCache = product.getCurrency() + "-" + targetCurrency;

            Double convertedValue = null;

            if (convertedValue == null) {

                CurrencyResponse currency = currencyClient.getCurrency(
                        product.getCurrency(),
                        targetCurrency);

                if (currency != null) {

                    convertedPrice = currency.conversionRate() *
                            product.getPrice();

                    environment = environment +
                            " - " +
                            currency.environment();

                    cacheManager
                            .getCache(nameCache)
                            .put(keyCache, currency.conversionRate());

                } else {

                    convertedPrice = -1.0;
                    environment = environment +
                            " - Currency Fallback";
                }

            } // else {
              // convertedPrice = null;
              // }
        }

        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getArtist(),
                product.getReleaseDate(),
                product.getGenre(),
                product.getIsActive(),
                product.getCategory(),
                product.getPrice(),
                product.getCurrency(),
                product.getStock(),
                product.getImageURL(),
                environment,
                convertedPrice,
                targetCurrency,
                product.getDescription());
    }
}
