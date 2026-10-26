package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import uo.ri.cws.domain.types.LicensePlate;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class Vehicle {

	// Natural attributes
	private LicensePlate licensePlate;
	private String make;
	private String model;
	
	private Client client;
	private Set<WorkOrder> workOrders = new HashSet<>();
	
	// Accidental attributes
	private VehicleType vehicleType;

	// ---- Constructors ----

	public Vehicle(LicensePlate plateNumber) {
		this(plateNumber, "no-make", "no-model");
	}

	public Vehicle(LicensePlate plateNumber, String make, String model) {
		ArgumentChecks.isNotNull(plateNumber);
		ArgumentChecks.isNotEmpty(make);
		ArgumentChecks.isNotEmpty(model);

		this.licensePlate = plateNumber;
		this.make = make;
		this.model = model;
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

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public Money getPricePerHour() {
		return vehicleType.getPricePerHour(); // VehicleType must return Money
	}
	
	/**
	 * TODO: pr
	 */
	public Client getClient() {
		return client;
	}
	
	/**
	 * TODO: pr
	 * @param client
	 */
	void _setClient(Client client) {
		this.client = client;
	}
	
	public Set<WorkOrder> getWorkOrders() {
		return new HashSet<>(workOrders);
	}
	
	Set<WorkOrder> _getWorkOrders() {
		return this.workOrders;
	}

	@Override
	public int hashCode() {
		return Objects.hash(licensePlate, make, model, vehicleType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		return Objects.equals(licensePlate, other.licensePlate) && Objects.equals(make, other.make)
				&& Objects.equals(model, other.model) && Objects.equals(vehicleType, other.vehicleType);
	}

	public void _setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	
}
