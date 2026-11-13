package uo.ri.cws.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.domain.Mechanic;
import uo.ri.cws.domain.types.Nif;
import uo.ri.cws.infrastructure.persistence.jpa.util.BaseJpaRepository;
import uo.ri.cws.infrastructure.persistence.jpa.util.Jpa;

public class MechanicJpaRepository
			//extends BaseJpaRepository<Mechanic>
			implements MechanicRepository {

	@Override
	public Optional<Mechanic> findByNif(Nif nif) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void add(Mechanic t) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void remove(Mechanic t) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Optional<Mechanic> findById(String id) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<Mechanic> findAll() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
