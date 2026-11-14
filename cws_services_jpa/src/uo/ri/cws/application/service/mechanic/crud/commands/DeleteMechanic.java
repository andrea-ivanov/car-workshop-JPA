package uo.ri.cws.application.service.mechanic.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.query.MechanicQueries;
import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Mechanic;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

public class DeleteMechanic implements Command<Void> {

	private String mechanicId;
	private MechanicRepository repo = Factories.repository.forMechanic();
	private MechanicQueries mQueries = Factories.query.forMechanic();

	public DeleteMechanic(String mechanicId) {
		ArgumentChecks.isNotBlank(mechanicId, "El id del mecánico no puede ser nulo");
		this.mechanicId = mechanicId;
	}

	@Override
	public Void execute() throws BusinessException {
		// Verifico que existe
		Optional<Mechanic> om = repo.findById(mechanicId);
		BusinessChecks.exists(om, "El mecánico no existe");

		Mechanic m = om.get();

		// Verifico que no tiene ordenes de trabajo asignadas
		BusinessChecks.isTrue(m.getAssigned().isEmpty(), "El mecánico tiene ordenes de trabajo todavía asignadas");
		// Verifico que no tiene intervenciones
		BusinessChecks.isFalse(mQueries.hasInterventions(mechanicId), "El mecánico tiene intervenciones");

		repo.remove(m);

		return null;
	}

}
