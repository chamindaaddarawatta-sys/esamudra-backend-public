package com.esamudra.backend.service;

import com.esamudra.backend.model.Feedback;
import com.esamudra.backend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Map<String, Object> getDashboardSummary() {
        List<Feedback> allFeedback = feedbackRepository.findAll();

        Map<String, Object> summary = new HashMap<>();

        // Basic counts
        summary.put("totalFeedback", allFeedback.size());

        // Sentiment distribution
        Map<String, Long> sentimentDist = new HashMap<>();
        sentimentDist.put("POSITIVE", allFeedback.stream().filter(f -> "POSITIVE".equals(f.getSentiment())).count());
        sentimentDist.put("NEUTRAL", allFeedback.stream().filter(f -> "NEUTRAL".equals(f.getSentiment())).count());
        sentimentDist.put("NEGATIVE", allFeedback.stream().filter(f -> "NEGATIVE".equals(f.getSentiment())).count());
        summary.put("sentimentDistribution", sentimentDist);

        // Average ratings
        summary.put("avgOverallSatisfaction", allFeedback.stream()
                .filter(f -> f.getOverallSatisfaction() != null)
                .mapToInt(Feedback::getOverallSatisfaction)
                .average().orElse(0.0));

        summary.put("avgRoomCleanliness", allFeedback.stream()
                .filter(f -> f.getRoomCleanliness() != null)
                .mapToInt(Feedback::getRoomCleanliness)
                .average().orElse(0.0));

        summary.put("avgStaffFriendliness", allFeedback.stream()
                .filter(f -> f.getStaffFriendliness() != null)
                .mapToInt(Feedback::getStaffFriendliness)
                .average().orElse(0.0));

        return summary;
    }
}
