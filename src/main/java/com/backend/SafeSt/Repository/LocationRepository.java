package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByLongitudeAndLatitude(double longitude,double latitude);
}
