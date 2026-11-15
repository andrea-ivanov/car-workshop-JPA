package uo.ri.cws.infrastructure.persistence.jpa.query;

import uo.ri.cws.application.query.ClientQueries;
import uo.ri.cws.infrastructure.persistence.jpa.util.Jpa;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * ClientJpaQueries
 */
public class ClientJpaQueries implements ClientQueries {

    @Override
    public boolean hasPaidInvoices(String clientId) {
        ArgumentChecks.isNotBlank(clientId, "Id del cliente no válido");
        return 0 < Jpa.getManager()
                .createNamedQuery("Client.countPaidInvoices", Long.class)
                .setParameter(1, clientId)
                .getSingleResult();
    }

}
