package uo.ri.cws.application.service.mechanic.crud.commands;

import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.util.exception.BusinessException;

public class UpdateMechanic implements Command<Void> {

	private MechanicDto dto;

	public UpdateMechanic(MechanicDto dto) {
		this.dto = dto;
	}

	@Override
	public Void execute() throws BusinessException {

		return null;
	}

}
