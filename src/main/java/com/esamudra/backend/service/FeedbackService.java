package com.esamudra.backend.service;

import com.esamudra.backend.service.NotificationService;
import com.esamudra.backend.model.Feedback;
import com.esamudra.backend.model.Complaint;
import com.esamudra.backend.model.Ticket;
import com.esamudra.backend.repository.FeedbackRepository;
import com.esamudra.backend.repository.ComplaintRepository;
import com.esamudra.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private NotificationService notificationService;

    // ✅ 1. GET ALL FEEDBACK
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // ✅ 2. GET FEEDBACK BY ID
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    // ✅ 3. SUBMIT/SAVE FEEDBACK (Updated with Ticket Creation and Debug Logging)
    @Transactional
    public Feedback saveFeedback(Feedback feedback) {
        // Auto-detect sentiment from comments
        String detectedSentiment = analyzeSentiment(feedback.getComments());
        feedback.setSentiment(detectedSentiment);

        // Set current timestamp if not already set
        if (feedback.getDateSubmitted() == null) {
            feedback.setDateSubmitted(LocalDateTime.now());
        }

        // Save feedback first
        Feedback savedFeedback = feedbackRepository.save(feedback);

        // AUTO-COMPLAINT CREATION FOR NEGATIVE FEEDBACK
        if ("NEGATIVE".equals(detectedSentiment)) {
            System.out.println("=== DEBUG: NEGATIVE feedback detected ===");

            notificationService.incrementNegativeFeedback();

            // Create automatic complaint with category detection
            Complaint complaint = new Complaint();
            complaint.setFeedback(savedFeedback);
            complaint.setDescription("Auto-generated from negative feedback: " +
                    (feedback.getComments() != null ? feedback.getComments() : "No comments"));

            // Auto-detect complaint category
            complaint.setCategory(detectComplaintCategory(feedback.getComments()));

            complaint.setStatus("RECEIVED");
            complaint.setCreationDate(LocalDateTime.now());

            Complaint savedComplaint = complaintRepository.save(complaint);
            System.out.println("=== DEBUG: Complaint created with ID: " + savedComplaint.getComplaintId());

            // ✅ NEW: AUTO-CREATE TICKET FOR THE COMPLAINT - URGENT FIX
            try {
                System.out.println("=== DEBUG: Starting AUTOMATIC ticket creation for complaint ID: " + savedComplaint.getComplaintId());

                // Create ticket WITHOUT staff (staff will be assigned later)
                String priority = determinePriority(savedComplaint.getCategory(), savedComplaint.getDescription());

                // Use the constructor that doesn't require staff
                Ticket ticket = new Ticket(savedComplaint, priority);
                ticket.setStaff(null); // Explicitly set to null

                System.out.println("=== DEBUG: Ticket created - Complaint: " + savedComplaint.getComplaintId() + ", Priority: " + priority);

                // Save the ticket
                Ticket savedTicket = ticketRepository.save(ticket);
                System.out.println("=== DEBUG: SUCCESS - Ticket saved with ID: " + savedTicket.getTicketId());

                // Update the complaint with ticket reference
                savedComplaint.setTicket(savedTicket);
                complaintRepository.save(savedComplaint);
                System.out.println("=== DEBUG: Complaint updated with ticket reference");

            } catch (Exception e) {
                System.out.println("=== DEBUG: CRITICAL ERROR in ticket creation: " + e.getMessage());
                e.printStackTrace();
                // Don't throw exception - we want complaint to be created even if ticket fails
            }
            // Create notification for the new complaint
            notificationService.createNotification(
                    "New Auto-Generated Complaint",
                    "Category: " + complaint.getCategory() + " - " + complaint.getDescription(),
                    "ALERT"
            );
        }

        return savedFeedback;
    }

    // ✅ 4. UPDATE FEEDBACK
    public Feedback updateFeedback(Long id, Feedback feedbackDetails) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        // Update fields if provided
        if (feedbackDetails.getComments() != null) {
            feedback.setComments(feedbackDetails.getComments());
            // Re-analyze sentiment if comments changed
            feedback.setSentiment(analyzeSentiment(feedbackDetails.getComments()));
        }

        if (feedbackDetails.getOverallSatisfaction() != null) {
            feedback.setOverallSatisfaction(feedbackDetails.getOverallSatisfaction());
        }

        if (feedbackDetails.getRoomCleanliness() != null) {
            feedback.setRoomCleanliness(feedbackDetails.getRoomCleanliness());
        }

        if (feedbackDetails.getStaffFriendliness() != null) {
            feedback.setStaffFriendliness(feedbackDetails.getStaffFriendliness());
        }

        // Update additional fields if they exist in your Feedback entity
        if (feedbackDetails.getComfortAndAmenities() != null) {
            feedback.setComfortAndAmenities(feedbackDetails.getComfortAndAmenities());
        }

        if (feedbackDetails.getFoodAndBeverages() != null) {
            feedback.setFoodAndBeverages(feedbackDetails.getFoodAndBeverages());
        }

        if (feedbackDetails.getNpsScore() != null) {
            feedback.setNpsScore(feedbackDetails.getNpsScore());
        }

        return feedbackRepository.save(feedback);
    }

    // ✅ 5. DELETE FEEDBACK
    public void deleteFeedback(Long id) {
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
        } else {
            throw new RuntimeException("Feedback not found with id: " + id);
        }
    }

    // ✅ 6. GET FEEDBACK BY SENTIMENT
    public List<Feedback> getFeedbackBySentiment(String sentiment) {
        return feedbackRepository.findBySentiment(sentiment);
    }

    // ✅ 7. SEARCH FEEDBACK BY KEYWORDS
    public List<Feedback> searchFeedbackByKeywords(String keyword) {
        return feedbackRepository.searchFeedbackByKeywords(keyword);
    }

    // ✅ 8. GET NEGATIVE FEEDBACK
    public List<Feedback> getNegativeFeedback() {
        return feedbackRepository.findBySentiment("NEGATIVE");
    }

    // ✅ 9. GET RECENT FEEDBACK
    public List<Feedback> getRecentFeedback(int limit) {
        return feedbackRepository.findAll().stream()
                .sorted((f1, f2) -> f2.getDateSubmitted().compareTo(f1.getDateSubmitted()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ✅ 10. GET FEEDBACK BY DATE RANGE
    public List<Feedback> getFeedbackByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return feedbackRepository.findFeedbackByDateRange(startDate, endDate);
    }

    // ✅ 11. GET RATING AVERAGES
    public Map<String, Double> getRatingAverages() {
        Map<String, Double> averages = new HashMap<>();
        averages.put("overallSatisfaction", feedbackRepository.getAverageOverallSatisfaction());
        averages.put("roomCleanliness", feedbackRepository.getAverageRoomCleanliness());
        averages.put("staffFriendliness", feedbackRepository.getAverageStaffFriendliness());

        // Add additional averages if available in your repository
        if (feedbackRepository.getAverageComfortAndAmenities() != null) {
            averages.put("comfortAndAmenities", feedbackRepository.getAverageComfortAndAmenities());
        }

        if (feedbackRepository.getAverageFoodAndBeverages() != null) {
            averages.put("foodAndBeverages", feedbackRepository.getAverageFoodAndBeverages());
        }

        return averages;
    }

    // ✅ 12. GET SENTIMENT TRENDS
    public List<Object[]> getSentimentTrends(LocalDateTime startDate) {
        return feedbackRepository.getSentimentTrends(startDate);
    }

    // ✅ 13. CALCULATE NPS (Net Promoter Score)
    public Map<String, Object> calculateNPS() {
        Long promoters = feedbackRepository.countPromoters();
        Long detractors = feedbackRepository.countDetractors();
        Long totalResponses = promoters + detractors + feedbackRepository.countPassives();

        double npsScore = totalResponses > 0 ?
                ((double) (promoters - detractors) / totalResponses) * 100 : 0;

        Map<String, Object> npsData = new HashMap<>();
        npsData.put("npsScore", Math.round(npsScore * 100.0) / 100.0);
        npsData.put("promoters", promoters);
        npsData.put("detractors", detractors);
        npsData.put("passives", feedbackRepository.countPassives());
        npsData.put("totalResponses", totalResponses);

        return npsData;
    }

    // ✅ 14. GET FEEDBACK WITH COMPLAINTS
    public List<Feedback> getFeedbackWithComplaints() {
        return feedbackRepository.findFeedbackWithComplaints();
    }

    // ✅ 15. GET FEEDBACK BY CUSTOMER ID
    public List<Feedback> getFeedbackByCustomerId(Long customerId) {
        return feedbackRepository.findByCustomerId(customerId);
    }

    // ✅ 16. GET MONTHLY STATISTICS
    public List<Object[]> getMonthlyStatistics(LocalDateTime startDate) {
        return feedbackRepository.getMonthlyStatistics(startDate);
    }

    // ✅ 17. GET RATING DISTRIBUTION
    public List<Object[]> getRatingDistribution() {
        return feedbackRepository.getRatingDistribution();
    }

    // ✅ 18. GET SATISFACTION COUNTS
    public Map<String, Long> getSatisfactionCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("highSatisfaction", feedbackRepository.countHighSatisfactionFeedback());
        counts.put("lowSatisfaction", feedbackRepository.countLowSatisfactionFeedback());
        return counts;
    }

    // ✅ 19. BULK SAVE FEEDBACK
    @Transactional
    public List<Feedback> saveAllFeedback(List<Feedback> feedbackList) {
        List<Feedback> savedFeedbacks = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            savedFeedbacks.add(saveFeedback(feedback));
        }
        return savedFeedbacks;
    }

    // ✅ 20. GET FEEDBACK STATISTICS (Enhanced)
    public Map<String, Object> getFeedbackStatistics() {
        List<Feedback> allFeedback = feedbackRepository.findAll();

        Map<String, Object> stats = new HashMap<>();

        // Basic counts
        stats.put("totalFeedback", allFeedback.size());

        // Sentiment distribution
        Map<String, Long> sentimentDist = new HashMap<>();
        sentimentDist.put("POSITIVE", allFeedback.stream().filter(f -> "POSITIVE".equals(f.getSentiment())).count());
        sentimentDist.put("NEUTRAL", allFeedback.stream().filter(f -> "NEUTRAL".equals(f.getSentiment())).count());
        sentimentDist.put("NEGATIVE", allFeedback.stream().filter(f -> "NEGATIVE".equals(f.getSentiment())).count());
        stats.put("sentimentDistribution", sentimentDist);

        // Average ratings
        stats.put("avgOverallSatisfaction", allFeedback.stream()
                .filter(f -> f.getOverallSatisfaction() != null)
                .mapToInt(Feedback::getOverallSatisfaction)
                .average().orElse(0.0));

        stats.put("avgRoomCleanliness", allFeedback.stream()
                .filter(f -> f.getRoomCleanliness() != null)
                .mapToInt(Feedback::getRoomCleanliness)
                .average().orElse(0.0));

        stats.put("avgStaffFriendliness", allFeedback.stream()
                .filter(f -> f.getStaffFriendliness() != null)
                .mapToInt(Feedback::getStaffFriendliness)
                .average().orElse(0.0));

        // Additional statistics
        stats.put("negativeFeedbackCount", sentimentDist.get("NEGATIVE"));
        stats.put("highSatisfactionCount", feedbackRepository.countHighSatisfactionFeedback());
        stats.put("lowSatisfactionCount", feedbackRepository.countLowSatisfactionFeedback());

        return stats;
    }

    // ✅ NEW HELPER METHOD: Determine ticket priority
    private String determinePriority(String category, String description) {
        if (description == null) return "MEDIUM";

        String lowerDesc = description.toLowerCase();

        // High priority for urgent keywords
        if (lowerDesc.contains("urgent") || lowerDesc.contains("emergency") ||
                lowerDesc.contains("immediate") || lowerDesc.contains("critical")) {
            return "HIGH";
        }

        // Medium priority for service-related issues
        if ("SERVICE".equals(category) || "BILLING".equals(category)) {
            return "MEDIUM";
        }

        // Low priority for facility issues that can wait
        if ("FACILITIES".equals(category) || "OTHER".equals(category)) {
            return "LOW";
        }

        return "MEDIUM"; // Default
    }

    // ✅ PRIVATE HELPER METHODS (Keep your existing ones)

    private String analyzeSentiment(String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            return "NEUTRAL";
        }

        String lowerComments = comments.toLowerCase();

        int positiveScore = 0;
        int negativeScore = 0;

        String[] positiveWords = {"excellent", "great", "awesome", "wonderful", "amazing",
                "good", "perfect", "love", "enjoy", "happy", "satisfied",
                "fantastic", "brilliant", "outstanding", "pleasant"};

        String[] negativeWords = {"poor", "bad", "terrible", "horrible", "awful",
                "disappoint", "hate", "worst", "not good", "never again",
                "disgusting", "unacceptable", "frustrated", "angry", "broken"};

        for (String word : positiveWords) {
            if (lowerComments.contains(word)) positiveScore++;
        }

        for (String word : negativeWords) {
            if (lowerComments.contains(word)) negativeScore++;
        }

        if (positiveScore > negativeScore) return "POSITIVE";
        if (negativeScore > positiveScore) return "NEGATIVE";
        return "NEUTRAL";
    }

    private String detectComplaintCategory(String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            return "OTHER";
        }

        String lowerComments = comments.toLowerCase();

        if (lowerComments.contains("room") || lowerComments.contains("clean") ||
                lowerComments.contains("bed") || lowerComments.contains("toilet") ||
                lowerComments.contains("bathroom") || lowerComments.contains("ac") ||
                lowerComments.contains("air conditioner") || lowerComments.contains("linen") ||
                lowerComments.contains("towel") || lowerComments.contains("housekeeping")) {
            return "ROOM";
        } else if (lowerComments.contains("food") || lowerComments.contains("restaurant") ||
                lowerComments.contains("meal") || lowerComments.contains("breakfast") ||
                lowerComments.contains("dinner") || lowerComments.contains("lunch") ||
                lowerComments.contains("taste") || lowerComments.contains("menu") ||
                lowerComments.contains("buffet") || lowerComments.contains("cuisine") ||
                lowerComments.contains("restaurant") || lowerComments.contains("bar")) {
            return "FOOD";
        } else if (lowerComments.contains("staff") || lowerComments.contains("service") ||
                lowerComments.contains("rude") || lowerComments.contains("friendly") ||
                lowerComments.contains("helpful") || lowerComments.contains("attitude") ||
                lowerComments.contains("reception") || lowerComments.contains("manager") ||
                lowerComments.contains("waiter") || lowerComments.contains("front desk") ||
                lowerComments.contains("unprofessional") || lowerComments.contains("ignored")) {
            return "SERVICE";
        } else if (lowerComments.contains("facility") || lowerComments.contains("pool") ||
                lowerComments.contains("gym") || lowerComments.contains("spa") ||
                lowerComments.contains("wi-fi") || lowerComments.contains("wifi") ||
                lowerComments.contains("internet") || lowerComments.contains("parking") ||
                lowerComments.contains("elevator") || lowerComments.contains("lift") ||
                lowerComments.contains("swimming") || lowerComments.contains("maintenance") ||
                lowerComments.contains("equipment") || lowerComments.contains("lobby")) {
            return "FACILITIES";
        } else if (lowerComments.contains("bill") || lowerComments.contains("price") ||
                lowerComments.contains("charge") || lowerComments.contains("payment") ||
                lowerComments.contains("cost") || lowerComments.contains("expensive") ||
                lowerComments.contains("overcharge") || lowerComments.contains("invoice") ||
                lowerComments.contains("rate") || lowerComments.contains("fee") ||
                lowerComments.contains("billing") || lowerComments.contains("refund")) {
            return "BILLING";
        } else {
            return "OTHER";
        }
    }
}