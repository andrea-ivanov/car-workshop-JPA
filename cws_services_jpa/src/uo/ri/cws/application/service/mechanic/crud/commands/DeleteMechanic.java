package uo.ri.cws.application.service.mechanic.crud.commands;

import uo.ri.cws.application.util.command.Command;
import uo.ri.util.exception.BusinessException;

public class DeleteMechanic implements Command<Void> {

	private String mechanicId;

	public DeleteMechanic(String mechanicId) {
		this.mechanicId = mechanicId;
	}

	@Override
	public Void execute() throws BusinessException {

		return null;
	}

}
