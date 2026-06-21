package br.edu.atitus.order_service.clients;

import java.util.List;

public record StockReductionRequestDTO(
    List<StockReductionItemDTO> items) {
}
