package com.algonquincollege.cst8277bank.controllers;

import com.algonquincollege.cst8277bank.TestUtil;
import com.algonquincollege.cst8277bank.models.Account;
import com.algonquincollege.cst8277bank.repositories.AccountRepository;
import com.algonquincollege.cst8277bank.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
// To be able to autowire a pre-configured MockMvc
@AutoConfigureMockMvc
class AccountControllerIntTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;

	private Account account;

	private static final String NAME = "Andy Ta";
	private static final String TYPE = "Chequing";
	private static final String UPDATED_NAME = "Andie Ta";
	private static final String UPDATED_TYPE = "Savings";

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		account = new Account(NAME, TYPE);
	}

	@Test
	void createAccount() throws Exception {
		int databaseSizeBeforeCreate = accountRepository.findAll().size();

		this.mockMvc.perform(post("/api/v1/accounts")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(this.account)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.balance").value(0.0))
				.andExpect(jsonPath("$.name").value(NAME))
				.andExpect(jsonPath("$.type").value(TYPE));

		List<Account> accounts = accountRepository.findAll();
		assertThat(accounts.size()).isEqualTo(databaseSizeBeforeCreate + 1);
	}

	@Test
	void findAccount() throws Exception {
		Account saved = accountService.save(account);

		mockMvc.perform(get("/api/v1/accounts/{id}", saved.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(saved.getId()))
				.andExpect(jsonPath("$.balance").value(saved.getBalance()))
				.andExpect(jsonPath("$.name").value(saved.getName()))
				.andExpect(jsonPath("$.type").value(saved.getType()));
	}

	@Test
	void findAll() throws Exception {
		Account saved = accountService.save(account);

		mockMvc.perform(get("/api/v1/accounts?sort=id,desc"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content.[*].id").value(hasItem(saved.getId().intValue())))
				.andExpect(jsonPath("$.content.[*].balance").value(hasItem(saved.getBalance())))
				.andExpect(jsonPath("$.content.[*].name").value(hasItem(saved.getName())))
				.andExpect(jsonPath("$.content.[*].type").value(hasItem(saved.getType())));
	}

	@Test
	void updateAccount() throws Exception {
		Account initial = accountService.save(account);

		int databaseSizeBeforeUpdate = accountRepository.findAll().size();

		Account updated = accountRepository.findById(initial.getId()).orElse(null);
		assertThat(updated).isNotNull();
		double balanceBeforeUpdate = updated.getBalance();
		// Disconnect from session so that the updates are not directly saved in db
		entityManager.detach(updated);
		updated.setName(UPDATED_NAME);
		updated.setType(UPDATED_TYPE);
		updated.setBalance(null); // Need wrapper Double to set to null

		mockMvc.perform(put("/api/v1/accounts/{id}", initial.getId())
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(updated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(initial.getId()))
				.andExpect(jsonPath("$.balance").value(balanceBeforeUpdate))
				.andExpect(jsonPath("$.name").value(updated.getName()))
				.andExpect(jsonPath("$.type").value(updated.getType()));

		List<Account> accounts = accountRepository.findAll();
		assertThat(accounts.size()).isEqualTo(databaseSizeBeforeUpdate);
		// It is also helpful to verify that the database has indeed changed
		Account databaseAccount = accountRepository.findById(initial.getId()).orElse(null);
		assertThat(databaseAccount).isNotNull();
		assertThat(databaseAccount.getBalance()).isEqualTo(balanceBeforeUpdate);
		assertThat(databaseAccount.getName()).isEqualTo(updated.getName());
		assertThat(databaseAccount.getType()).isEqualTo(updated.getType());
	}

	@Test
	public void deleteAccount() throws Exception {
		// Initialize the database
		Account saved = accountService.save(account);

		int databaseSizeBeforeDelete = accountRepository.findAll().size();

		// Get the account
		mockMvc.perform(delete("/api/v1/accounts/{id}", saved.getId()))
				.andExpect(status().isNoContent());

		// Validate the database is empty
		List<Account> accounts = accountRepository.findAll();
		assertThat(accounts.size()).isEqualTo(databaseSizeBeforeDelete - 1);
	}

	@Test
	void deposit() throws Exception {
		/* Need to use the service, because has logic to set balance to 0.
		   If I used the repository instead, we'd be persisting null as a balance to the database and get an error. */
		Account initial = accountService.save(account);

		double amt = 10.0;
		double expectedBalance = initial.getBalance() + amt;

		mockMvc.perform(patch("/api/v1/accounts/{id}/deposit?amount={amount}", initial.getId(), amt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(initial.getId()))
				.andExpect(jsonPath("$.balance").value(expectedBalance))
				.andExpect(jsonPath("$.name").value(initial.getName()))
				.andExpect(jsonPath("$.type").value(initial.getType()));

		// It is also helpful to verify that the database has indeed changed
		Account databaseAccount = accountRepository.findById(initial.getId()).orElse(null);
		assertThat(databaseAccount).isNotNull();
		assertThat(databaseAccount.getBalance()).isEqualTo(expectedBalance);
		assertThat(databaseAccount.getName()).isEqualTo(initial.getName());
		assertThat(databaseAccount.getType()).isEqualTo(initial.getType());
	}

	@Test
	void withdraw() throws Exception {
		// Use the repository directly to bypass balance verification and set an initial balance.
		account.setBalance(10.0);
		Account initial = accountRepository.saveAndFlush(account);

		double amt = 10.0;
		double expectedBalance = initial.getBalance() - amt;

		mockMvc.perform(patch("/api/v1/accounts/{id}/withdraw?amount={amount}", initial.getId(), amt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(initial.getId()))
				.andExpect(jsonPath("$.balance").value(expectedBalance))
				.andExpect(jsonPath("$.name").value(initial.getName()))
				.andExpect(jsonPath("$.type").value(initial.getType()));

		// It is also helpful to verify that the database has indeed changed
		Account databaseAccount = accountRepository.findById(initial.getId()).orElse(null);
		assertThat(databaseAccount).isNotNull();
		assertThat(databaseAccount.getBalance()).isEqualTo(expectedBalance);
		assertThat(databaseAccount.getName()).isEqualTo(initial.getName());
		assertThat(databaseAccount.getType()).isEqualTo(initial.getType());
	}
}
