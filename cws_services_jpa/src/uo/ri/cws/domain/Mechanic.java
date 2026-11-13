package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TMECHANICS")
public class Mechanic extends BaseEntity {

	// Natural attributes
	@AttributeOverride(name = "nif", column = @Column(unique = true))
	private Nif nif;
	private FullName fullName;

	// Accidental attributes
	@OneToMany(mappedBy = "mechanic")
	private Set<WorkOrder> assigned = new HashSet<>();

	protected Mechanic() {
	}

	public Mechanic(Nif nif) {
		this(nif, new FullName("No name", "No surname"));
	}

	public Mechanic(Nif nif, FullName name) {
		ArgumentChecks.isNotNull(nif, "El nif no puede ser nulo");
		ArgumentChecks.isNotNull(name, "El nombre no puede ser nulo");
		this.nif = nif;
		this.fullName = name;
	}

	// ---- Setters (optional, controlled) ----

	public void setFullName(FullName name) {
		ArgumentChecks.isNotNull(name);
		this.fullName = name;
	}

	public void setNif(Nif nif) {
		ArgumentChecks.isNotNull(nif);
		this.nif = nif;
	}

	// ---- Associations helpers ----

	Set<WorkOrder> _getAssigned() {
		return assigned;
	}

	public Nif getNif() {
		return nif;
	}

	public FullName getFullName() {
		return fullName;
	}

	@Override
	public String toString() {
		return "Mechanic [nif=" + nif + ", fullName=" + fullName + "]";
	}

	public Set<WorkOrder> getAssigned() {
		return new HashSet<>(assigned);
	}

}
