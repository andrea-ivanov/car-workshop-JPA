package uo.ri.cws.application.service.client.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.repository.RecommendationRepository;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.service.client.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Cash;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Recommendation;
import uo.ri.cws.domain.types.Address;
import uo.ri.cws.domain.types.Email;
import uo.ri.cws.domain.types.FullName;
import uo.ri.cws.domain.types.Nif;
import uo.ri.cws.domain.types.PhoneNumber;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

/**
 * AddClient
 */
public class AddClient implements Command<ClientDto> {

    private ClientDto dto;
    private String recommenderNif;

    private ClientRepository cRepo = Factories.repository.forClient();
    private RecommendationRepository rRepo = Factories.repository.forRecommendation();
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();

    public AddClient(ClientDto dto, String recomenderNif) {
        checkParametersConstructor(dto, recomenderNif);
        this.dto = dto;
        this.recommenderNif = recomenderNif;
    }

    private void checkParametersConstructor(ClientDto dto, String recomenderNif) {
        ArgumentChecks.isNotNull(dto, "El cliente no puede ser nulo");
        ArgumentChecks.isNotBlank(dto.nif, "El nif no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.name, "El nombre no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.surname, "Los apellidos no pueden estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressStreet, "La calle es no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressCity, "La ciudad es no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressCountry, "El país no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.addressZipcode, "El código postal no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.phone, "El teléfono no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.email, "El email no puede estar en blanco");
        if (recomenderNif != null)
            ArgumentChecks.isNotBlank(recomenderNif, "El nif del recomendador no puede estar vacío");
    }

    /**
     * (non-Javadoc)
     * Se crea siempre un cash para el cliente nuevo, el recomendador puede ser nulo
     * pero no puede estar en blanco ni coincidir con su propio nif
     * 
     * @see uo.ri.cws.application.util.command.Command#execute()
     */
    @Override
    public ClientDto execute() throws BusinessException {
        Nif nif = new Nif(dto.nif);
        BusinessChecks.doesNotExist(cRepo.findByNif(nif), "El cliente ya existe");

        Client client = new Client(
                nif,
                new FullName(dto.name, dto.surname),
                new Email(dto.email),
                new PhoneNumber(dto.phone),
                new Address(dto.addressStreet, dto.addressCity, dto.addressCountry, dto.addressZipcode));

        Cash cash = new Cash(client);
        cRepo.add(client);
        pmRepo.add(cash);

        checkRecommendationChore(client);

        return DtoAssembler.toDto(client);
    }

    private void checkRecommendationChore(Client client) throws BusinessException {
        if (recommenderNif != null) {
            BusinessChecks.isTrue(Nif.isValid(recommenderNif), "Nif del recomendador no válido");
            Nif recNif = new Nif(recommenderNif);
            BusinessChecks.isFalse(recNif.equals(client.getNif()), "Un cliente no puede recomendarse a sí mismo");

            Optional<Client> recommender = cRepo.findByNif(recNif);
            BusinessChecks.exists(recommender, "no existe recomendador");

            Recommendation recommendation = new Recommendation(recommender.get(), client);
            rRepo.add(recommendation);
        }
    }

}
