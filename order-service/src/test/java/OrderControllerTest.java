import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.edu.atitus.order_service.clients.ProductClient;
import br.edu.atitus.order_service.clients.ProductResponse;
import br.edu.atitus.order_service.controllers.OrderController;
import br.edu.atitus.order_service.dtos.OrderDTO;
import br.edu.atitus.order_service.dtos.OrderItemDTO;
import br.edu.atitus.order_service.entities.OrderEntity;
import br.edu.atitus.order_service.entities.OrderItemEntity;
import br.edu.atitus.order_service.services.OrderService;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderController controller;

    private OrderDTO orderDTO;
    private OrderEntity order;
    private ProductResponse product;

    @BeforeEach
    void setUp() {

        product = new ProductResponse(
                1L,
                "Thriller",
                "Michael Jackson",
                LocalDate.of(1982, 11, 30),
                "Pop",
                true,
                "CD",
                100.0,
                "USD",
                10,
                "image.png",
                "product-service",
                100.0
        );

        orderDTO = new OrderDTO(
                List.of(
                        new OrderItemDTO(1L, 2)
                )
        );

        order = new OrderEntity();
        order.setId(1L);
        order.setCustomerId(5L);
        order.setOrderDate(LocalDateTime.now());
    }

    @Test
    void shouldCreateOrder() {

        when(productClient.getProductById(1L))
            .thenReturn(product);

        when(orderService.createOrder(any(OrderEntity.class), eq(5L)))
            .thenReturn(order);

        ResponseEntity<OrderEntity> response =
            controller.createOrder(
                    orderDTO,
                    5L,
                    "user@email.com",
                    1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(1, response.getBody().getItems().size());

        OrderItemEntity item = response.getBody().getItems().get(0);

        assertEquals(1L, item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(100.0, item.getPriceAtPurchase());
        assertEquals("USD", item.getCurrencyAtPurchase());

        verify(productClient).getProductById(1L);

        verify(orderService)
            .createOrder(any(OrderEntity.class), eq(5L));
    }

    @Test
    void shouldReturnAllOrdersWhenAdmin() {

        Pageable pageable = PageRequest.of(0, 6);

        Page<OrderEntity> page =
            new PageImpl<>(List.of(order));

        when(orderService.findAllOrders("USD", pageable))
            .thenReturn(page);

        ResponseEntity<Page<OrderEntity>> response =
            controller.listOrdersByUser(
                    "usd",
                    pageable,
                    5L,
                    "admin@email.com",
                    0);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1,
            response.getBody().getContent().size());

        verify(orderService)
            .findAllOrders("USD", pageable);
    }

    @Test
    void shouldReturnOrdersByCustomer() {

        Pageable pageable = PageRequest.of(0, 6);

        Page<OrderEntity> page =
            new PageImpl<>(List.of(order));

        when(orderService.findOrdersByCustomerId(
            5L,
            "USD",
            pageable))
            .thenReturn(page);

        ResponseEntity<Page<OrderEntity>> response =
            controller.listOrdersByUser(
                    "usd",
                    pageable,
                    5L,
                    "user@email.com",
                    1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1,
            response.getBody().getContent().size());

        verify(orderService)
            .findOrdersByCustomerId(
                    5L,
                    "USD",
                    pageable);
    }

    @Test
    void shouldFinalizeOrder() {

        when(orderService.finalizeOrder(
            1L,
            5L,
            1))
            .thenReturn(order);

        ResponseEntity<OrderEntity> response =
            controller.finalizeOrder(
                    1L,
                    5L,
                    "user@email.com",
                    1);

        assertEquals(HttpStatus.OK,
            response.getStatusCode());

        assertEquals(order,
            response.getBody());

        verify(orderService)
            .finalizeOrder(
                    1L,
                    5L,
                    1);
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
