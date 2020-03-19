package com.algonquincollege.cst8277bank.repositories;

import com.algonquincollege.cst8277bank.models.Account;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class AccountRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    public List<Account> findAll() {
        Query query = entityManager.createNativeQuery("SELECT * FROM ACCOUNT;", Account.class);
        return query.getResultList();
    }
}
