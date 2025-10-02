package com.esamudra.backend.repository;

import com.esamudra.backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // ✅ BASIC FILTERING
    List<Feedback> findBySentiment(String sentiment);

    List<Feedback> findByOverallSatisfaction(Integer rating);

    List<Feedback> findByRoomCleanliness(Integer rating);

    List<Feedback> findByStaffFriendliness(Integer rating);

    // ✅ COUNT METHODS FOR DASHBOARD
    Long countBySentiment(String sentiment);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.overallSatisfaction >= 4")
    Long countHighSatisfactionFeedback();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.overallSatisfaction <= 2")
    Long countLowSatisfactionFeedback();

    // ✅ AVERAGE RATINGS FOR ANALYTICS
    @Query("SELECT AVG(f.overallSatisfaction) FROM Feedback f WHERE f.overallSatisfaction IS NOT NULL")
    Double getAverageOverallSatisfaction();

    @Query("SELECT AVG(f.roomCleanliness) FROM Feedback f WHERE f.roomCleanliness IS NOT NULL")
    Double getAverageRoomCleanliness();

    @Query("SELECT AVG(f.staffFriendliness) FROM Feedback f WHERE f.staffFriendliness IS NOT NULL")
    Double getAverageStaffFriendliness();

    @Query("SELECT AVG(f.comfortAndAmenities) FROM Feedback f WHERE f.comfortAndAmenities IS NOT NULL")
    Double getAverageComfortAndAmenities();

    @Query("SELECT AVG(f.foodAndBeverages) FROM Feedback f WHERE f.foodAndBeverages IS NOT NULL")
    Double getAverageFoodAndBeverages();

    // ✅ DATE-BASED QUERIES
    List<Feedback> findByDateSubmittedAfter(LocalDateTime date);

    List<Feedback> findByDateSubmittedBefore(LocalDateTime date);

    @Query("SELECT f FROM Feedback f WHERE f.dateSubmitted BETWEEN :startDate AND :endDate")
    List<Feedback> findFeedbackByDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // ✅ TEXT SEARCH FOR COMMENTS/SUGGESTIONS
    @Query("SELECT f FROM Feedback f WHERE LOWER(f.comments) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(f.suggestions) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Feedback> searchFeedbackByKeywords(@Param("keyword") String keyword);

    // ✅ SENTIMENT TREND ANALYSIS
    @Query("SELECT DATE(f.dateSubmitted), f.sentiment, COUNT(f) FROM Feedback f " +
            "WHERE f.dateSubmitted >= :startDate GROUP BY DATE(f.dateSubmitted), f.sentiment " +
            "ORDER BY DATE(f.dateSubmitted) DESC")
    List<Object[]> getSentimentTrends(@Param("startDate") LocalDateTime startDate);

    // ✅ RATING DISTRIBUTION ANALYSIS
    @Query("SELECT f.overallSatisfaction, COUNT(f) FROM Feedback f " +
            "WHERE f.overallSatisfaction IS NOT NULL GROUP BY f.overallSatisfaction " +
            "ORDER BY f.overallSatisfaction DESC")
    List<Object[]> getRatingDistribution();

    // ✅ NPS (Net Promoter Score) CALCULATION
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.npsScore >= 9")
    Long countPromoters();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.npsScore <= 6")
    Long countDetractors();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.npsScore BETWEEN 7 AND 8")
    Long countPassives();

    // ✅ FEEDBACK WITH COMPLAINTS (for analysis)
    @Query("SELECT f FROM Feedback f WHERE f IN (SELECT c.feedback FROM Complaint c)")
    List<Feedback> findFeedbackWithComplaints();

    // ✅ RECENT FEEDBACK FOR DASHBOARD
    @Query("SELECT f FROM Feedback f ORDER BY f.dateSubmitted DESC LIMIT 10")
    List<Feedback> findRecentFeedback();

    // ✅ FEEDBACK BY STAY (if you have stay relationship)
    @Query("SELECT f FROM Feedback f WHERE f.stay.stayId = :stayId")
    List<Feedback> findByStayId(@Param("stayId") Long stayId);

    // ✅ FEEDBACK BY CUSTOMER
    List<Feedback> findByCustomerId(Long customerId);

    // ✅ MONTHLY STATISTICS FOR REPORTING
    @Query("SELECT YEAR(f.dateSubmitted), MONTH(f.dateSubmitted), " +
            "AVG(f.overallSatisfaction), COUNT(f), " +
            "SUM(CASE WHEN f.sentiment = 'POSITIVE' THEN 1 ELSE 0 END) " +
            "FROM Feedback f " +
            "WHERE f.dateSubmitted >= :startDate " +
            "GROUP BY YEAR(f.dateSubmitted), MONTH(f.dateSubmitted) " +
            "ORDER BY YEAR(f.dateSubmitted) DESC, MONTH(f.dateSubmitted) DESC")
    List<Object[]> getMonthlyStatistics(@Param("startDate") LocalDateTime startDate);
}