package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.ReviewDto;
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

    @Cacheable(value = "review", key = "#id")
    public ReviewDto findReviewById(Long id) {
        log.info("Find review by id method invoked");

        return reviewRepository.findById(id)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Review not found with id %s", id)));
    }

    public ReviewDto addReview(ReviewDto reviewDto) {

        log.info("Add review method invoked");

        if (reviewDto != null) {

            var reviewExists = reviewRepository.existsByReservationId(reviewDto.getReservationId());
            var reservation = findReviewReservationOrElseThrow(reviewDto.getReservationId());

            validateReviewAddingIsAllowed(reservation);
            checkIfReviewExists(reviewDto, reviewExists);

            var reviewEntity = reviewMapper.toEntity(reviewDto);
            reviewEntity.setReservation(reservation);

            log.info("Saving review to DB");
            return reviewMapper.toDto(reviewRepository.save(reviewEntity));
        } else {
            throw new IllegalArgumentException("Review should not be null");
        }
    }

    @CacheEvict(value = "review", key = "#id")
    public void deleteReview(Long id) {

        log.info("Delete review method invoked");

        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            log.info("Deleted review with id {}", id);
        } else {
            throw new EntityNotFoundException(String.format("Review with id %s not found", id));
        }

    }

    @CachePut(value = "review", key = "#id")
    @Transactional
    public ReviewDto updateReview(Long id, ReviewDto reviewDto) {

        log.info("Update review method invoked");

        var review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Review with id %s not found", id)));

        var reservation = findReviewReservationOrElseThrow(reviewDto.getReservationId());

        review.setDescription(reviewDto.getDescription());
        review.setRating(reviewDto.getRating());
        review.setPseudonym(reviewDto.getPseudonym());

        review.setReservation(reservation);

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    private Reservation findReviewReservationOrElseThrow(Long id) {

        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Reservation not found with id %s", id)));

    }

    private static void checkIfReviewExists(ReviewDto reviewDto, boolean reviewExists) {
        if (reviewExists) {
            throw new EntityExistsException(String.format("Review for reservation with id %s already exists", reviewDto.getReservationId()));
        }
    }

    private static void validateReviewAddingIsAllowed(Reservation reservation) {
        if (!reservation.getDateTo().isBefore(LocalDate.now()) || !reservation.isConfirmed()) {
            throw new ReviewNotAllowedYetException("Adding review is not allowed.");
        }
    }
}
