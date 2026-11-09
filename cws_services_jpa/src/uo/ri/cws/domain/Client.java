package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import uo.ri.cws.domain.types.Address;
import uo.ri.cws.domain.types.Email;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.cws.domain.types.PhoneNumber;
import uo.ri.util.assertion.ArgumentChecks;

public class Client {

	// Natural attributes
	private Nif nif;
	private FullName fullName;
	private Email email;
	private PhoneNumber phone;
	private Address address;

	// accidentales
	private Set<Vehicle> vehicles = new HashSet<>();
	private Set<PaymentMean> paymentMeans = new HashSet<>();

	public Set<Vehicle> getVehicles() {
		return new HashSet<>(vehicles);
	}

	Set<Vehicle> _getVehicles() {
		return vehicles;
	}

	// ---- Constructors ----
	public Client(Nif nif) {
		this(nif,
				new FullName("No name", "No surname"),
				new Email("default@example.com"),
				new PhoneNumber("+34600000000"),
				new Address("No street", "No city", "No zip", "No country"));
	}

	public Client(Nif nif, FullName fullName) {
		this(nif,
				fullName,
				new Email("default@example.com"),
				new PhoneNumber("+34600000000"),
				new Address("No street", "No city", "No zip", "No country"));
	}

	public Client(Nif nif, FullName fullName, Email email, PhoneNumber phone, Address address) {
		ArgumentChecks.isNotNull(nif, "El nif no puede ser nulo");
		ArgumentChecks.isNotNull(fullName, "El nombre no puede ser nulo");
		ArgumentChecks.isNotNull(email, "El email no puede ser nulo");
		ArgumentChecks.isNotNull(phone, "El numero de telefono no puede ser nulo");
		ArgumentChecks.isNotNull(address, "La dirección no puede ser nula");
		this.nif = nif;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
	}

	// ----- getters -----

	public Nif getNif() {
		return nif;
	}

	public FullName getFullName() {
		return fullName;
	}

	public Email getEmail() {
		return email;
	}

	public PhoneNumber getPhone() {
		return phone;
	}

	public Address getAddress() {
		return address;
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
		Client other = (Client) obj;
		if (nif == null) {
			if (other.nif != null)
				return false;
		} else if (!nif.equals(other.nif))
			return false;
		return true;
	}

	/**
	 * (non-Javadoc)
	 * Atributos naturales
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client [nif=" + nif + ", fullName=" + fullName + ", email=" + email + ", phone=" + phone + ", address="
				+ address + "]";
	}

	Set<PaymentMean> _getPaymentMeans() {
		return paymentMeans;
	}

	public Set<PaymentMean> getPaymentMeans() {
		return new HashSet<>(paymentMeans);
	}
}