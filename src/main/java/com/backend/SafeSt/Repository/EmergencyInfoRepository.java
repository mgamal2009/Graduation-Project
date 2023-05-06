package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.EmergencyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyInfoRepository extends JpaRepository<EmergencyInfo, Integer> {

}
