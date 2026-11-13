package uo.ri.cws.application.service.mechanic.crud.commands;

import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.util.exception.BusinessException;

public class AddMechanic implements Command<MechanicDto> {

	private MechanicDto dto;

	public AddMechanic(MechanicDto dto) {
		this.dto = dto;
	}

	@Override
	public MechanicDto execute() throws BusinessException {

		return dto;
	}

}
