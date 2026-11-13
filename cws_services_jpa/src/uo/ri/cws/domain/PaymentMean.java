package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TPAYMENTMEANS")
public abstract class PaymentMean extends BaseEntity {

	// Natural attributes
	@AttributeOverride(name = "amount", column = @Column(name = "accumulated", precision = 19, scale = 2))
	private Money accumulated = Money.ZERO; // cents

	// Accidental attributes
	@ManyToOne
	private Client client;

	// ---- Association helpers ----
	protected PaymentMean() {

	}

	void _setClient(Client client) {
		this.client = client;
	}

	// ---- Business methods ----

	public abstract boolean canBeCharged(Money amount);

	/**
	 * Increments the accumulated amount of the payment mean by the given amount.
	 * package-private Only called from Charge constructor.
	 * 
	 * @param amount
	 */
	/* package */ void charge(Money amount) {
		// Normalize the amount to cents
		// check if it can be charged
		// increment the accumulated amount
		Money normalized = normalizePositive(amount);
		StateChecks.isTrue(canBeCharged(normalized), "El medio de pago no admite el cobro");
		this.accumulated = Money.calc(this.accumulated).add(normalized).toMoneyRoundedToCents();
	}

	/**
	 * Decrements the accumulated amount of the payment mean by the given amount.
	 * package-private Only called from Charge.rewind().
	 * 
	 * @param amount
	 */
	/* package */ void rewind(Money amount) {
		Money normalized = normalizePositive(amount);
		StateChecks.isTrue(accumulated.greaterOrEqual(normalized), "No se puede devolver más de lo acumulado");
		this.accumulated = Money.calc(this.accumulated).subtract(normalized).toMoneyRoundedToCents();
	}

	public Client getClient() {
		return client;
	}

	public Money getAccumulated() {
		return accumulated;
	}

	protected static void checkPositive(Money amount) {
		ArgumentChecks.isNotNull(amount, "Amount cannot be null");
		ArgumentChecks.isTrue(amount.greaterThan(Money.ZERO), "Amount must be positive");
	}

	protected static Money normalizePositive(Money amount) {
		ArgumentChecks.isNotNull(amount, "Amount cannot be null");
		Money result = Money.calc(amount).toMoneyRoundedToCents();
		checkPositive(result);
		return result;
	}

}
