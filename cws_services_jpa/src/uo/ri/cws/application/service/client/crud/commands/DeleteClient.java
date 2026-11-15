package uo.ri.cws.application.service.client.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.query.ClientQueries;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.repository.RecommendationRepository;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.PaymentMean;
import uo.ri.cws.domain.Recommendation;
import uo.ri.cws.domain.types.Nif;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

/**
 * DeleteClient
 */
public class DeleteClient implements Command<Void> {

    private String nif;
    private ClientRepository cRepo = Factories.repository.forClient();
    private ClientQueries cQueries = Factories.query.forClient();
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();
    private RecommendationRepository rRepo = Factories.repository.forRecommendation();

    public DeleteClient(String nif) {
        ArgumentChecks.isNotBlank(nif, "Nif del cliente inválido");
        this.nif = nif;
    }

    @Override
    public Void execute() throws BusinessException {
        BusinessChecks.isTrue(Nif.isValid(nif), "Formato de nif no válido");

        Optional<Client> oc = cRepo.findByNif(new Nif(nif));
        BusinessChecks.exists(oc, "No existe cliente con este nif");

        Client client = oc.get();
        BusinessChecks.isTrue(client.getVehicles().isEmpty(), "El cliente tiene vehículos registrados");
        BusinessChecks.isFalse(cQueries.hasPaidInvoices(client.getId()), "El cliente tiene facturas pagadas");

        unlinkPaymentMeans(client);

        unlinkRecommendations(client);

        unlinkRecommendationReceived(client);

        cRepo.remove(client);
        return null;
    }

    private void unlinkRecommendationReceived(Client client) {
        if (client.getRecommendation() != null) {
            Recommendation rec = client.getRecommendation();
            Associations.Recommends.unlink(rec);
            rRepo.remove(rec);
        }
    }

    private void unlinkRecommendations(Client client) {
        for (Recommendation r : client.getRecommendations()) {
            Associations.Recommends.unlink(r);
            rRepo.remove(r);
        }
    }

    private void unlinkPaymentMeans(Client client) {
        for (PaymentMean pm : client.getPaymentMeans()) {
            Associations.Holds.unlink(pm);
            pmRepo.remove(pm);
        }
    }

}
