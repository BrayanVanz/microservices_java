package br.edu.atitus.order_service.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.atitus.order_service.clients.ProductClient;
import br.edu.atitus.order_service.clients.ProductResponse;
import br.edu.atitus.order_service.dtos.OrderDTO;
import br.edu.atitus.order_service.entities.OrderEntity;
import br.edu.atitus.order_service.entities.OrderItemEntity;
import br.edu.atitus.order_service.services.OrderService;

@RestController
@RequestMapping("/ws/orders")
public class OrderController {

	private final OrderService orderService;
	private final ProductClient productClient;

	public OrderController(OrderService orderService, ProductClient productClient) {
		this.orderService = orderService;
		this.productClient = productClient;
	}

	@PostMapping
	public ResponseEntity<OrderEntity> createOrder(
			@RequestBody OrderDTO orderDTO,
			@RequestHeader("X-User-Id") Long userId,
			@RequestHeader("X-User-Email") String userEmail,
			@RequestHeader("X-User-Type") Integer userType) {

		OrderEntity order = new OrderEntity();
		order.setOrderDate(LocalDateTime.now());
		order.setCustomerId(userId);

		List<OrderItemEntity> items = orderDTO.items().stream().map(dto -> {
			OrderItemEntity item = new OrderItemEntity();
			item.setProductId(dto.productId());
			item.setQuantity(dto.quantity());

			ProductResponse product = productClient.getProductById(dto.productId());
			item.setPriceAtPurchase(product.price());
			item.setCurrencyAtPurchase(product.currency());

			item.setProduct(product);

			item.setOrder(order);
			return item;
		}).toList();

		order.setItems(items);

		orderService.createOrder(order, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@GetMapping
	public ResponseEntity<Page<OrderEntity>> listOrdersByUser(
			@RequestParam String targetCurrency,
			@PageableDefault(page = 0, size = 6, sort = "orderDate", direction = Direction.ASC) Pageable pageable,
			@RequestHeader("X-User-Id") Long userId,
			@RequestHeader("X-User-Email") String userEmail,
			@RequestHeader("X-User-Type") Integer userType) {

		targetCurrency = targetCurrency.toUpperCase();

		Page<OrderEntity> orders = userType == 0
				? orderService.findAllOrders(targetCurrency, pageable)
				: orderService.findOrdersByCustomerId(userId, targetCurrency, pageable);

		return ResponseEntity.ok(orders);
	}

	// Finalizes an order: confirms the purchase and reduces stock for every
	// product in the order over at product-service.
	@PatchMapping("/{idOrder}/finalize")
	public ResponseEntity<OrderEntity> finalizeOrder(
			@PathVariable Long idOrder,
			@RequestHeader("X-User-Id") Long userId,
			@RequestHeader("X-User-Email") String userEmail,
			@RequestHeader("X-User-Type") Integer userType) {

		OrderEntity order = orderService.finalizeOrder(idOrder, userId, userType);
		return ResponseEntity.ok(order);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		String message = e.getMessage() == null ? "Erro inesperado." : e.getMessage();
		return ResponseEntity.badRequest().body(message);
	}
}
