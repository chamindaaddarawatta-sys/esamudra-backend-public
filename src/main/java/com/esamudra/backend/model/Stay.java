package com.esamudra.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stay")
public class Stay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stayId;

    @Column(name = "customer_id")
    private Long customerId; // FK to Customer

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "number_of_guests")
    private Integer numberOfGuests;

    @Column(name = "booking_reference")
    private String bookingReference;

    @Column(name = "room_type")
    private String roomType; // SINGLE, DOUBLE, SUITE, etc.

    @Column(name = "total_amount")
    private Double totalAmount;

    // Constructors
    public Stay() {}

    public Stay(Long customerId, LocalDate checkInDate, LocalDate checkOutDate,
                String roomNumber, Integer numberOfGuests, String bookingReference) {
        this.customerId = customerId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.numberOfGuests = numberOfGuests;
        this.bookingReference = bookingReference;
    }

    // Getters and Setters
    public Long getStayId() { return stayId; }
    public void setStayId(Long stayId) { this.stayId = stayId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}
