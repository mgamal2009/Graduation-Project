package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    /*@Query("""
            select t from Token t inner join Customer u on t.customer.id = u.id
            where u.id = :customerId and (t.expired = false or t.revoked = false)
            """)*/
    List<Token> findAllTokensByCustomer(Customer customer);
    Optional<Token> findByToken(String token);
}
