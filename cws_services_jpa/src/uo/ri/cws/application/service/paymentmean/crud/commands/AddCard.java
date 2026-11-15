package uo.ri.cws.application.service.paymentmean.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.CardDto;
import uo.ri.cws.application.service.paymentmean.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.CreditCard;
import uo.ri.cws.domain.types.CreditCardNumber;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

public class AddCard implements Command<CardDto> {

    private CardDto dto;
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();
    private ClientRepository cRepo = Factories.repository.forClient();

    public AddCard(CardDto dto) {
        ArgumentChecks.isNotNull(dto, "La tarjeta de crédito no puede ser nula");
        ArgumentChecks.isNotBlank(dto.clientId, "El id del cliente no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.cardNumber, "El número de tarjeta no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.cardType, "El tipo de tarjeta no puede estar en blanco");
        ArgumentChecks.isNotNull(dto.cardExpiration, "La fecha de caducidad no puede estar en blanco");
        ArgumentChecks.isTrue(CreditCardNumber.isValid(dto.cardNumber), "Número de tarjeta inválido");
        this.dto = dto;
    }

    @Override
    public CardDto execute() throws BusinessException {
        CreditCardNumber cNumber = new CreditCardNumber(dto.cardNumber);
        BusinessChecks.doesNotExist(pmRepo.findCreditCardByNumber(cNumber),
                "Ya existe una tarjeta de crédito con ese número");

        CreditCard cc = new CreditCard(cNumber, dto.cardType, dto.cardExpiration);
        BusinessChecks.isTrue(cc.isValidNow(), "Tarjeta caducada");

        Optional<Client> client = cRepo.findById(dto.clientId);
        BusinessChecks.exists(client, "No existe cliente con este id");

        Associations.Holds.link(client.get(), cc);
        pmRepo.add(cc);
        return DtoAssembler.toDto(cc);
    }
}
