package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.ReviewDto;
import pl.coderslab.carrental.exception.ReviewExistsException;
import pl.coderslab.carrental.exception.ReviewNotAllowedYetException;
import pl.coderslab.carrental.mapper.ReviewMapper;
import pl.coderslab.carrental.model.Reservation;
import pl.coderslab.carrental.repository.ReservationRepository;
import pl.coderslab.carrental.repository.ReviewRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReservationRepository reservationRepository;

    public List<ReviewDto> findAllReviews() {

        log.info("Find all reviews method invoked");

        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public ReviewDto addReview(ReviewDto reviewDto) {

        log.info("Add review method invoked");

        if (reviewDto != null) {

            var reviewExists = reviewRepository.existsByReservationId(reviewDto.getReservationId());
            var reservation = findReservationOrElseThrow(reviewDto.getReservationId());

            if (!reservation.getDateTo().isBefore(LocalDate.now()) || !reservation.isConfirmed()) {
                throw new ReviewNotAllowedYetException("Adding review is not allowed yet.");
            }

            if (reviewExists) {
                throw new ReviewExistsException(String.format("Review for reservation with id %s already exists", reviewDto.getReservationId()));
            }

            var reviewEntity = reviewMapper.toEntity(reviewDto);
            reviewEntity.setReservation(reservation);

            log.info("Saving review to DB");
            return reviewMapper.toDto(reviewRepository.save(reviewEntity));
        } else {
            throw new IllegalArgumentException("Review should not be null");
        }
    }

    public void deleteReview(Long id) {

        log.info("Delete review method invoked");

        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            log.info("Deleted review with id {}", id);
        } else {
            throw new EntityNotFoundException(String.format("Review with id %s not found", id));
        }

    }

    public ReviewDto updateReview(Long id, ReviewDto reviewDto) {

        log.info("Update review method invoked");

        var review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Review with id %s not found", id)));

        var reservation = findReservationOrElseThrow(reviewDto.getReservationId());

        review.setDescription(reviewDto.getDescription());
        review.setRating(reviewDto.getRating());
        review.setPseudonym(reviewDto.getPseudonym());

        review.setReservation(reservation);

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    private Reservation findReservationOrElseThrow(Long id) {

        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Reservation not found with id %s", id)));

    }
}
