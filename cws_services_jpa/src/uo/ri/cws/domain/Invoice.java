package uo.ri.cws.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uo.ri.cws.domain.types.Money;
import uo.ri.cws.domain.types.Percentage;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

public class Invoice {
	private static final LocalDate JULY_1_2012 = LocalDate.of(2012, 7, 1);

	public enum InvoiceState {
		ISSUED, PAID
	}

	// Natural attributes
	private long number;
	private LocalDate date;
	private InvoiceState state = InvoiceState.ISSUED;

	/*
	 * Subtotal amount of the invoice, before applying VAT. This represents the
	 * taxable base.
	 */
	private Money subtotal = Money.ZERO; // cents

	/*
	 * VAT rate applied to the invoice, expressed as a percentage. Example: 21% VAT.
	 */
	private Percentage vatRate = Percentage.ofPercent(21); // default to 21%

	/*
	 * VAT amount calculated from the subtotal amount. This is the tax the customer
	 * must pay.
	 */
	private Money vatAmount = Money.ZERO; // cents

	/*
	 * Total amount of the invoice including VAT. This is the final amount the
	 * customer must pay.
	 */
	private Money total = Money.ZERO; // cents

	// Accidental attributes
	private Set<WorkOrder> workOrders = new HashSet<>();
	private Set<Charge> charges = new HashSet<>();

	// ----------------- Constructors -------------------------------------

	public Invoice(long number) {
		this(number, LocalDate.now(), List.of());
	}

	public Invoice(long number, LocalDate date) {
		this(number, date, List.of());
	}

	public Invoice(long number, List<WorkOrder> workOrders) {
		this(number, LocalDate.now(), workOrders);
	}

	public Invoice(long number, LocalDate date, List<WorkOrder> workOrders) {
		// check arguments (always), through IllegalArgumentException
		// store the number
		// add every work order calling addWorkOrder( w )
		ArgumentChecks.isTrue(number >= 0, "El número debe ser positivo");
		ArgumentChecks.isNotNull(date, "La fecha no puede ser nula");
		ArgumentChecks.isNotNull(workOrders, "La lista de ordenes de trabajo no puede ser nula");

		// compruebo que no sea ninguna nula y todas aprobadas
		for (WorkOrder wo : workOrders) {
			ArgumentChecks.isNotNull(wo, "No puede haber ordenes de trabajo nulas");
			StateChecks.isTrue(wo.isApproved(), "Las ordenes de trabajo deben estar aprobadas");
		}

		this.number = number;
		this.date = date;

		for (WorkOrder wo : workOrders)
			addWorkOrder(wo);
	}

	// ---- Getters ----
	public Set<WorkOrder> getWorkOrders() {
		return new HashSet<>(workOrders);
	}

	public Set<Charge> getCharges() {
		return new HashSet<>(charges);
	}

	public long getNumber() {
		return number;
	}

	public LocalDate getDate() {
		return date;
	}

	public Money getSubtotal() {
		return subtotal;
	}

	public Percentage getVatRate() {
		return vatRate;
	}

	public Money getVatAmount() {
		return vatAmount;
	}

	public Money getTotal() {
		return total;
	}

	// ------------------ Association helpers (package-private) --------------

	Set<WorkOrder> _getWorkOrders() {
		return workOrders;
	}

	Set<Charge> _getCharges() {
		return charges;
	}

	// ---- Business methods --------------------------------------------

	/**
	 * Adds a work order to the invoice. The work order must be approved and the
	 * invoice must not be settled.
	 * 
	 * @param workOrder
	 */
	public void addWorkOrder(WorkOrder workOrder) {
		// Validations
		// links the work order
		// compute the invoice amount after adding the work order
		// marks it as invoiced
		ArgumentChecks.isNotNull(workOrder, "La orden de trabajo no puede ser nula");
		StateChecks.isFalse(isSettled(), "La factura ya está pagada");
		StateChecks.isTrue(workOrder.isApproved(), "La orden de trabajo debe estar aprobada");

		Associations.Bills.link(this, workOrder);
		workOrder.invoice();
		computeAmount();
	}

	/**
	 * Removes a work order from the invoice. The invoice must not be settled.
	 * 
	 * @param workOrder
	 */
	public void removeWorkOrder(WorkOrder workOrder) {
		ArgumentChecks.isNotNull(workOrder, "La orden de trabajo no debe ser nula");
		StateChecks.isFalse(isSettled(), "La factura ya está pagada");
		ArgumentChecks.isTrue(workOrders.contains(workOrder), "La orden de trabajo debe estar aprobada");

		Associations.Bills.unlink(workOrder);
		workOrder.uninvoice();
		computeAmount();
	}

	public boolean isSettled() {
		return state == InvoiceState.PAID;
	}

	/**
	 * Settles the invoice by charging the specified payment means with the
	 * requested amounts.
	 * The total of the charges must equal the invoice total, and each payment mean
	 * must be able to be charged.
	 * 
	 * @param requestedCharges A map of payment means and their corresponding
	 *                         amounts to charge.
	 */
	public void settle(Map<PaymentMean, Money> requestedCharges) {
		// Validations
		// Normalize the requested charges (round to cents, check positive)
		// Check that the total of requested charges equals the invoice total
		// For each payment mean, create a Charge and link it to the invoice and payment
		// mean
		// Set the invoice state to PAID

		ArgumentChecks.isNotNull(requestedCharges, "La lista de cargos no puede ser nula");
		ArgumentChecks.isFalse(requestedCharges.isEmpty(), "La lista de cargos no puede setar vacía");
		StateChecks.isFalse(isSettled(), "La factura ya está pagada");
		StateChecks.isTrue(total.greaterThan(Money.ZERO), "No se puede liquidar una factura de 0€");

		Map<PaymentMean, Money> normalized = normalize(requestedCharges);
		Money sum = chargesTotal(normalized);
		StateChecks.isTrue(sum.equals(this.total), "El importe y los cargos no coinciden en total");

		for (Map.Entry<PaymentMean, Money> entry : normalized.entrySet()) {
			StateChecks.isTrue(entry.getKey().canBeCharged(entry.getValue()), "Un medio de pago no puede ser cobrado");
		}

		for (Map.Entry<PaymentMean, Money> entry : normalized.entrySet()) {
			new Charge(this, entry.getKey(), entry.getValue());
		}

		this.state = InvoiceState.PAID;
	}

	public void unSettle() {
		StateChecks.isTrue(isSettled(), "Invoice is not settled");

		new HashSet<>(charges).forEach(Charge::rewind);
		state = InvoiceState.ISSUED;
	}

	// ---- Calculation ----
	/*
	 * 1. Sum all work order totals = subtotal
	 * 2. Determine VAT rate based on invoice date
	 * 3. Compute VAT amount
	 * 4. Compute total amount (subtotal + VAT) rounded to cents
	 */
	private void computeAmount() {
		Money.MoneyCalculator calculator = Money.calc();
		for (WorkOrder wo : workOrders) {
			calculator.add(wo.getAmount());
		}
		this.subtotal = calculator.toMoneyRoundedToCents();
		this.vatRate = getApplicableVatRate();
		this.vatAmount = Money.calc(subtotal).times(vatRate.asRatio()).toMoneyRoundedToCents();
		this.total = Money.calc(subtotal).add(vatAmount).toMoneyRoundedToCents();
	}

	private Percentage getApplicableVatRate() {
		return date.isAfter(JULY_1_2012) ? Percentage.ofPercent(21) : Percentage.ofPercent(18);
	}

	private Map<PaymentMean, Money> normalize(Map<PaymentMean, Money> requestedCharges) {
		Map<PaymentMean, Money> result = new LinkedHashMap<>();
		requestedCharges.forEach((paymentMean, amount) -> {
			ArgumentChecks.isNotNull(paymentMean);
			ArgumentChecks.isNotNull(amount);
			Money normalizedAmount = Money.calc(amount).toMoneyRoundedToCents();
			ArgumentChecks.isTrue(normalizedAmount.greaterThan(Money.ZERO), "Charge amount must be positive");
			result.put(paymentMean, normalizedAmount);
		});
		return result;
	}

	private Money chargesTotal(Map<PaymentMean, Money> requestedCharges) {
		Money.MoneyCalculator calculator = Money.calc();
		requestedCharges.values().forEach(calculator::add);
		return calculator.toMoneyRoundedToCents();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (number ^ (number >>> 32));
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
		Invoice other = (Invoice) obj;
		if (number != other.number)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Invoice [number=" + number + ", date=" + date + ", state=" + state + ", subtotal=" + subtotal
				+ ", vatRate=" + vatRate + ", vatAmount=" + vatAmount + ", total=" + total + "]";
	}

}
