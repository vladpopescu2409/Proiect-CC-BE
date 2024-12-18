package com.project.HR.Connect.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.HR.Connect.entitie.Feedback;
import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    UserService userService;

    public Pair<Boolean,String> addNewFeedback(Feedback feedback, String email) {
        if(feedback == null) {
            return Pair.of(false, "You feedback data is incomplete!");
        }
        User currentUser = userService.getUserByLoginDetailsEmail(email);

        try {
            feedback.setCreatedBy(currentUser);
            feedback.setRating(false);
            feedbackRepository.save(feedback);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(feedback);
            return Pair.of(true, json);
        } catch (JsonProcessingException e) {
            return Pair.of(false, "Feedback was not added because of a json error: " + e.getMessage());
        }
    }

    public boolean deleteFeedback(Integer ID) {
        try {
            Optional<Feedback> fb = feedbackRepository.findById(ID);
            if (fb.isEmpty()) {
                return false;
            }

            Feedback feedback = fb.get();
            feedbackRepository.delete(feedback);
        } catch (DataIntegrityViolationException e) {
            return false;
        }
        return true;
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> getFeedbackById(Integer ID) {
        return feedbackRepository.findById(ID);
    }

    public void updateFavourite(Integer ID) {
        Optional<Feedback> fb = feedbackRepository.findById(ID);
        if (fb.isEmpty()) {
            return;
        }
        Feedback feedback = fb.get();
        boolean value = feedback.isRating();
        feedback.setRating(!value);
        feedbackRepository.save(feedback);
    }
}
