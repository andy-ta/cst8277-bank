package controllers;

import models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.AccountService;

@RestController
public class AccountController {

	private AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/accounts")
	public Account saveAccount(@RequestBody Account account) throws Exception {
		return accountService.save(account);
	}

	@GetMapping("/accounts/{id}")
	public Account findAccount(@PathVariable Long id) {
		return accountService.find(id);
	}

	@PutMapping("/accounts/{id}")
	public Account updateAccount(@PathVariable Long id, @RequestBody Account account) throws Exception {
		if (id.equals(account.getId())) {
			return accountService.save(account);
		} else {
			throw new Exception("Path error. mismatch Account id");
		}
	}
	
	@DeleteMapping("/accounts/{id}")
	public void deleteAccount(@PathVariable Long id) {
		accountService.delete(id);
	}
	
	@PatchMapping("/accounts/{id}/deposit")
	public Account deposit(@PathVariable Long id,
							@RequestParam double amount) throws Exception {
		
		return accountService.deposit(id, amount);
		
	}
	
	@PatchMapping("/accounts/{id}/withdraw")
	public Account withdraw(@PathVariable Long id,
							@RequestParam double amount) throws Exception {
		
		return accountService.withdraw(id, amount);
	}
}
