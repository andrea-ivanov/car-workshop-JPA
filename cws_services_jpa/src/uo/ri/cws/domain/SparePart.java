package uo.ri.cws.domain;

import java.util.Objects;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class SparePart {
	
	// Natural attributes
	private String code;
	private String description;
	private Money unitPrice;

	public SparePart(String code) {
		this( code, "no-description", Money.ZERO );
	}

	public SparePart(String code, String description, Money price) {
		// Argument checks
		// Round the price to cents
		ArgumentChecks.isNotNull(description);
		ArgumentChecks.isNotNull(code);
		ArgumentChecks.isTrue(price.greaterOrEqual(Money.ZERO), "Price per hour must be greater than or equal to zero");
		
		this.code = code;
		this.description = description;
		this.unitPrice = price.roundToCents();
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Money getPrice() {
		return unitPrice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SparePart other = (SparePart) obj;
		return Objects.equals(code, other.code);
	}
	
}

