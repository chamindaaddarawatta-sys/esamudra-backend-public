package com.esamudra.backend.service;

import com.esamudra.backend.model.Stay;
import com.esamudra.backend.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StayService {

    @Autowired
    private StayRepository stayRepository;

    public Stay createStay(Stay stay) {
        return stayRepository.save(stay);
    }

    public List<Stay> getAllStays() {
        return stayRepository.findAll();
    }

    public Optional<Stay> getStayById(Long id) {
        return stayRepository.findById(id);
    }

    public List<Stay> getStaysByCustomer(Long customerId) {
        return stayRepository.findByCustomerId(customerId);
    }

    public Stay updateStay(Long id, Stay stayDetails) {
        Stay stay = stayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stay not found with id: " + id));

        stay.setCheckInDate(stayDetails.getCheckInDate());
        stay.setCheckOutDate(stayDetails.getCheckOutDate());
        stay.setRoomNumber(stayDetails.getRoomNumber());
        stay.setNumberOfGuests(stayDetails.getNumberOfGuests());
        stay.setRoomType(stayDetails.getRoomType());
        stay.setTotalAmount(stayDetails.getTotalAmount());

        return stayRepository.save(stay);
    }

    public void deleteStay(Long id) {
        stayRepository.deleteById(id);
    }
}