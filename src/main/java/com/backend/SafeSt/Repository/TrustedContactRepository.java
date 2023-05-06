package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Trip;
import com.backend.SafeSt.Entity.TrustedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrustedContactRepository extends JpaRepository<TrustedContact, Integer> {
    void deleteByTrusted_Id(Integer id);

}
