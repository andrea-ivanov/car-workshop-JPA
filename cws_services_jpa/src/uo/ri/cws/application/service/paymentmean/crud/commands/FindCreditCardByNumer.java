package uo.ri.cws.application.service.paymentmean.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.CardDto;
import uo.ri.cws.application.service.paymentmean.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.types.CreditCardNumber;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessException;

/**
 * FindCreditCardByNumer
 */
public class FindCreditCardByNumer implements Command<Optional<CardDto>> {

    private String number;
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();

    public FindCreditCardByNumer(String number) {
        ArgumentChecks.isNotBlank(number, "Número de tarjeta inválido");
        this.number = number;
    }

    @Override
    public Optional<CardDto> execute() throws BusinessException {
        if (!CreditCardNumber.isValid(number))
            return Optional.empty();

        CreditCardNumber ccNumber = new CreditCardNumber(number);
        return pmRepo.findCreditCardByNumber(ccNumber).map(cc -> DtoAssembler.toDto(cc));
    }

}
