package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
    ArrayList<Trip> findAllByCustomer_IdAndAndEnded(Integer customerId, boolean end);

    Optional<Trip> findByCustomer_IdAndId(Integer customerId, Integer tripId);

}
