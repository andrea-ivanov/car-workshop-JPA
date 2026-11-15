package uo.ri.cws.application.service.paymentmean.crud.commands;

import java.math.BigDecimal;
import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.VoucherDto;
import uo.ri.cws.application.service.paymentmean.crud.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Voucher;
import uo.ri.cws.domain.types.Money;
import uo.ri.cws.domain.types.VoucherCode;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

/**
 * AddVoucher
 */
public class AddVoucher implements Command<VoucherDto> {

    private VoucherDto dto;
    private ClientRepository cRepo = Factories.repository.forClient();
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();

    public AddVoucher(VoucherDto dto) {
        ArgumentChecks.isNotNull(dto, "El bono no puede ser nula");
        ArgumentChecks.isNotBlank(dto.clientId, "El id del cliente no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.code, "El código de bono no puede estar en blanco");
        ArgumentChecks.isNotBlank(dto.description, "La descripción de bono no puede estar en blanco");
        ArgumentChecks.isNotNull(dto.availableAmount, "El importe disponible no puede ser nulo");
        ArgumentChecks.isTrue(dto.availableAmount.compareTo(BigDecimal.ZERO) >= 0,
                "El dinero disponible debe ser más que cero");
        this.dto = dto;
    }

    @Override
    public VoucherDto execute() throws BusinessException {
        Optional<Client> oc = cRepo.findById(dto.clientId);
        BusinessChecks.exists(oc, "No existe cliente con este id");

        VoucherCode vCode = new VoucherCode(dto.code);
        BusinessChecks.doesNotExist(pmRepo.findVoucherByCode(vCode), "Ya existe bono con ese código");

        Voucher voucher = new Voucher(vCode, dto.description, Money.of(dto.availableAmount));
        Associations.Holds.link(oc.get(), voucher);
        pmRepo.add(voucher);

        return DtoAssembler.toDto(voucher);
    }

}
