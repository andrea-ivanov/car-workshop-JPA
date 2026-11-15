package uo.ri.cws.application.service.paymentmean.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.PaymentMeanDto;
import uo.ri.cws.application.service.paymentmean.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessException;

/**
 * FindById
 */
public class FindById implements Command<Optional<PaymentMeanDto>> {

    private String id;
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();

    public FindById(String id) {
        ArgumentChecks.isNotBlank(id, "El id del método de pago no puede estar en blanco");
        this.id = id;
    }

    @Override
    public Optional<PaymentMeanDto> execute() throws BusinessException {
        return pmRepo.findById(id).map(pm -> DtoAssembler.toDto(pm));
    }

}
