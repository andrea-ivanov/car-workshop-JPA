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
		this(code, "no-description", Money.ZERO);
	}

	public SparePart(String code, String description, Money price) {
		ArgumentChecks.isNotBlank(code, "El codigo no puede estar en blanco");
		ArgumentChecks.isNotBlank(description, "La descripción no puede estar en blanco");
		ArgumentChecks.isNotNull(price, "El precio no puede ser nulo");
		ArgumentChecks.isTrue(price.isNonNegative());

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

	public Money getUnitPrice() {
		return unitPrice;
	}

	public Money getPrice() {
		return unitPrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
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
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SparePart [code=" + code + ", description=" + description + ", unitPrice=" + unitPrice + "]";
	}

}
