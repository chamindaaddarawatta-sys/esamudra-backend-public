package com.esamudra.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    // ✅ UPDATED: Proper relationship with Stay entity
    @ManyToOne
    @JoinColumn(name = "stay_id", referencedColumnName = "stayId")
    private Stay stay;

    @Column(nullable = true)
    private Integer overallSatisfaction;
    private Integer roomCleanliness;
    private Integer staffFriendliness;

    // NEW FIELDS ADDED
    @Column(name = "comfort_amenities")
    private Integer comfortAndAmenities;

    @Column(name = "food_beverages")
    private Integer foodAndBeverages;

    @Column(name = "checkin_checkout_experience")
    private Integer checkinCheckoutExperience;

    @Column(name = "value_for_money")
    private Integer valueForMoney;

    @Column(name = "facilities_rating")
    private Integer facilities;

    private String comments;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "nps_score")
    private Integer npsScore;

    private String sentiment;

    @Column(name = "date_submitted")
    private LocalDateTime dateSubmitted;

    @Column(name = "customer_id")
    private Long customerId;

    // Constructors
    public Feedback() {
        this.dateSubmitted = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }

    // ✅ UPDATED: Stay getter/setter
    public Stay getStay() { return stay; }
    public void setStay(Stay stay) { this.stay = stay; }

    public Integer getOverallSatisfaction() { return overallSatisfaction; }
    public void setOverallSatisfaction(Integer overallSatisfaction) { this.overallSatisfaction = overallSatisfaction; }

    public Integer getRoomCleanliness() { return roomCleanliness; }
    public void setRoomCleanliness(Integer roomCleanliness) { this.roomCleanliness = roomCleanliness; }

    public Integer getStaffFriendliness() { return staffFriendliness; }
    public void setStaffFriendliness(Integer staffFriendliness) { this.staffFriendliness = staffFriendliness; }

    // NEW GETTERS AND SETTERS
    public Integer getComfortAndAmenities() { return comfortAndAmenities; }
    public void setComfortAndAmenities(Integer comfortAndAmenities) { this.comfortAndAmenities = comfortAndAmenities; }

    public Integer getFoodAndBeverages() { return foodAndBeverages; }
    public void setFoodAndBeverages(Integer foodAndBeverages) { this.foodAndBeverages = foodAndBeverages; }

    public Integer getCheckinCheckoutExperience() { return checkinCheckoutExperience; }
    public void setCheckinCheckoutExperience(Integer checkinCheckoutExperience) { this.checkinCheckoutExperience = checkinCheckoutExperience; }

    public Integer getValueForMoney() { return valueForMoney; }
    public void setValueForMoney(Integer valueForMoney) { this.valueForMoney = valueForMoney; }

    public Integer getFacilities() { return facilities; }
    public void setFacilities(Integer facilities) { this.facilities = facilities; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }

    public Integer getNpsScore() { return npsScore; }
    public void setNpsScore(Integer npsScore) { this.npsScore = npsScore; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(LocalDateTime dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}