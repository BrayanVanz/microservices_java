package br.edu.atitus.productservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.edu.atitus.productservice.controllers.WsProductController;
import br.edu.atitus.productservice.dtos.ProductInDTO;
import br.edu.atitus.productservice.dtos.StockReductionItemDTO;
import br.edu.atitus.productservice.dtos.StockReductionRequestDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
class WsProductControllerTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private WsProductController controller;

    private ProductEntity product;
    private ProductInDTO dto;

    @BeforeEach
    void setUp() {

        dto = new ProductInDTO(
            "Thriller",
            "Michael Jackson",
            LocalDate.of(1982, 11, 30),
            "Pop",
            "CD",
            "USD",
            100.0,
            "image.png",
            "Album description"
    );

        product = new ProductEntity();
        product.setId(1L);
        product.setTitle("Thriller");
        product.setArtist("Michael Jackson");
        product.setReleaseDate(LocalDate.of(1982, 11, 30));
        product.setGenre("Pop");
        product.setCategory("CD");
        product.setCurrency("USD");
        product.setPrice(100.0);
        product.setImageURL("image.png");
        product.setDescription("Album description");
        product.setStock(10);
        product.setIsActive(true);
    }

    @Test
    void shouldCreateProduct() throws Exception {

        when(repository.save(any(ProductEntity.class)))
            .thenReturn(product);

        ResponseEntity<ProductEntity> response =
            controller.postProduct(dto, 1L, "admin@email.com", 0);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProductEntity entity = response.getBody();

        assertNotNull(entity);
        assertTrue(entity.getIsActive());
        assertEquals(10, entity.getStock());

        verify(repository).save(any(ProductEntity.class));
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserIsNotAdmin() {

        AuthenticationException exception =
            assertThrows(AuthenticationException.class,
                    () -> controller.postProduct(dto, 1L, "user@email.com", 1));

        assertEquals("Usuário sem Permissão!", exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void shouldUpdateProduct() throws Exception {

        when(repository.findById(1L))
            .thenReturn(Optional.of(product));

        when(repository.save(any(ProductEntity.class)))
            .thenReturn(product);

        ResponseEntity<ProductEntity> response =
            controller.putProduct(1L, dto, 1L, "admin@email.com", 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(repository).findById(1L);
        verify(repository).save(any(ProductEntity.class));
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingProduct() {

        when(repository.findById(1L))
            .thenReturn(Optional.empty());

        RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> controller.putProduct(1L, dto, 1L, "admin@email.com", 0));

        assertEquals("Produto não encontrado!", exception.getMessage());

        verify(repository).findById(1L);    
    }

    @Test
    void shouldDeactivateProduct() throws Exception {

        when(repository.findById(1L))
            .thenReturn(Optional.of(product));

        ResponseEntity<ProductEntity> response =
            controller.deactivateProduct(1L,1L,"admin@email.com",0);

        assertFalse(response.getBody().getIsActive());

        verify(repository).save(product);
    }

    @Test
    void shouldReduceStock() {

        StockReductionItemDTO item =
            new StockReductionItemDTO(1L, 2);

        StockReductionRequestDTO request =
            new StockReductionRequestDTO(List.of(item));

        when(repository.findById(1L))
            .thenReturn(Optional.of(product));

        ResponseEntity<?> response =
            controller.reduceStock(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(8, product.getStock());

        verify(repository).saveAll(anyList());
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsEmpty() {

        StockReductionRequestDTO request =
            new StockReductionRequestDTO(List.of());

        ResponseEntity<?> response =
            controller.reduceStock(request);

        assertEquals(HttpStatus.BAD_REQUEST,
            response.getStatusCode());

        assertEquals(
            "Nenhum item informado para baixa de estoque.",
            response.getBody());
    }

    @Test
    void shouldThrowWhenStockIsInsufficient() {

        StockReductionItemDTO item =
            new StockReductionItemDTO(1L, 20);

        StockReductionRequestDTO request =
            new StockReductionRequestDTO(List.of(item));

        when(repository.findById(1L))
            .thenReturn(Optional.of(product));

        RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> controller.reduceStock(request));

        assertEquals(
            "Estoque insuficiente para o produto: Thriller",
            exception.getMessage());
    }

    @Test
    void shouldDeleteProduct() throws Exception {

        ResponseEntity<String> response =
            controller.deleteProduct(1L,1L,"admin@email.com",0);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Excluído", response.getBody());

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldHandleException() {

        Exception exception =
            new Exception("Erro qualquer");

        ResponseEntity<String> response =
            controller.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST,
            response.getStatusCode());

        assertEquals("Erro qualquer",
            response.getBody());
    }
}
