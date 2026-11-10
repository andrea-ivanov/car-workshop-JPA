package uo.ri.cws.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
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
    private boolean usedForVoucher;

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
        // Validate input parameters
        // Truncate the date to milliseconds to avoid precision issues
        // Link the work order with the vehicle

        ArgumentChecks.isNotNull(vehicle, "El vehículo no puede ser nulo");
        ArgumentChecks.isNotNull(date, "La fecha no puede ser nula");
        ArgumentChecks.isNotBlank(description, "Descripción no válida");

        this.date = date.truncatedTo(ChronoUnit.MILLIS);
        this.description = description;

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
     * Links (assigns) the work order to a mechanic and then changes its state to
     * ASSIGNED
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if The work order is not in OPEN state
     */
    public void assignTo(Mechanic mechanic) {
        ArgumentChecks.isNotNull(mechanic, "El mecánico no puede ser nulo");
        StateChecks.isTrue(isOpen(), "Solo se puede asignar si el estado es OPEN");
        Associations.Assigns.link(mechanic, this);
        this.state = WorkOrderState.ASSIGNED;
    }

    /**
     * Unlinks (unassigns) the work order from its mechanic and then changes its
     * state to OPEN
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if The work order is not in ASSIGNED state
     */
    public void unassign() {
        StateChecks.isTrue(isAssigned(), "No hay orden de trabajo asignada.");
        Associations.Assigns.unlink(this);
        this.state = WorkOrderState.OPEN;
    }

    /**
     * Given the right conditions unlinks the workorder and the mechanic,
     * changes the state to FINISHED and computes the amount
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if the work order is not in ASSIGNED state
     */
    public void finish() {
        StateChecks.isTrue(isAssigned(), "No hay orden de trabajo asignada.");
        Associations.Assigns.unlink(this);
        this.state = WorkOrderState.FINISHED;
    }

    /**
     * Given the right conditions changes the state to APPROVED and computes the
     * amount
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if the work order is not in FINISHED state
     */
    public void approve() {
        StateChecks.isTrue(isFinished(), "La orden de trabajo no está ternimada");
        this.amount = computeAmount();
        this.state = WorkOrderState.APPROVED;
    }

    /**
     * Given the right conditions changes the state to INVOICED
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if the work order is not in APPROVED state or
     *                               if it has no invoice assigned
     */
    public void invoice() {
        StateChecks.isTrue(isApproved(), "La orden de trabajo no está aprobada.");
        StateChecks.isTrue(invoice != null, "No hay factura asignada.");
        this.state = WorkOrderState.INVOICED;
    }

    /**
     * Changes back the state to FINISHED, given the right conditions
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if the work order is not in APPROVED state
     */
    public void unapprove() {
        StateChecks.isTrue(isApproved(), "La orden de trabajo no está aprobada");
        this.state = WorkOrderState.FINISHED;
        this.amount = Money.ZERO;
    }

    /**
     * Changes back the state to APPROVED, given the right conditions
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if the work order is not in INVOICED state
     */
    public void uninvoice() {
        StateChecks.isTrue(isInvoiced(), "La orden de trabajo no está facturada");
        this.state = WorkOrderState.APPROVED;
    }

    /**
     * Changes a finished work order to OPEN
     * 
     * @see UML_State diagrams on the problem statement document
     * @throws IllegalStateException if the work order is not in FINISHED state
     */
    public void reopen() {
        StateChecks.isTrue(isFinished(), "La orden de trabajo debe estar cerrdad");
        this.state = WorkOrderState.OPEN;
    }

    // ---- Intervention management ----
    /**
     * This method is called by the mechanic's use case AddIntervention
     * Can only be called if the work order is assigned, and it will add an
     * intervention
     * for the mechanic assigned to the work order
     * 
     * @param date
     * @param minutes
     */
    public void addIntervention(LocalDateTime date, int minutes) {
        addIntervention(date, minutes, Map.of());
    }

    /**
     * This method is called by the mechanic's use case AddIntervention
     * Can only be called if the work order is assigned, and it will add an
     * intervention with substitutions
     * for the mechanic assigned to the work order
     * 
     * @param date
     * @param minutes
     * @param substitutions
     */
    public void addIntervention(LocalDateTime date, int minutes, Map<SparePart, Integer> substitutions) {
        StateChecks.isTrue(isAssigned(), "Work order not assigned");
        addIntervention(mechanic, date, minutes, substitutions);
    }

    /**
     * This method is called by the foreman's use case AddIntervention to mechanic
     * Can be called if the work order is assigned or finished, and it will add an
     * intervention
     * 
     * @param mechanic The mechanic performing the intervention
     * @param date     The date and time of the intervention
     * @param minutes  The duration of the intervention in minutes
     */
    public void addIntervention(Mechanic mechanic, LocalDateTime date, int minutes) {
        addIntervention(mechanic, date, minutes, Map.of());
    }

    /**
     * Adds an intervention to the work order
     * This is a factory method that creates a new Intervention and links it to the
     * work order and the mechanic
     * It is called by the other addIntervention methods or from the foreman's use
     * case
     * 
     * @param mechanic      The mechanic performing the intervention
     * @param date          The date and time of the intervention
     * @param minutes       The duration of the intervention in minutes
     * @param substitutions A map of spare parts and their quantities used in the
     *                      intervention
     * @throws IllegalStateException if the work order is neither assigned nor
     *                               finished, or if there is a duplicated
     *                               intervention
     */
    public void addIntervention(
            Mechanic mechanic,
            LocalDateTime date,
            int minutes,
            Map<SparePart, Integer> substitutions) {
        // Validate input parameters
        // State checks: work order must be assigned or finished, and no duplicated
        // intervention
        // truncate the date to milliseconds to avoid precision issues
        // Create a new Intervention (that auto links it to the work order and mechanic)
        ArgumentChecks.isNotNull(mechanic, "El mecánico no puede ser nulo");
        ArgumentChecks.isNotNull(date, "La fecha no puede ser nula");
        ArgumentChecks.isTrue(minutes >= 0, "El tiempo debe ser mayor que cero.");
        ArgumentChecks.isNotNull(substitutions, "Las sustituciones no pueden ser nulas");

        StateChecks.isTrue(isAssigned() || isFinished(), "La orden de trabajo debe estar asignada o finalizada");
        LocalDateTime truncated = date.truncatedTo(ChronoUnit.MILLIS);
        StateChecks.isFalse(checkIsInterventionDuplicated(truncated, mechanic),
                "Ya existe una intervención con este mecánico y fecha");

        new Intervention(mechanic, this, truncated, minutes, substitutions);
    }

    /**
     * Comprueba si la intervención está duplicada.
     * 
     * @param date
     */
    private boolean checkIsInterventionDuplicated(LocalDateTime truncated, Mechanic mechanic) {
        for (Intervention i : interventions) {
            if (i.getMechanic().equals(mechanic) && i.getDate().equals(truncated))
                return true;
        }

        return false;

    }

    public void removeIntervention(Intervention intervention) {
        ArgumentChecks.isNotNull(intervention);
        StateChecks.isTrue(isAssigned() || isFinished(), "Work order neither assigned nor finished");
        StateChecks.isTrue(interventions.contains(intervention), "Intervention does not belong to this work order");

        intervention.removeSubstitutions();
        Associations.RecordsIntervenes.atomicUnlink(intervention);
    }

    // ---- Amount calculation ----
    /**
     * Suma el importe de todas las intervenciones redondeando a los céntimos
     */
    private Money computeAmount() {
        Money.MoneyCalculator calculator = Money.calc();
        for (Intervention intervention : interventions) {
            calculator.add(intervention.getTotalAmount());
        }
        return calculator.toMoneyRoundedToCents();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((vehicle == null) ? 0 : vehicle.hashCode());
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
        WorkOrder other = (WorkOrder) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (vehicle == null) {
            if (other.vehicle != null)
                return false;
        } else if (!vehicle.equals(other.vehicle))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkOrder [date=" + date + ", description=" + description + ", amount=" + amount + ", state=" + state
                + "]";
    }

    public boolean isUsedForVoucher() {
        return usedForVoucher;
    }

    public void markAsUsedForVoucher() {
        this.usedForVoucher = true;
    }

}
