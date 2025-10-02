package com.esamudra.backend.service;

import com.esamudra.backend.model.Feedback;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SentimentAnalysisService {

    // Simple keyword-based sentiment analysis (Beginner-friendly)
    public String analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "NEUTRAL";
        }

        String lowerText = text.toLowerCase();

        // Positive keywords
        List<String> positiveWords = Arrays.asList(
                "good", "great", "excellent", "amazing", "wonderful", "fantastic",
                "nice", "perfect", "love", "happy", "satisfied", "awesome", "best"
        );

        // Negative keywords
        List<String> negativeWords = Arrays.asList(
                "bad", "terrible", "awful", "horrible", "disappointing", "poor",
                "worst", "hate", "angry", "frustrated", "disgusting", "dirty", "broken"
        );

        int positiveCount = 0;
        int negativeCount = 0;

        for (String word : positiveWords) {
            if (lowerText.contains(word)) positiveCount++;
        }

        for (String word : negativeWords) {
            if (lowerText.contains(word)) negativeCount++;
        }

        if (positiveCount > negativeCount) return "POSITIVE";
        if (negativeCount > positiveCount) return "NEGATIVE";
        return "NEUTRAL";
    }

    // Analyze feedback and auto-create complaints for negative sentiment
    public void processFeedbackSentiment(Feedback feedback) {
        String sentiment = analyzeSentiment(feedback.getComments());
        feedback.setSentiment(sentiment);

        // Auto-create complaint for negative feedback
        if ("NEGATIVE".equals(sentiment)) {
            // You'll need to inject ComplaintService here
            // complaintService.createComplaintFromFeedback(feedback);
        }
    }
}