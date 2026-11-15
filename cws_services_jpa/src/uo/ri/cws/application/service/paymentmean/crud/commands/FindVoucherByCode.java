package uo.ri.cws.application.service.paymentmean.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.VoucherDto;
import uo.ri.cws.application.service.paymentmean.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.types.VoucherCode;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessException;

/**
 * FindVoucherByCode
 */
public class FindVoucherByCode implements Command<Optional<VoucherDto>> {

    private String code;
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();

    public FindVoucherByCode(String code) {
        ArgumentChecks.isNotBlank(code, "Código inválido de bono");
        this.code = code;
    }

    @Override
    public Optional<VoucherDto> execute() throws BusinessException {
        return pmRepo.findVoucherByCode(new VoucherCode(code)).map(v -> DtoAssembler.toDto(v));
    }

}
