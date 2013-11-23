package com.iullui.domain.data;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="accounts")
public class Account {

	protected class Payment {
		Date timestamp;
		Money amount;
		
		public Payment(Money amount) {
			this.setAmount(amount);
			this.setTimestamp(new Date());
		}
		
		public Date getTimestamp() {
			return timestamp;
		}
		
		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}
		
		public Money getAmount() {
			return amount;
		}
		
		public void setAmount(Money amount) {
			this.amount = amount;
		}
		
	}
	
	@Id String id;
	@Indexed(name="idxUser") String userId;
	Money credit;
	Money topup;
	List<Payment> payments = new ArrayList<Payment>();
	
	public Account(String userId) {
		this.setUserId(userId);
		this.setCredit(new Money(0.0));
		this.getCredit().setScale(2, RoundingMode.HALF_UP);
	}

	public void addCredit(Money payment) {
		this.setCredit(new Money(this.getCredit().add(payment)));
		this.getPayments().add(new Payment(payment));
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Money getCredit() {
		return credit;
	}

	public void setCredit(Money credit) {
		this.credit = credit;
	}

	public Money getTopup() {
		return topup;
	}

	public void setTopup(Money topup) {
		this.topup = topup;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	
	
}
