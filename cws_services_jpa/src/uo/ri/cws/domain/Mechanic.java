package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;

public class Mechanic {

	// Natural attributes
	private Nif nif;
	private FullName fullName;

	// Accidental attributes
	private Set<WorkOrder> assigned = new HashSet<>();

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nif == null) ? 0 : nif.hashCode());
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
		Mechanic other = (Mechanic) obj;
		if (nif == null) {
			if (other.nif != null)
				return false;
		} else if (!nif.equals(other.nif))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mechanic [nif=" + nif + ", fullName=" + fullName + "]";
	}

	public Set<WorkOrder> getAssigned() {
		return new HashSet<>(assigned);
	}

}
