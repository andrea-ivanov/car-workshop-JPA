package uo.ri.cws.domain;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

public class VehicleType {
    private String name;
    private Money pricePerHour;

    public VehicleType(String name, Money pricePerHour) {
        ArgumentChecks.isNotBlank(name, "El nombre no puede estar en blanco");
        ArgumentChecks.isNotNull(pricePerHour, "El precio por hora no puede estar en blanco");
        ArgumentChecks.isTrue(pricePerHour.isNonNegative(), "El precio por hora no puede ser negativo");
        this.name = name;
        this.pricePerHour = pricePerHour.roundToCents();
    }

    public String getName() {
        return name;
    }

    public Money getPricePerHour() {
        return pricePerHour;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        VehicleType other = (VehicleType) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "VehicleType [name=" + name + ", pricePerHour=" + pricePerHour + "]";
    }

}
