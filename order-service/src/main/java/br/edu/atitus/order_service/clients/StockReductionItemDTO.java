package br.edu.atitus.order_service.clients;

public record StockReductionItemDTO(
    Long productId,
    Integer quantity) {
}
