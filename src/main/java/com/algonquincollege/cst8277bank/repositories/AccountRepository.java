package com.algonquincollege.cst8277bank.repositories;

import java.util.HashMap;
import java.util.Map;

import com.algonquincollege.cst8277bank.models.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountRepository {

	private Map<Long, Account> accounts;

	public AccountRepository() {
		accounts = new HashMap<>();
	}

	public Map<Long, Account> getAccounts() {
		return accounts;
	}

}