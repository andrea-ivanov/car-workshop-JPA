package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TSPAREPARTS")
public class SparePart extends BaseEntity {

	// Natural attributes
	@Column(unique = true)
	private String code;
	private String description;
	@AttributeOverride(name = "amount", column = @Column(name = "PRICE", precision = 19, scale = 2))
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
