package br.edu.atitus.productservice.dtos;

public record StockReductionItemDTO(
    Long productId,
    Integer quantity) {
}
