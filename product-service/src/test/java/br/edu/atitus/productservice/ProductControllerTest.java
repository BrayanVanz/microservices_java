package br.edu.atitus.productservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import br.edu.atitus.productservice.clients.CurrencyClient;
import br.edu.atitus.productservice.controllers.ProductController;
import br.edu.atitus.productservice.dtos.ProductDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;

    @ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CurrencyClient currencyClient;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ProductController controller;

    private ProductEntity product;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(controller, "port", "8080");

        product = new ProductEntity();
        product.setId(1L);
        product.setTitle("Chocolate starfish and hot dog flavored water");
        product.setArtist("Limp Bizkit");
        product.setGenre("Nu Metal");
        product.setCategory("CD");
        product.setPrice(100.0);
        product.setCurrency("USD");
        product.setStock(20);
        product.setImageURL("https://upload.wikimedia.org/wikipedia/en/thumb/3/38/Limp_Bizkit_Chocolate_Starfish_and_the_Hotdog_Flavored_Water.jpg/250px-Limp_Bizkit_Chocolate_Starfish_and_the_Hotdog_Flavored_Water.jpg");
        product.setDescription("Chocolate Starfish and the Hot Dog Flavored Water is the third studio album by American nu metal band Limp Bizkit. It was released on October 17, 2000, through Flip and Interscope Records. The album saw the band capitalize on newfound mainstream attention following the success of their previous album, Significant Other. ");
        product.setIsActive(true);
        product.setReleaseDate(LocalDate.of(2000,10,17));
    }

    @Test
    void shouldReturnProductWithoutConversion() throws Exception {

        when(repository.findById(1L))
            .thenReturn(Optional.of(product));

        ResponseEntity<ProductDTO> response =
            controller.getProductNoConverter(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProductDTO dto = response.getBody();

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Chocolate starfish and hot dog flavored water", dto.title());
        assertEquals("Limp Bizkit", dto.artist());
        assertEquals("USD", dto.currency());
        assertEquals(-1.0, dto.convertedPrice());
        assertNull(dto.requestedCurrency());
        assertEquals("Product-service running on port: 8080", dto.environment());
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(repository.findById(1L))
            .thenReturn(Optional.empty());

        Exception exception = assertThrows(
            Exception.class,
            () -> controller.getProductNoConverter(1L));

        assertEquals("Produto não encontrado!", exception.getMessage());
        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnAllProducts() throws Exception {

    Pageable pageable = PageRequest.of(0,5);

    Page<ProductEntity> page =
            new PageImpl<>(List.of(product));

    when(repository.findAll(pageable))
            .thenReturn(page);

    ResponseEntity<Page<ProductDTO>> response =
            controller.getAllProducts("usd", pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertEquals(1,
            response.getBody().getContent().size());

    verify(repository).findAll(pageable);
    }

    @Test
    void shouldReturnActiveProducts() throws Exception {

        Pageable pageable = PageRequest.of(0,6);

        Page<ProductEntity> page =
            new PageImpl<>(List.of(product));

        when(repository.findByIsActiveTrue(pageable))
            .thenReturn(page);

        ResponseEntity<Page<ProductDTO>> response =
            controller.getAllActiveProducts("usd", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1,
            response.getBody().getContent().size());

        verify(repository).findByIsActiveTrue(pageable);
    }

    @Test
    void shouldReturnProductsByGenre() throws Exception {

        Pageable pageable = PageRequest.of(0,6);

        Page<ProductEntity> page =
                new PageImpl<>(List.of(product));

        when(repository.findByGenreIgnoreCase("Pop", pageable))
            .thenReturn(page);

        ResponseEntity<Page<ProductDTO>> response =
            controller.getProductsByGenre("Pop", "usd", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1,
            response.getBody().getContent().size());

        verify(repository)
            .findByGenreIgnoreCase("Pop", pageable);
    }

    @Test
    void shouldReturnProductsByCategory() throws Exception {

        Pageable pageable = PageRequest.of(0,6);

        Page<ProductEntity> page =
            new PageImpl<>(List.of(product));

        when(repository.findByCategoryIgnoreCase("CD", pageable))
            .thenReturn(page);

        ResponseEntity<Page<ProductDTO>> response =
            controller.getProductsByCategory("CD", "usd", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1,
            response.getBody().getContent().size());

        verify(repository)
            .findByCategoryIgnoreCase("CD", pageable);
    }
}