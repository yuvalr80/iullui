package com.iullui.domain.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money extends BigDecimal {

	static final long serialVersionUID = 1L;
	
	public final String currency = "USD";	
	
	public Money(Double amount) {
		super(amount);
		this.setScale(2, RoundingMode.HALF_UP);
		if (amount < 0) throw new RuntimeException("Amount can't be negative");
	}
	
	public Money(BigDecimal amount) {
		this(amount.doubleValue());
	}
	
	public Money(String amount) {
		this(new Double(amount));
	}
	
	public String getCurrency() { 
		return this.currency;
	}
	
}
