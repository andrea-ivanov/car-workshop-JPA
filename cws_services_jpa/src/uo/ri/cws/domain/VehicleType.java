package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TVEHICLETYPES")
public class VehicleType extends BaseEntity {

    @Column(unique = true)
    private String name;
    @AttributeOverride(name = "amount", column = @Column(name = "PRICE_PER_HOUR", precision = 19, scale = 2))
    private Money pricePerHour;

    protected VehicleType() {
    }

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
    public String toString() {
        return "VehicleType [name=" + name + ", pricePerHour=" + pricePerHour + "]";
    }

}
