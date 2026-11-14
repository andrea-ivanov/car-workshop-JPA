package uo.ri.cws.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.types.Nif;
import uo.ri.cws.infrastructure.persistence.jpa.util.BaseJpaRepository;
import uo.ri.util.exception.NotYetImplementedException;

public class ClientJpaRepository extends BaseJpaRepository<Client> implements ClientRepository {

	@Override
	public Optional<Client> findByNif(Nif nif) {
		throw new NotYetImplementedException("Not implemented yet");
	}

	@Override
	public List<Client> findRecommendedBy(Nif recommenderNif) {
		throw new NotYetImplementedException("Unimplemented method 'findRecommendedBy'");
	}

	@Override
	public List<Client> findWithWorkOrdersUnusedForVoucher(int minimumWorkOrders) {
		throw new NotYetImplementedException("Unimplemented method 'findWithWorkOrdersUnusedForVoucher'");
	}

	@Override
	public List<Client> findWithRecommendationsUnusedForVoucher(int minimumRecommendations) {
		throw new NotYetImplementedException("Unimplemented method 'findWithRecommendationsUnusedForVoucher'");
	}

}
