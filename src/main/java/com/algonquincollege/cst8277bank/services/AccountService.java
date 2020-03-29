package com.algonquincollege.cst8277bank.services;

import com.algonquincollege.cst8277bank.exceptions.ClientException;
import com.algonquincollege.cst8277bank.exceptions.NotFoundException;
import com.algonquincollege.cst8277bank.models.Account;
import com.algonquincollege.cst8277bank.repositories.AccountRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.algonquincollege.cst8277bank.repositories.AccountRepository;

import java.util.List;
import java.util.function.Supplier;

@Service
public class AccountService {

	private AccountRepository accountRepository;
	private AccountRepositoryCustom accountRepositoryCustom;

	@Autowired
	public AccountService(AccountRepository accountRepository,
						  AccountRepositoryCustom accountRepositoryCustom) {
		this.accountRepository = accountRepository;
		this.accountRepositoryCustom = accountRepositoryCustom;
	}

	public Account save(Account account) throws Exception {
		if (account.getId() == null) {
			if (account.getBalance() != null && account.getBalance() > 0) {
				throw new ClientException("Cannot create account with balance greater than zero");
			}
			account.setBalance(0.0);
			return accountRepository.save(account);
		} else {
			Account inDatabase = accountRepository.findById(account.getId()).orElseThrow(accountNotFound());
			if (account.getBalance() != null && !account.getBalance().equals(inDatabase.getBalance())) {
					throw new ClientException("Cannot modify your own balance.");
			} else {
				inDatabase.setName(account.getName());
				inDatabase.setType(account.getType());
				return accountRepository.save(inDatabase);
			}
		}
	}

	public Account find(Long id) {
		return accountRepository.findById(id).orElse(null);
	}

	public Page<Account> findAll(Pageable page) {
		return accountRepository.findAll(page);
	}

	public List<Account> findAllCustom() {
		return accountRepositoryCustom.findAll();
	}

	public void delete(Long id) throws NotFoundException {
		accountRepository.delete(
				accountRepository.findById(id)
						.orElseThrow(accountNotFound()));
	}

	public Account deposit(Long id, double amount) throws Exception {
		Account account = accountRepository.findById(id).orElseThrow(accountNotFound());
		if (amount > 0) {
			account.setBalance(account.getBalance() + amount);
		} else {
			throw new ClientException("Amount must be greater than zero.");
		}

		return accountRepository.save(account);
	}
	
	public Account withdraw(Long id, double amount) throws Exception {
		Account account = accountRepository.findById(id).orElseThrow(accountNotFound());

		if (amount > 0 && account.getBalance() >= amount) {
			account.setBalance(account.getBalance() - amount);
		} else {
			throw new ClientException("Not enough money in account to withdraw.");
		}
		
		return accountRepository.save(account);
	}

	private Supplier<NotFoundException> accountNotFound() {
		return () -> new NotFoundException("The account was not found.");
	}
}
