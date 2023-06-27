package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.TrustedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface TrustedContactRepository extends JpaRepository<TrustedContact, Integer> {
    void deleteTrustedContactByCustomer_IdAndTrusted_Id(Integer customerId, Integer trustedId);

    Optional<TrustedContact> findByCustomer_IdAndTrusted_Id(Integer customerId, Integer trustedId);
    ArrayList<TrustedContact> findAllByCustomer_Id(Integer customerId);
}
