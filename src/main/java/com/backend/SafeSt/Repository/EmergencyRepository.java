package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Integer> {

}
