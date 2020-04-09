package com.algonquincollege.cst8277bank.services;

import com.algonquincollege.cst8277bank.models.Account;
import com.algonquincollege.cst8277bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AccountDetailsService implements UserDetailsService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account user = accountRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException(s);
        }
        return new User(user.getUsername(),
                        user.getPassword(),
                true,
                true,
                true,
                true,
                new HashSet<>());
    }
}
