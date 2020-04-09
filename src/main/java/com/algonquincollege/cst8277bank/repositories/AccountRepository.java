package com.algonquincollege.cst8277bank.repositories;

import com.algonquincollege.cst8277bank.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}
