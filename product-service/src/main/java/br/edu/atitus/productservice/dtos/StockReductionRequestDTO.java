package br.edu.atitus.productservice.dtos;

import java.util.List;

public record StockReductionRequestDTO(
    List<StockReductionItemDTO> items) {
}
