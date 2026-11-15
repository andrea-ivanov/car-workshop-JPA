package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.LicensePlate;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class Vehicle extends BaseEntity {

	// Natural attributes
	private LicensePlate licensePlate;
	private String make;
	private String model;

	// Accidental attributes
	private VehicleType vehicleType;
	private Client client;
	private Set<WorkOrder> workOrders = new HashSet<>();

	// ---- Constructors ----
	protected Vehicle() {
	}

	public Vehicle(LicensePlate plateNumber) {
		this(plateNumber, "no make", "no model");
	}

	public Vehicle(LicensePlate plateNumber, String make, String model) {
		ArgumentChecks.isNotNull(plateNumber, "La matricula no puede ser nula");
		ArgumentChecks.isNotBlank(make, "El make no puede estar en blanco");
		ArgumentChecks.isNotBlank(model, "El modelo no puede estar en blanco");
		this.licensePlate = plateNumber;
		this.make = make;
		this.model = model;
	}

	public Client getClient() {
		return client;
	}

	void _setClient(Client client) {
		this.client = client;
	}

	public Money getPricePerHour() {
		return vehicleType.getPricePerHour(); // VehicleType must return Money
	}

	public LicensePlate getLicensePlate() {
		return licensePlate;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	/**
	 * (non-Javadoc)
	 * Atributos naturales
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vehicle [licensePlate=" + licensePlate + ", make=" + make + ", model=" + model + "]";
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	void _setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public Set<WorkOrder> getWorkOrders() {
		return new HashSet<>(workOrders);
	}

	Set<WorkOrder> _getWorkOrders() {
		return workOrders;
	}

}
