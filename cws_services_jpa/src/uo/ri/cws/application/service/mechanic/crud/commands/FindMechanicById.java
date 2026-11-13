package uo.ri.cws.application.service.mechanic.crud.commands;

import java.util.Optional;

import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.util.exception.BusinessException;

public class FindMechanicById implements Command<Optional<MechanicDto>> {

	private String id;

	public FindMechanicById(String id) {
		this.id = id;
	}

	@Override
	public Optional<MechanicDto> execute() throws BusinessException {

		return Optional.empty();
	}

}
