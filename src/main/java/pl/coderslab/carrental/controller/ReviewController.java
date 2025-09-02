package pl.coderslab.carrental.controller;

import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.ReviewDto;
import pl.coderslab.carrental.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewDto> getReviews() {

        return reviewService.findAllReviews();
    }

    @PostMapping
    public ReviewDto addReview(@RequestBody ReviewDto reviewDto) {

        return reviewService.addReview(reviewDto);
    }

    @DeleteMapping
    public void deleteReview(@RequestParam Long id) {

        reviewService.deleteReview(id);
    }

}
