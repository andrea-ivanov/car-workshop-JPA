package uo.ri.cws.domain;

import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

public class Associations {

	/*
	 * Client one to many vehicle - bidireccional
	 */
	public static class Owns {

		public static void link(Client client, Vehicle vehicle) {
			ArgumentChecks.isNotNull(client);
			ArgumentChecks.isNotNull(vehicle);
			StateChecks.isTrue(vehicle.getClient() == null, "El cliente no es nulo antes de asignar nuevo");
			StateChecks.isFalse(client._getVehicles().contains(vehicle), "El cliente ya está asociado a sus vehiculos");

			vehicle._setClient(client);
			client._getVehicles().add(vehicle);
		}

		public static void unlink(Vehicle vehicle) {
			ArgumentChecks.isNotNull(vehicle);
			Client client = vehicle.getClient();
			StateChecks.isTrue(client != null, "Cliente ya vacío");
			StateChecks.isTrue(client._getVehicles().contains(vehicle));

			client._getVehicles().remove(vehicle);
			vehicle._setClient(null);
		}
	}

	/*
	 * Vehicle many to one VehicleType - unidireccional
	 */
	public static class Classifies {

		/*
		 * Relación unidireccional, VehicleType no conoce los vehículos, solo el
		 * vehículo sabe de que tipo es
		 */
		public static void link(VehicleType vehicleType, Vehicle vehicle) {
			ArgumentChecks.isNotNull(vehicleType);
			ArgumentChecks.isNotNull(vehicle);
			StateChecks.isTrue(vehicle.getVehicleType() == null, "Vehicle already has a vehicle type");

			vehicle._setVehicleType(vehicleType);
		}

		public static void unlink(Vehicle vehicle) {
			ArgumentChecks.isNotNull(vehicle);
			VehicleType vehicleType = vehicle.getVehicleType();
			StateChecks.isTrue(vehicleType != null, "Vehicle has no vehicle type");

			vehicle._setVehicleType(null);
		}
	}

	/*
	 * Client one to many PaymentMean - bidireccional
	 */
	public static class Holds {

		public static void link(Client client, PaymentMean paymentMean) {
			ArgumentChecks.isNotNull(client);
			ArgumentChecks.isNotNull(paymentMean);
			StateChecks.isTrue(paymentMean.getClient() == null, "Payment mean already has a client");
			StateChecks.isFalse(client._getPaymentMeans().contains(paymentMean),
					"Client already has this payment mean");

			paymentMean._setClient(client);
			client._getPaymentMeans().add(paymentMean);
		}

		public static void unlink(PaymentMean paymentMean) {
			ArgumentChecks.isNotNull(paymentMean);
			Client client = paymentMean.getClient();
			StateChecks.isTrue(client != null, "Payment mean has no client");
			StateChecks.isTrue(client._getPaymentMeans().contains(paymentMean),
					"Client does not have this payment mean");

			client._getPaymentMeans().remove(paymentMean);
			paymentMean._setClient(null);
		}
	}

	/*
	 * Vehicle one to many WorkOrder - bidireccional
	 */
	public static class Fixes {

		/* package */ static void link(Vehicle vehicle, WorkOrder workOrder) {
			ArgumentChecks.isNotNull(vehicle, "El vehiculo no puede ser nulo");
			ArgumentChecks.isNotNull(workOrder, "La orden de trabajo no puede ser nula");
			StateChecks.isFalse(vehicle.getWorkOrders().contains(workOrder),
					"Ya está enlazada la orden de trabajo al vehículo");
			StateChecks.isTrue(workOrder.getVehicle() == null, "Ya está enlazada la orden de trabajo al vehículo");

			workOrder._setVehicle(vehicle);
			vehicle._getWorkOrders().add(workOrder);
		}

		public static void unlink(WorkOrder workOrder) {
			ArgumentChecks.isNotNull(workOrder, "La orden de trabajo no puede ser nula");
			Vehicle vehicle = workOrder.getVehicle();
			StateChecks.isTrue(vehicle != null, "La orden de trabajo no está enlazada con el vehículo");
			StateChecks.isTrue(vehicle.getWorkOrders().contains(workOrder),
					"La orden de trabajo no está enlazada con el vehículo");

			vehicle._getWorkOrders().remove(workOrder);
			workOrder._setVehicle(null);
		}
	}

	/*
	 * Invoice one to many WorkOrder - bidireccional
	 */
	public static class Bills {

		/* package */ static void link(Invoice invoice, WorkOrder workOrder) {
			ArgumentChecks.isNotNull(invoice, "La factura no puede ser nula");
			ArgumentChecks.isNotNull(workOrder, "La orden de trabajo no puede ser nula");
			StateChecks.isFalse(invoice.getWorkOrders().contains(workOrder),
					"La factura ya tiene enlazada la orden de trabajo");
			StateChecks.isTrue(workOrder.getInvoice() == null, "La orden de trabajo ya tiene factua asociada");

			workOrder._setInvoice(invoice);
			invoice._getWorkOrders().add(workOrder);
		}

		/* package */ static void unlink(WorkOrder workOrder) {
			ArgumentChecks.isNotNull(workOrder, "La orden de trabajo no puede ser nula");
			Invoice invoice = workOrder.getInvoice();
			StateChecks.isNotNull(invoice, "La orden de trabajo no tiene asociada factura");
			StateChecks.isTrue(invoice.getWorkOrders().contains(workOrder),
					"La factura no tiene asociada la orden de trabajo");

			invoice._getWorkOrders().remove(workOrder);
			workOrder._setInvoice(null);
		}
	}

	/**
	 * Atomically manages the settles and withdraws associations of a Charge.
	 * All identity ends are set before adding to, and cleared after removing from,
	 * the inverse collection.
	 */
	public static class SettlesWithdraws {

		/* package */ static void atomicLink(
				Invoice invoice,
				Charge charge,
				PaymentMean paymentMean) {

			ArgumentChecks.isNotNull(invoice);
			ArgumentChecks.isNotNull(charge);
			ArgumentChecks.isNotNull(paymentMean);
			StateChecks.isTrue(charge.getInvoice() == null, "Charge already has an invoice");
			StateChecks.isTrue(charge.getPaymentMean() == null, "Charge already has a payment mean");
			StateChecks.isFalse(invoice._getCharges().contains(charge), "Invoice already has this charge");

			charge._setInvoice(invoice);
			charge._setPaymentMean(paymentMean);
			invoice._getCharges().add(charge);
		}

		/* package */ static void atomicUnlink(Charge charge) {
			ArgumentChecks.isNotNull(charge);
			Invoice invoice = charge.getInvoice();
			StateChecks.isTrue(invoice != null, "Charge has no invoice");
			StateChecks.isTrue(charge.getPaymentMean() != null, "Charge has no payment mean");
			StateChecks.isTrue(invoice._getCharges().contains(charge), "Invoice does not have this charge");

			invoice._getCharges().remove(charge);
			charge._setInvoice(null);
			charge._setPaymentMean(null);
		}
	}

	public static class Assigns {

		/* package */ static void link(Mechanic mechanic, WorkOrder workOrder) {
			ArgumentChecks.isNotNull(mechanic);
			ArgumentChecks.isNotNull(workOrder);
			StateChecks.isTrue(workOrder.getMechanic() == null, "Work order already has a mechanic");
			StateChecks.isFalse(mechanic._getAssigned().contains(workOrder), "Mechanic already has this work order");

			workOrder._setMechanic(mechanic);
			mechanic._getAssigned().add(workOrder);
		}

		/* package */ static void unlink(WorkOrder workOrder) {
			ArgumentChecks.isNotNull(workOrder);
			Mechanic mechanic = workOrder.getMechanic();
			StateChecks.isTrue(mechanic != null, "Work order has no mechanic");
			StateChecks.isTrue(mechanic._getAssigned().contains(workOrder), "Mechanic does not have this work order");

			mechanic._getAssigned().remove(workOrder);
			workOrder._setMechanic(null);
		}
	}

	/**
	 * Atomically manages the records and intervenes associations of an
	 * Intervention.
	 * All identity ends are set before adding to, and cleared after removing from,
	 * the inverse collection.
	 */
	public static class RecordsIntervenes {

		/* package */ static void atomicLink(
				WorkOrder workOrder,
				Intervention intervention,
				Mechanic mechanic) {
			ArgumentChecks.isNotNull(workOrder);
			ArgumentChecks.isNotNull(intervention);
			ArgumentChecks.isNotNull(mechanic);
			StateChecks.isTrue(intervention.getWorkOrder() == null, "Intervention already has a work order");
			StateChecks.isTrue(intervention.getMechanic() == null, "Intervention already has a mechanic");
			StateChecks.isFalse(workOrder._getInterventions().contains(intervention),
					"Work order already has this intervention");

			intervention._setWorkOrder(workOrder);
			intervention._setMechanic(mechanic);
			workOrder._getInterventions().add(intervention);
		}

		/* package */ static void atomicUnlink(Intervention intervention) {
			ArgumentChecks.isNotNull(intervention);
			WorkOrder workOrder = intervention.getWorkOrder();
			StateChecks.isTrue(workOrder != null, "Intervention has no work order");
			StateChecks.isTrue(intervention.getMechanic() != null, "Intervention has no mechanic");
			StateChecks.isTrue(workOrder._getInterventions().contains(intervention),
					"Work order does not have this intervention");

			workOrder._getInterventions().remove(intervention);
			intervention._setMechanic(null);
			intervention._setWorkOrder(null);
		}
	}

	/**
	 * Atomically manages the performs and substitutes associations of a
	 * Substitution.
	 * All identity ends are set before adding to, and cleared after removing from,
	 * the inverse collection.
	 */
	public static class PerformsSubstitutes {

		/* package */ static void atomicLink(
				Intervention intervention,
				Substitution substitution,
				SparePart sparePart) {
			ArgumentChecks.isNotNull(intervention);
			ArgumentChecks.isNotNull(substitution);
			ArgumentChecks.isNotNull(sparePart);
			StateChecks.isTrue(substitution.getIntervention() == null, "Substitution already has an intervention");
			StateChecks.isTrue(substitution.getSparePart() == null, "Substitution already has a spare part");
			StateChecks.isFalse(intervention._getSubstitutions().contains(substitution),
					"Intervention already has this substitution");

			substitution._setIntervention(intervention);
			substitution._setSparePart(sparePart);
			intervention._getSubstitutions().add(substitution);
		}

		/* package */ static void atomicUnlink(Substitution substitution) {
			ArgumentChecks.isNotNull(substitution);
			Intervention intervention = substitution.getIntervention();
			StateChecks.isTrue(intervention != null, "Substitution has no intervention");
			StateChecks.isTrue(substitution.getSparePart() != null, "Substitution has no spare part");
			StateChecks.isTrue(intervention._getSubstitutions().contains(substitution),
					"Intervention does not have this substitution");

			intervention._getSubstitutions().remove(substitution);
			substitution._setIntervention(null);
			substitution._setSparePart(null);
		}
	}
}
