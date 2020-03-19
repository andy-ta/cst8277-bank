package com.algonquincollege.cst8277bank.controllers;

import com.algonquincollege.cst8277bank.exceptions.ClientException;
import com.algonquincollege.cst8277bank.exceptions.NotFoundException;
import com.algonquincollege.cst8277bank.models.Account;
import com.algonquincollege.cst8277bank.services.AccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

	private AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@ApiOperation(value = "Find accounts", response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success")
	})
	@GetMapping("/accounts")
	public Page<Account> findAll(Pageable page) throws Exception {
		return accountService.findAll(page);
	}

	@ApiOperation(value = "Create an account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully created account"),
			@ApiResponse(code = 400, message = "Invalid input")
	})
	@PostMapping("/accounts")
	public ResponseEntity<Account> saveAccount(@RequestBody Account account) throws Exception {
		Account result = accountService.save(account);
		return ResponseEntity.created(new URI("/api/v1/accounts/" + result.getId())).body(result);
	}

	@ApiOperation(value = "Find an account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 404, message = "Account not found")
	})
	@GetMapping("/accounts/{id}")
	public ResponseEntity<Account> findAccount(@PathVariable Long id) {
		Account result = accountService.find(id);
		return ResponseEntity.of(Optional.of(result));
	}

	@ApiOperation(value = "Update an account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully updated account"),
			@ApiResponse(code = 400, message = "Invalid input")
	})
	@PutMapping("/accounts/{id}")
	public ResponseEntity<Account> updateAccount(@PathVariable Long id,
												 @RequestBody Account account) throws Exception {
		if (id.equals(account.getId())) {
			return ResponseEntity.ok(accountService.save(account));
		} else {
			throw new ClientException("The id in the path does not much the id in the body.");
		}
	}

	@ApiOperation(value = "Delete an account")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully deleted account"),
			@ApiResponse(code = 404, message = "Account not found")
	})
	@DeleteMapping("/accounts/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) throws NotFoundException {
		accountService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value = "Deposit into an account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully deposited into account"),
			@ApiResponse(code = 400, message = "Invalid input")
	})
	@PatchMapping("/accounts/{id}/deposit")
	public ResponseEntity<Account> deposit(@PathVariable Long id,
										@RequestParam double amount) throws Exception {
		return ResponseEntity.ok(accountService.deposit(id, amount));
		
	}

	@ApiOperation(value = "Withdraw from an account", response = Account.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully withdrawn into account"),
			@ApiResponse(code = 400, message = "Invalid input")
	})
	@PatchMapping("/accounts/{id}/withdraw")
	public ResponseEntity<Account> withdraw(@PathVariable Long id,
											@RequestParam double amount) throws Exception {
		return ResponseEntity.ok(accountService.deposit(id, amount));
	}
}
