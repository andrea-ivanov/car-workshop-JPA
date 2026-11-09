package uo.ri.cws.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * Represents an intervention performed by a mechanic on a work order.
 * Internal aggregate class of WorkOrder. Only WorkOrder can create
 * Interventions.
 */
public class Intervention {
	// Natural attributes
	private LocalDateTime date;
	private int minutes;

	// Accidental attributes
	private WorkOrder workOrder;
	private Mechanic mechanic;
	private Set<Substitution> substitutions = new HashSet<>();

	// ---------------------------- Constructors -----------------------------
	/**
	 * All these constructors are package-private
	 * Only WorkOrder (aggregate root) can create Interventions
	 */
	/* package */ Intervention(Mechanic mechanic, WorkOrder workOrder, int minutes) {
		this(mechanic, workOrder, LocalDateTime.now(), minutes);
	}

	/* package */ Intervention(Mechanic mechanic, WorkOrder workOrder, LocalDateTime date, int minutes) {
		this(mechanic, workOrder, date, minutes, Map.of());
	}

	/**
	 * Creates a new Intervention with the given parameters.
	 * 
	 * @param mechanic      The mechanic performing the intervention.
	 * @param workOrder     The work order associated with the intervention.
	 * @param date          The date and time of the intervention.
	 * @param minutes       The duration of the intervention in minutes.
	 * @param substitutions A map of spare parts and their quantities used in the
	 *                      intervention.
	 */
	/* package */ Intervention(
			Mechanic mechanic,
			WorkOrder workOrder,
			LocalDateTime date,
			int minutes,
			Map<SparePart, Integer> substitutions) {

		// Validate input parameters
		// Truncate the date to milliseconds to avoid precision issues
		// links the intervention with the work order and mechanic (atomiclink)
		// For each substitution in the map, add it to the intervention
		ArgumentChecks.isNotNull(mechanic, "Mecánico no válido");
		ArgumentChecks.isNotNull(workOrder, "Orden de trabajo no válida");
		ArgumentChecks.isNotNull(date);
		ArgumentChecks.isTrue(minutes >= 0, "Tiempo no válido");
		validateSubstitutions(substitutions);

		this.date = date.truncatedTo(ChronoUnit.MILLIS);
		this.minutes = minutes;

		Associations.RecordsIntervenes.atomicLink(workOrder, this, mechanic);
		substitutions.forEach(this::addSubstitution);
	}

	// ---------------------------- Association helpers
	// -----------------------------
	void _setMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	void _setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}

	public Set<Substitution> getSubstitutions() {
		return new HashSet<>(substitutions);
	}

	Set<Substitution> _getSubstitutions() {
		return substitutions;
	}

	// ---------------------------- Business methods -----------------------------

	/* package */ void removeSubstitutions() {
		for (Substitution substitution : new HashSet<>(substitutions)) {
			Associations.PerformsSubstitutes.atomicUnlink(substitution);
		}
	}

	/**
	 * Calculates the total amount of the intervention, including work and spare
	 * parts.
	 * 
	 * @return The total amount of the intervention rounded to cents as a Money
	 *         object.
	 */
	public Money getTotalAmount() {
		return Money.calc()
				.add(calculateWorkAmount())
				.add(calculateSparesAmount())
				.toMoneyRoundedToCents();
	}

	private Money calculateWorkAmount() {
		Money pricePerHour = workOrder.getPricePerHour();

		return Money.calc(pricePerHour)
				.times(minutes)
				.div(60) // mins/hour
				.toMoneyExact();
	}

	private Money calculateSparesAmount() {
		Money.MoneyCalculator calculator = Money.calc();
		for (Substitution substitution : substitutions) {
			calculator.add(substitution.getTotalAmount());
		}
		return calculator.toMoneyExact();
	}

	private void addSubstitution(SparePart sparePart, int quantity) {
		new Substitution(sparePart, this, quantity);
	}

	private static void validateSubstitutions(Map<SparePart, Integer> substitutions) {
		ArgumentChecks.isNotNull(substitutions, "Las sustituciones no pueden ser nulas");
		substitutions.forEach((sparePart, quantity) -> {
			ArgumentChecks.isNotNull(sparePart, "La pieza de repuesto no puede ser nula");
			ArgumentChecks.isNotNull(quantity, "La pieza de sustitución no puede ser nula");
			ArgumentChecks.isTrue(quantity > 0, "La cantidad de repuestos debe ser positiva");
		});
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getMinutes() {
		return minutes;
	}

	public WorkOrder getWorkOrder() {
		return workOrder;
	}

	public Mechanic getMechanic() {
		return mechanic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((workOrder == null) ? 0 : workOrder.hashCode());
		result = prime * result + ((mechanic == null) ? 0 : mechanic.hashCode());
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
		Intervention other = (Intervention) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (workOrder == null) {
			if (other.workOrder != null)
				return false;
		} else if (!workOrder.equals(other.workOrder))
			return false;
		if (mechanic == null) {
			if (other.mechanic != null)
				return false;
		} else if (!mechanic.equals(other.mechanic))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Intervention [date=" + date + ", minutes=" + minutes + "]";
	}

}
