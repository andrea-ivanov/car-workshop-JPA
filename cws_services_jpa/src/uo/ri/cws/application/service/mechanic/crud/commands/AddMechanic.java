package uo.ri.cws.application.service.mechanic.crud.commands;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.service.mechanic.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Mechanic;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

public class AddMechanic implements Command<MechanicDto> {

	private MechanicDto dto;
	private MechanicRepository repo = Factories.repository.forMechanic();

	public AddMechanic(MechanicDto dto) {
		ArgumentChecks.isNotNull(dto, "El mecánico no puede ser nulo");
		ArgumentChecks.isNotBlank(dto.nif, "El nif del mecánico no puede estar en blanco");
		ArgumentChecks.isNotBlank(dto.name, "El nombre del mecánico no puede estar en blanco");
		ArgumentChecks.isNotBlank(dto.surname, "El apellido del mecánico no puede estar en blanco");
		this.dto = dto;
	}

	@Override
	public MechanicDto execute() throws BusinessException {
		Nif nif = new Nif(dto.nif);
		BusinessChecks.doesNotExist(repo.findByNif(nif), "Mecánico ya existente");

		FullName name = new FullName(dto.name, dto.surname);
		Mechanic m = new Mechanic(nif, name);

		repo.add(m);
		return DtoAssembler.toDto(m);
	}

}
