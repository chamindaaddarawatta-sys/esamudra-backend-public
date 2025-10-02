package com.esamudra.backend.controller;

import com.esamudra.backend.model.Feedback;
import com.esamudra.backend.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:3000") // ✅ ADD THIS FOR REACT FRONTEND
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // ✅ 1. SUBMIT NEW FEEDBACK (Existing - Enhanced)
    @PostMapping
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        return feedbackService.saveFeedback(feedback);
    }

    // ✅ 2. GET ALL FEEDBACK
    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback(); // You'll need to add this method to FeedbackService
    }

    // ✅ 3. GET FEEDBACK BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 4. UPDATE FEEDBACK
    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedbackDetails) {
        try {
            Feedback updatedFeedback = feedbackService.updateFeedback(id, feedbackDetails);
            return ResponseEntity.ok(updatedFeedback);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 5. DELETE FEEDBACK
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 6. FILTER BY SENTIMENT
    @GetMapping("/sentiment/{sentiment}")
    public List<Feedback> getFeedbackBySentiment(@PathVariable String sentiment) {
        return feedbackService.getFeedbackBySentiment(sentiment);
    }

    // ✅ 7. SEARCH FEEDBACK BY KEYWORDS
    @GetMapping("/search")
    public List<Feedback> searchFeedback(@RequestParam String keyword) {
        return feedbackService.searchFeedbackByKeywords(keyword);
    }

    // ✅ 8. DASHBOARD STATISTICS
    @GetMapping("/stats/dashboard")
    public Map<String, Object> getDashboardStats() {
        return feedbackService.getFeedbackStatistics();
    }

    // ✅ 9. GET NEGATIVE FEEDBACK (For Complaints Dashboard)
    @GetMapping("/negative")
    public List<Feedback> getNegativeFeedback() {
        return feedbackService.getNegativeFeedback();
    }

    // ✅ 10. GET RECENT FEEDBACK
    @GetMapping("/recent")
    public List<Feedback> getRecentFeedback(@RequestParam(defaultValue = "10") int limit) {
        return feedbackService.getRecentFeedback(limit);
    }

    // ✅ 11. FEEDBACK BY DATE RANGE
    @GetMapping("/date-range")
    public List<Feedback> getFeedbackByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return feedbackService.getFeedbackByDateRange(start, end);
    }

    // ✅ 12. RATING STATISTICS
    @GetMapping("/stats/ratings")
    public Map<String, Double> getRatingAverages() {
        return feedbackService.getRatingAverages();
    }

    // ✅ 13. SENTIMENT TRENDS (Last 30 days)
    @GetMapping("/stats/trends")
    public List<Object[]> getSentimentTrends(@RequestParam(defaultValue = "30") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return feedbackService.getSentimentTrends(startDate);
    }

    // ✅ 14. NPS CALCULATION
    @GetMapping("/stats/nps")
    public Map<String, Object> calculateNPS() {
        return feedbackService.calculateNPS();
    }

    // ✅ 15. FEEDBACK WITH COMPLAINTS
    @GetMapping("/with-complaints")
    public List<Feedback> getFeedbackWithComplaints() {
        return feedbackService.getFeedbackWithComplaints();
    }

    // ✅ 16. FEEDBACK BY CUSTOMER
    @GetMapping("/customer/{customerId}")
    public List<Feedback> getFeedbackByCustomer(@PathVariable Long customerId) {
        return feedbackService.getFeedbackByCustomerId(customerId);
    }

    // ✅ 17. MONTHLY REPORT
    @GetMapping("/stats/monthly")
    public List<Object[]> getMonthlyReport(@RequestParam int months) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        return feedbackService.getMonthlyStatistics(startDate);
    }

    // ✅ 18. RATING DISTRIBUTION
    @GetMapping("/stats/distribution")
    public List<Object[]> getRatingDistribution() {
        return feedbackService.getRatingDistribution();
    }

    // ✅ 19. HIGH/LOW SATISFACTION COUNTS
    @GetMapping("/stats/satisfaction")
    public Map<String, Long> getSatisfactionCounts() {
        return feedbackService.getSatisfactionCounts();
    }

    // ✅ 20. BULK OPERATIONS (Advanced - for admin)
    @PostMapping("/bulk")
    public ResponseEntity<String> createBulkFeedback(@RequestBody List<Feedback> feedbackList) {
        try {
            feedbackService.saveAllFeedback(feedbackList);
            return ResponseEntity.ok("Successfully created " + feedbackList.size() + " feedback entries");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating bulk feedback: " + e.getMessage());
        }
    }
}