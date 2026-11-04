package uo.ri.cws.domain;

import java.util.Objects;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * A substitution is a relation between an intervention and a spare part.
 * It represents the fact that a spare part has been used in an intervention.
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
	 * parameters.
	 * Only Intervention can create Substitutions.
	 */
	/* package */ Substitution(SparePart sparePart, Intervention intervention, int quantity) {
		// Validate input parameters
		// assignations
		// link the substitution with the intervention and spare part (atomiclink)
		throw new UnsupportedOperationException("Not implemented yet");
	}

	// ---------------------------- Association helpers
	// -----------------------------

	void _setSparePart(SparePart sparePart) {
		this.sparePart = sparePart;
	}

	void _setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}

	// ---------------------------- Business methods -----------------------------
	public Money getTotalAmount() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sparePart == null) ? 0 : sparePart.hashCode());
		result = prime * result + ((intervention == null) ? 0 : intervention.hashCode());
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
		Substitution other = (Substitution) obj;
		if (sparePart == null) {
			if (other.sparePart != null)
				return false;
		} else if (!sparePart.equals(other.sparePart))
			return false;
		if (intervention == null) {
			if (other.intervention != null)
				return false;
		} else if (!intervention.equals(other.intervention))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Substitution [price=" + price + ", quantity=" + quantity + "]";
	}

}
