package uo.ri.cws.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

public class WorkOrder {

	public enum WorkOrderState {
		OPEN,
		ASSIGNED,
		FINISHED,
		APPROVED,
		INVOICED
	}

	// Natural attributes
	private LocalDateTime date;
	private String description;
	private Money amount = Money.ZERO; // cents
	private WorkOrderState state = WorkOrderState.OPEN;

	// Accidental attributes
	private Invoice invoice;
	private Mechanic mechanic;
	private Vehicle vehicle;
	private Set<Intervention> interventions = new HashSet<>();

	// ---- Constructors ----

	public WorkOrder(Vehicle vehicle) {
		this(vehicle, LocalDateTime.now(), "no-description");
	}

	public WorkOrder(Vehicle vehicle, LocalDateTime date) {
		this(vehicle, date, "no-description");
	}

	public WorkOrder(Vehicle vehicle, String description) {
		this(vehicle, LocalDateTime.now(), description);
	}

	public WorkOrder(Vehicle vehicle, LocalDateTime date, String description) {
		ArgumentChecks.isNotNull(vehicle, "Vehicle cannot be null");
		ArgumentChecks.isNotNull(date, "Date cannot be null");
		ArgumentChecks.isNotBlank(description, "Description cannot be blank");

		this.date = date.truncatedTo(ChronoUnit.MILLIS);
		this.description = description;
		this.state = WorkOrderState.OPEN;
		this.amount = Money.ZERO;

		Associations.Fixes.link(vehicle, this);
	}

	// ---- Getters ----
	public String getDescription() {
		return description;
	}

	public Money getAmount() {
		return amount;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public Mechanic getMechanic() {
		return mechanic;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public Money getPricePerHour() {
		return getVehicle().getPricePerHour();
	}

	public Set<Intervention> getInterventions() {
		return new HashSet<>(interventions);
	}

	public WorkOrderState getState() {
		return state;
	}

	// ---- Association helpers ----
	void _setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	void _setMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	void _setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	Set<Intervention> _getInterventions() {
		return interventions;
	}

	// ---- State checks ----
	public boolean isOpen() {
		return state == WorkOrderState.OPEN;
	}

	public boolean isAssigned() {
		return state == WorkOrderState.ASSIGNED;
	}

	public boolean isFinished() {
		return state == WorkOrderState.FINISHED;
	}

	public boolean isApproved() {
		return state == WorkOrderState.APPROVED;
	}

	public boolean isInvoiced() {
		return state == WorkOrderState.INVOICED;
	}

	// ---- Business methods & state transitions ----

	/**
	 * Links (assigns) the work order to a mechanic and then changes its state to ASSIGNED
	 * 
	 * @throws IllegalStateException if the work order is not in OPEN state
	 */
	public void assignTo(Mechanic mechanic) {
		ArgumentChecks.isNotNull(mechanic, "Mechanic cannot be null");
		StateChecks.isTrue(isOpen(), "Work order not open");

		Associations.Assigns.link(mechanic, this);
		this.state = WorkOrderState.ASSIGNED;
	}

	/**
	 * Unlinks (unassigns) the work order from its mechanic and then changes its state to OPEN
	 * 
	 * @throws IllegalStateException if the work order is not in ASSIGNED state
	 */
	public void unassign() {
		StateChecks.isTrue(isAssigned(), "Work order not assigned");

		Associations.Assigns.unlink(this);
		this.state = WorkOrderState.OPEN;
	}

	/**
	 * Given the right conditions unlinks the workorder and the mechanic,
	 * changes the state to FINISHED.
	 * 
	 * @throws IllegalStateException if the work order is not in ASSIGNED state
	 */
	public void finish() {
		StateChecks.isTrue(isAssigned(), "Work order not assigned");

		Associations.Assigns.unlink(this);
		this.state = WorkOrderState.FINISHED;
	}

	/**
	 * Given the right conditions changes the state to APPROVED and computes the amount.
	 * 
	 * @throws IllegalStateException if the work order is not in FINISHED state
	 */
	public void approve() {
		StateChecks.isTrue(isFinished(), "Work order not finished");

		this.state = WorkOrderState.APPROVED;
		computeAmount();
	}

	/**
	 * Changes back the state to FINISHED, given the right conditions.
	 * 
	 * @throws IllegalStateException if the work order is not in APPROVED state
	 */
	public void unapprove() {
		StateChecks.isTrue(isApproved(), "Work order not approved");

		this.state = WorkOrderState.FINISHED;
		this.amount = Money.ZERO;
	}

	/**
	 * Given the right conditions changes the state to INVOICED.
	 * 
	 * @throws IllegalStateException if the work order is not in APPROVED state or if it has no invoice assigned
	 */
	public void invoice() {
		StateChecks.isTrue(isApproved(), "Work order not approved");
		StateChecks.isNotNull(this.invoice, "Work order has no invoice assigned");

		this.state = WorkOrderState.INVOICED;
	}

	/**
	 * Changes back the state to APPROVED, given the right conditions.
	 * 
	 * @throws IllegalStateException if the work order is not in INVOICED state
	 */
	public void uninvoice() {
		StateChecks.isTrue(isInvoiced(), "Work order not invoiced");

		this.state = WorkOrderState.APPROVED;
	}

	/**
	 * Changes a finished work order to OPEN.
	 * 
	 * @throws IllegalStateException if the work order is not in FINISHED state
	 */
	public void reopen() {
		StateChecks.isTrue(isFinished(), "Work order not finished");

		this.state = WorkOrderState.OPEN;
	}

	// ---- Intervention management ----

	public void addIntervention(LocalDateTime date, int minutes) {
		addIntervention(date, minutes, Map.of());
	}

	public void addIntervention(LocalDateTime date, int minutes, Map<SparePart, Integer> substitutions) {
		StateChecks.isTrue(isAssigned(), "Work order not assigned");
		addIntervention(mechanic, date, minutes, substitutions);
	}

	public void addIntervention(Mechanic mechanic, LocalDateTime date, int minutes) {
		addIntervention(mechanic, date, minutes, Map.of());
	}

	public void addIntervention(
			Mechanic mechanic,
			LocalDateTime date,
			int minutes,
			Map<SparePart, Integer> substitutions) {
		StateChecks.isTrue(isAssigned() || isFinished(), "Work order neither assigned nor finished");
		ArgumentChecks.isNotNull(mechanic, "Mechanic cannot be null");
		ArgumentChecks.isNotNull(date, "Date cannot be null");
		ArgumentChecks.isTrue(minutes >= 0, "Minutes must be non-negative");
		ArgumentChecks.isNotNull(substitutions, "Substitutions map cannot be null");

		for (Map.Entry<SparePart, Integer> entry : substitutions.entrySet()) {
			ArgumentChecks.isNotNull(entry.getKey(), "Spare part cannot be null");
			ArgumentChecks.isNotNull(entry.getValue(), "Quantity cannot be null");
			ArgumentChecks.isTrue(entry.getValue() > 0, "Quantity must be positive");
		}

		LocalDateTime truncatedDate = date.truncatedTo(ChronoUnit.MILLIS);
		boolean duplicate = interventions.stream()
				.anyMatch(i -> i.getMechanic().equals(mechanic) && i.getDate().equals(truncatedDate));
		StateChecks.isFalse(duplicate, "Duplicate intervention");

		new Intervention(mechanic, this, truncatedDate, minutes, substitutions);
	}

	public void removeIntervention(Intervention intervention) {
		ArgumentChecks.isNotNull(intervention, "Intervention cannot be null");
		StateChecks.isTrue(isAssigned() || isFinished(), "Work order neither assigned nor finished");
		StateChecks.isTrue(interventions.contains(intervention), "Intervention does not belong to this work order");

		intervention.removeSubstitutions();
		Associations.RecordsIntervenes.atomicUnlink(intervention);
	}

	// ---- Amount calculation ----
	public void computeAmount() {
		Money.MoneyCalculator calc = Money.calc();
		for (Intervention i : interventions) {
			calc.add(i.getTotalAmount());
		}
		this.amount = calc.toMoneyRoundedToCents();
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, vehicle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkOrder other = (WorkOrder) obj;
		return Objects.equals(date, other.date) && Objects.equals(vehicle, other.vehicle);
	}

	@Override
	public String toString() {
		return "WorkOrder [date=" + date + ", description=" + description + ", amount=" + amount + ", state=" + state
				+ ", vehicle=" + vehicle + "]";
	}

}
