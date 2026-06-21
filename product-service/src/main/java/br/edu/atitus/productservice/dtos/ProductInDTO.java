package br.edu.atitus.productservice.dtos;

import java.time.LocalDate;

public record ProductInDTO(
    String title,
    String artist,
    LocalDate releaseDate,
    String genre,
    String category,
    String currency,
    Double price,
    String imageURL) {
}
