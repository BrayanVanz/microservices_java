package br.edu.atitus.productservice.dtos;

import java.time.LocalDate;

public record ProductDTO(
        Long id,
        String title,
        String artist,
        LocalDate releaseDate,
        String genre,
        Boolean isActive,
        String category,
        Double price,
        String currency,
        Integer stock,
        String imageURL,
        String environment,
        Double convertedPrice,
        String requestedCurrency,
        String description) {
}
