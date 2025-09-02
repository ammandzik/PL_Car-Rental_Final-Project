package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.ReviewDto;
import pl.coderslab.carrental.model.Review;

@Component
public class ReviewMapper {

    public ReviewDto toDto(Review review) {

        return ReviewDto.builder()
                .id(review.getId())
                .description(review.getDescription())
                .pseudonym(review.getPseudonym())
                .rating(review.getRating())
                .reservationId(review.getReservation().getId())
                .build();
    }

    public Review toEntity(ReviewDto reviewDto) {

        return Review.builder()
                .id(reviewDto.getId())
                .description(reviewDto.getDescription())
                .pseudonym(reviewDto.getPseudonym())
                .rating(reviewDto.getRating())
                .build();
    }
}
