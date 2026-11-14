package uo.ri.cws.application.service.mechanic.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Mechanic;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

public class UpdateMechanic implements Command<Void> {

	private MechanicDto dto;
	private MechanicRepository repo = Factories.repository.forMechanic();

	public UpdateMechanic(MechanicDto dto) {
		ArgumentChecks.isNotNull(dto, "El mecánico no puede ser nulo");
		ArgumentChecks.isNotBlank(dto.id, "El id del mecánico no puede estar en blanco");
		ArgumentChecks.isNotBlank(dto.nif, "El nif del mecánico no puede estar en blanco");
		ArgumentChecks.isNotBlank(dto.name, "El nombre del mecánico no puede estar en blanco");
		ArgumentChecks.isNotBlank(dto.surname, "El apellido del mecánico no puede estar en blanco");
		this.dto = dto;
	}

	@Override
	public Void execute() throws BusinessException {

		Optional<Mechanic> om = repo.findById(dto.id);
		BusinessChecks.exists(om, "No existe mécanico");

		Mechanic m = om.get();
		BusinessChecks.hasVersion(dto.version, m.getVersion());

		m.setFullName(new FullName(dto.name, dto.surname));
		m.setNif(new Nif(dto.nif));
		m.updatedNow();

		return null;
	}

}
