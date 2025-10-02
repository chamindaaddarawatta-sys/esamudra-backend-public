package com.esamudra.backend.controller;

import com.esamudra.backend.model.Feedback; // ← ADD THIS IMPORT
import com.esamudra.backend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/summary")
    public Map<String, Object> getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Total feedback count
        long totalFeedback = feedbackRepository.count();
        summary.put("totalFeedback", totalFeedback);

        // Sentiment distribution
        Map<String, Long> sentimentStats = new HashMap<>();
        sentimentStats.put("POSITIVE", feedbackRepository.countBySentiment("POSITIVE"));
        sentimentStats.put("NEGATIVE", feedbackRepository.countBySentiment("NEGATIVE"));
        sentimentStats.put("NEUTRAL", feedbackRepository.countBySentiment("NEUTRAL"));
        summary.put("sentimentDistribution", sentimentStats);

        // Average ratings
        summary.put("avgOverallSatisfaction", feedbackRepository.getAverageOverallSatisfaction());
        summary.put("avgRoomCleanliness", feedbackRepository.getAverageRoomCleanliness());
        summary.put("avgStaffFriendliness", feedbackRepository.getAverageStaffFriendliness());

        return summary;
    }

    // ADD THIS METHOD ↓
    @GetMapping("/negative-feedback")
    public List<Feedback> getNegativeFeedback() {
        return feedbackRepository.findBySentiment("NEGATIVE");
    }
}