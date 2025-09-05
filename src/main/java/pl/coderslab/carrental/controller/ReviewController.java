package pl.coderslab.carrental.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.ReviewDto;
import pl.coderslab.carrental.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews() {

        return new ResponseEntity<>(reviewService.findAllReviews(), HttpStatus.OK);
    }

    @PostMapping("/review")
    public ResponseEntity<ReviewDto> addReview(@RequestBody ReviewDto reviewDto) {

        return new ResponseEntity<>(reviewService.addReview(reviewDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/review")
    public ResponseEntity<String> deleteReview(@RequestParam Long id) {

        reviewService.deleteReview(id);
        return new ResponseEntity<>("Removed review with id: " + id, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/review")
    public ResponseEntity<ReviewDto> updateReview(@RequestParam Long id, @RequestBody ReviewDto reviewDto) {

        return new ResponseEntity<>(reviewService.updateReview(id, reviewDto), HttpStatus.OK);
    }
}
