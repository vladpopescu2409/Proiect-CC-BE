package com.project.HR.Connect.controller;

import com.project.HR.Connect.entitie.Article;
import com.project.HR.Connect.entitie.Feedback;
import com.project.HR.Connect.security.JWTUtils;
import com.project.HR.Connect.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
@CrossOrigin(origins = "http://localhost:4200")
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    JWTUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> addNewFeedback(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody Feedback feedback) {
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        var out = feedbackService.addNewFeedback(feedback, email);
        if (out.getFirst()) {
            return ResponseEntity.ok(out.getSecond());
        } else {
            return ResponseEntity.badRequest().body(out.getSecond());
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('hr')")
    public ResponseEntity<?> deleteFeedback(@RequestParam("id") Integer id) {
        if(feedbackService.deleteFeedback(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('hr')")
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @GetMapping("/by")
    public ResponseEntity<?> getFeedbackAuthor(@RequestParam("id") Integer id) {
        Optional<Feedback> fb = feedbackService.getFeedbackById(id);
        Feedback feedback = fb.get();
        return ResponseEntity.ok(feedback.getCreatedBy().getFirstName() + " " + feedback.getCreatedBy().getLastName());
    }

    @PostMapping("/fav")
    @PreAuthorize("hasRole('hr')")
    public ResponseEntity<?> updateFavouriteFeedbackValue(@RequestParam("id") Integer id) {
        feedbackService.updateFavourite(id);
        return ResponseEntity.ok("Favourite state updated!");
    }
}
