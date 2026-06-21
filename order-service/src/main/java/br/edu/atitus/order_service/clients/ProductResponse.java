package br.edu.atitus.order_service.clients;

import java.time.LocalDate;

public record ProductResponse(
	    Long id,
	    String title,
	    String artist,
	    LocalDate releaseDate,
	    String genre,
	    Boolean isActive,
	    String category,
	    double price,
	    String currency,
	    Integer stock,
	    String imageURL,
	    String environment,
	    double convertedPrice
	) {}
