package uo.ri.cws.domain;

import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class SparePart extends BaseEntity {

	// Natural attributes
	private String code;
	private String description;
	private Money unitPrice;

	protected SparePart() {
	}

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
	public String toString() {
		return "SparePart [code=" + code + ", description=" + description + ", unitPrice=" + unitPrice + "]";
	}

}
