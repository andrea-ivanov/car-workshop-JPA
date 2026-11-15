package uo.ri.cws.application.service.client.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.types.Address;
import uo.ri.cws.domain.types.Email;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.cws.domain.types.PhoneNumber;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

/**
 * UpdateClient
 */
public class UpdateClient implements Command<Void> {

    private ClientDto dto;
    private ClientRepository cRepo = Factories.repository.forClient();

    public UpdateClient(ClientDto dto) {
        checkArgumentsForClient(dto);
        this.dto = dto;
    }

    private void checkArgumentsForClient(ClientDto dto) {
        ArgumentChecks.isNotNull(dto, "El cliente no puede ser nulo");
        ArgumentChecks.isNotBlank(dto.id, "El id del cliente no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.nif, "El nif no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.name, "El nombre no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.surname, "Los apellidos no pueden estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressStreet, "La calle no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressCity, "La ciudad no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressCountry, "El país no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressZipcode, "El código postal no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.phone, "El teléfono no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.email, "El email no puede estar en blanco");
    }

    @Override
    public Void execute() throws BusinessException {
        Optional<Client> oc = cRepo.findById(dto.id);
        BusinessChecks.exists(oc, "No existe el cliente");
        Client client = oc.get();

        BusinessChecks.hasVersion(client.getVersion(), dto.version);

        Nif newNif = new Nif(dto.nif);
        if (!client.getNif().equals(newNif))
            BusinessChecks.doesNotExist(cRepo.findByNif(newNif), "Ya existe cliente con este nif");

        client.update(
                newNif,
                new FullName(dto.name, dto.surname), new Email(dto.email),
                new PhoneNumber(dto.phone),
                new Address(dto.addressStreet, dto.addressCity, dto.addressCountry, dto.addressZipcode));

        return null;
    }

}
