package br.edu.atitus.productservice.controllers;

import javax.naming.AuthenticationException;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.productservice.dtos.ProductInDTO;
import br.edu.atitus.productservice.dtos.StockReductionItemDTO;
import br.edu.atitus.productservice.dtos.StockReductionRequestDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ws/products")
public class WsProductController {

    private final ProductRepository repository;

    public WsProductController(ProductRepository repository) {
        this.repository = repository;
    }

    private ProductEntity convertDTOtoEntity(ProductInDTO dto){
        var entity = new ProductEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @PostMapping
    public ResponseEntity<ProductEntity> postProduct(
            @RequestBody ProductInDTO dto,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer type) throws AuthenticationException {
        if (type != 0)
            throw new AuthenticationException("Usuário sem Permissão!");

        var product = convertDTOtoEntity(dto);
        product.setStock(10);
        product.setIsActive(true);
        repository.save(product);
        return ResponseEntity.status(201).body(product);
    }

    @PutMapping("/{idProduct}")
    public ResponseEntity<ProductEntity> putProduct(
            @PathVariable Long idProduct,
            @RequestBody ProductInDTO dto,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer type) throws AuthenticationException {
        if (type != 0)
            throw new AuthenticationException("Usuário sem Permissão!");

        var existingProduct = repository.findById(idProduct)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

        var product = convertDTOtoEntity(dto);
        product.setId(idProduct);
        product.setStock(existingProduct.getStock());
        product.setIsActive(existingProduct.getIsActive());
        repository.save(product);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{idProduct}/deactivate")
    public ResponseEntity<ProductEntity> deactivateProduct(
            @PathVariable Long idProduct,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer type) throws AuthenticationException {
        if (type != 0)
            throw new AuthenticationException("Usuário sem Permissão!");

        var product = repository.findById(idProduct)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

        product.setIsActive(false);
        repository.save(product);

        return ResponseEntity.ok(product);
    }

    // Internal endpoint called by order-service when an order is finalized,
    // so the stock of every purchased product is decremented accordingly.
    @PatchMapping("/reduce-stock")
    public ResponseEntity<?> reduceStock(@RequestBody StockReductionRequestDTO request) {

        if (request == null || request.items() == null || request.items().isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum item informado para baixa de estoque.");
        }

        // Validate all products and quantities before applying any change,
        // so the stock update is all-or-nothing for the order.
        List<ProductEntity> productsToUpdate = new ArrayList<>();

        for (StockReductionItemDTO item : request.items()) {
            ProductEntity product = repository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produto não encontrado: " + item.productId()));

            if (item.quantity() == null || item.quantity() <= 0) {
                throw new RuntimeException(
                        "Quantidade inválida para o produto: " + item.productId());
            }

            if (product.getStock() < item.quantity()) {
                throw new RuntimeException(
                        "Estoque insuficiente para o produto: " + product.getTitle());
            }

            product.setStock(product.getStock() - item.quantity());
            productsToUpdate.add(product);
        }

        repository.saveAll(productsToUpdate);

        return ResponseEntity.ok(productsToUpdate);
    }

    @DeleteMapping("/{idProduct}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long idProduct,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer type) throws AuthenticationException {
        if (type != 0)
            throw new AuthenticationException("Usuário sem Permissão!");

        repository.deleteById(idProduct);

        return ResponseEntity.ok("Excluído");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e){
        String message = e.getMessage().replace("/r/n", "");
        return ResponseEntity.badRequest().body(message);
    }
}
