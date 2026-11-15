package uo.ri.cws.application.service.client.crud.commands;

import java.util.List;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.service.client.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessException;

/**
 * FindClientsRecommendedBy
 */
public class FindClientsRecommendedBy implements Command<List<ClientDto>> {

    private String recommenderNif;
    private ClientRepository clientRepo = Factories.repository.forClient();

    public FindClientsRecommendedBy(String nif) {
        ArgumentChecks.isNotBlank(nif, "El nif del recomendador no puede estar vacío");
        this.recommenderNif = nif;
    }

    @Override
    public List<ClientDto> execute() throws BusinessException {
        if (!Nif.isValid(recommenderNif)) {
            return List.of();
        }
        return DtoAssembler.toDtoList(clientRepo.findRecommendedBy(new Nif(recommenderNif)));
    }

}
