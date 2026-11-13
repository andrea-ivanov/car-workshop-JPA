package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * A substitution is a relation between an intervention and a spare part.
 * It represents the fact that a spare part has been used in an intervention.
 * 
 * Internal aggregate class of WorkOrder. Only Intervention can create
 * Substitutions (through WorkOrder).
 */
@Entity
@Table(name = "TSUBSTITUTIONS", uniqueConstraints = @UniqueConstraint(columnNames = { "INTERVENTION_ID",
		"SPAREPART_ID" }))
public class Substitution extends BaseEntity {
	// Natural attributes
	@AttributeOverride(name = "amount", column = @Column(name = "price", precision = 19, scale = 2))
	private Money price;
	private int quantity;

	// Accidental attributes
	@ManyToOne
	private SparePart sparePart;
	@ManyToOne
	private Intervention intervention;

	// ---------------------------- Constructors -----------------------------

	protected Substitution() {

	}

	/**
	 * Package-private constructor to create a new Substitution with the given
	 * parameters.
	 * Only Intervention can create Substitutions.
	 */
	/* package */ Substitution(SparePart sparePart, Intervention intervention, int quantity) {
		// Validate input parameters
		// assignations
		// link the substitution with the intervention and spare part (atomiclink)
		ArgumentChecks.isNotNull(sparePart, "Pieza de repuesto no válida");
		ArgumentChecks.isNotNull(intervention, "Intervención no válida");
		ArgumentChecks.isTrue(quantity > 0, "Cantidad no válida");
		this.quantity = quantity;
		this.price = sparePart.getPrice().roundToCents();
		Associations.PerformsSubstitutes.atomicLink(intervention, this, sparePart);
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
		return Money.calc(price).times(quantity).toMoneyRoundedToCents();
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
	public String toString() {
		return "Substitution [price=" + price + ", quantity=" + quantity + "]";
	}

}
