package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class VehicleType {
    private String name;
    private Money pricePerHour;
    
    private Set<Vehicle> vehicles = new HashSet<>();
    
	public VehicleType(String name, Money zero) {
		ArgumentChecks.isNotNull(name);
		ArgumentChecks.isNotEmpty(name);
		ArgumentChecks.isNotBlank(name);
		ArgumentChecks.isNotNull(zero);
		ArgumentChecks.isTrue(zero.greaterOrEqual(Money.ZERO), "Price per hour must be greater than or equal to zero");
		
		this.name = name;
		this.pricePerHour = zero.roundToCents();
	}
	public String getName() {
		return name;
	}
	public Money getPricePerHour() {
		return pricePerHour;
	}
	
	/* --- */
	public Set<Vehicle> _getVehicles() {
		return this.vehicles;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleType other = (VehicleType) obj;
		return Objects.equals(name, other.name);
	}
    
}
