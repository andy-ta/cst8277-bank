package com.algonquincollege.cst8277bank.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Account {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@NotNull
	private Double balance;
	private String type;

	public Account() {
	}

	public Account(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public Account(Account account) {
		this.id = account.getId();
		this.name = account.getName();
		this.balance = account.getBalance();
		this.type = account.getType();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
