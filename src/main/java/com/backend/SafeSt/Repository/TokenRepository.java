package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {

    List<Token> findAllTokensByCustomer(Customer customer);
    Optional<Token> findByCustomer_Id(Integer id);
    Optional<Token> findByToken(String token);
}
