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
	
	// Accidental attributes
	private Client client;
	private VehicleType vehicleType;
	private Set<WorkOrder> workOrders = new HashSet<>();

	// ---- Constructors ----

	public Vehicle(LicensePlate plateNumber) {
		this(plateNumber, "no-make", "no-model");
	}

	public Vehicle(LicensePlate plateNumber, String make, String model) {
		ArgumentChecks.isNotNull(plateNumber);
		ArgumentChecks.isNotBlank(make);
		ArgumentChecks.isNotBlank(model);

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
		return vehicleType.getPricePerHour();
	}
	
	public Client getClient() {
		return client;
	}
	
	void _setClient(Client client) {
		this.client = client;
	}
	
	void _setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public Set<WorkOrder> getWorkOrders() {
		return new HashSet<>(workOrders);
	}
	
	Set<WorkOrder> _getWorkOrders() {
		return this.workOrders;
	}

	@Override
	public int hashCode() {
		return Objects.hash(licensePlate);
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
		return Objects.equals(licensePlate, other.licensePlate);
	}

	@Override
	public String toString() {
		return "Vehicle [licensePlate=" + licensePlate + ", make=" + make + ", model=" + model + "]";
	}

}
