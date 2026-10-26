package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Objects;
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
	
	private Set<Vehicle> vehicles = new HashSet<>();

	// ---- Constructors ----
	public Client(Nif nif) {
		this(nif, 
			new FullName("No name", "No surname"), 
			new Email("default@example.com"),
			new PhoneNumber("+34600000000"), 
			new Address("No street", "No city", "No zip", "No country")
		);
	}

	public Client(Nif nif, FullName fullName) {
	    this(nif,
	        fullName,
	        new Email("default@example.com"),
	        new PhoneNumber("+34600000000"),
			new Address("No street", "No city", "No zip", "No country")
	    );
	}
	
	public Client(Nif nif, FullName fullName, Email email, PhoneNumber phone, Address address) {
		ArgumentChecks.isNotNull(nif);
		ArgumentChecks.isNotNull(fullName);
		ArgumentChecks.isNotNull(email);
		ArgumentChecks.isNotNull(phone);
		ArgumentChecks.isNotNull(address);

		this.nif = nif;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
	}

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
	
	/**
	 * todo
	 * @return
	 */
	public Set<Vehicle> getVehicles() {
		Set<Vehicle> vehicles = new HashSet<>(this.vehicles);
		return vehicles;
	}
	
	/**
	 * TODO: pr
	 * @return
	 */
	Set<Vehicle> _getVehicles() {
		return this.getVehicles();
	}

	@Override
	public int hashCode() {
		return Objects.hash(nif);
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
		return Objects.equals(nif, other.nif);
	}

	
	
}