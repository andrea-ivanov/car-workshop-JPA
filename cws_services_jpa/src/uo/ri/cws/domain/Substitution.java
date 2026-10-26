package uo.ri.cws.domain;

import java.util.Objects;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * A substitution is a relation between an intervention and a spare part. It
 * represents the fact that a spare part has been used in an intervention.
 * 
 * Internal aggregate class of WorkOrder. Only Intervention can create
 * Substitutions (through WorkOrder).
 */
public class Substitution {
	// Natural attributes
	private Money price;
	private int quantity;

	// Accidental attributes
	private SparePart sparePart;
	private Intervention intervention;

	// ---------------------------- Constructors -----------------------------
	/**
	 * Package-private constructor to create a new Substitution with the given
	 * parameters. Only Intervention can create Substitutions.
	 */
	/* package */ Substitution(SparePart sparePart, Intervention intervention, int quantity) {
		ArgumentChecks.isNotNull(sparePart, "Spare part cannot be null");
		ArgumentChecks.isNotNull(intervention, "Intervention cannot be null");
		ArgumentChecks.isTrue(quantity > 0, "Quantity must be positive");

		this.quantity = quantity;
		this.price = sparePart.getPrice();

		Associations.PerformsSubstitutes.atomicLink(intervention, this, sparePart);
	}

	// ---------------------------- Getters -----------------------------

	public Money getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public SparePart getSparePart() {
		return sparePart;
	}

	public Intervention getIntervention() {
		return intervention;
	}

	// ---------------------------- Association helpers -----------------------------

	void _setSparePart(SparePart sparePart) {
		this.sparePart = sparePart;
	}

	void _setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}

	// ---------------------------- Business methods -----------------------------
	public Money getTotalAmount() {
		return Money.calc(price).times(quantity).toMoneyRoundedToCents();
	}

	@Override
	public int hashCode() {
		return Objects.hash(intervention, sparePart);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Substitution other = (Substitution) obj;
		return Objects.equals(intervention, other.intervention) && Objects.equals(sparePart, other.sparePart);
	}

	@Override
	public String toString() {
		return "Substitution [sparePart=" + sparePart + ", quantity=" + quantity + ", price=" + price + "]";
	}

}
