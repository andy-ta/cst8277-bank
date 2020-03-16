package services;

import models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.AccountRepository;

import java.util.Map;

@Service
public class AccountService {

	private AccountRepository accountRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Account save(Account account) throws Exception {
		Map<Long, Account> accounts = accountRepository.getAccounts();
		if (account.getId() == null) {
			Long id = (long) (accounts.size() + 1);
			account.setId(id);
			if (account.getBalance() > 0) {
				throw new Exception("Error: Cannot create account with balance greater than zero");
			}
			accounts.put(id, account);
		} else {
			if (accountRepository.getAccounts().get(account.getId()).getBalance() == account.getBalance()) {
				accounts.put(account.getId(), account);

			} else {
				throw new Exception("Error: Cannot modify your own balance.");
			}
		}

		return accounts.get(account.getId());
	}

	public Account find(Long id) {
		return accountRepository.getAccounts().get(id);
	}

	public void delete(Long id) {
		accountRepository.getAccounts().remove(id);
	}

	public Account deposit(Long id, double amount) throws Exception {
		Account account = accountRepository.getAccounts().get(id);
		if (amount > 0) {
			account.setBalance(account.getBalance() + amount);
			
		} else {
			throw new Exception("Error: amount must be greater then zero.");
		}

		return save(account);
	}
	
	public Account withdraw(Long id, double amount) throws Exception {
		Account account = accountRepository.getAccounts().get(id);

		if(amount > 0 && account.getBalance() > amount) {
			account.setBalance(account.getBalance() - amount);
		}else {
			throw new Exception("Error: not enough money in account to withdraw.");
		}
		
		return save(account);
	}

}
