package uo.ri.cws.application.service.paymentmean.crud;

import java.util.List;
import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService;
import uo.ri.cws.application.service.paymentmean.crud.commands.AddCard;
import uo.ri.cws.application.service.paymentmean.crud.commands.AddVoucher;
import uo.ri.cws.application.service.paymentmean.crud.commands.DeletePaymentMean;
import uo.ri.cws.application.service.paymentmean.crud.commands.FindById;
import uo.ri.cws.application.service.paymentmean.crud.commands.FindCreditCardByNumer;
import uo.ri.cws.application.service.paymentmean.crud.commands.FindPaymentMeansByClientNif;
import uo.ri.cws.application.service.paymentmean.crud.commands.FindVoucherByCode;
import uo.ri.cws.application.util.command.CommandExecutor;
import uo.ri.util.exception.BusinessException;

/**
 * PaymentMeanCrudServiceImpl
 */
public class PaymentMeanCrudServiceImpl implements PaymentMeanCrudService {

    private CommandExecutor executor = Factories.executor.forExecutor();

    @Override
    public CardDto addCard(CardDto dto) throws BusinessException {
        return executor.execute(new AddCard(dto));
    }

    @Override
    public VoucherDto addVoucher(VoucherDto dto) throws BusinessException {
        return executor.execute(new AddVoucher(dto));
    }

    @Override
    public void deletePaymentMean(String id) throws BusinessException {
        executor.execute(new DeletePaymentMean(id));
    }

    @Override
    public Optional<PaymentMeanDto> findById(String id) throws BusinessException {
        return executor.execute(new FindById(id));
    }

    @Override
    public Optional<CardDto> findCreditCardByNumber(String number) throws BusinessException {
        return executor.execute(new FindCreditCardByNumer(number));
    }

    @Override
    public List<PaymentMeanDto> findPaymentMeansByClientNif(String nif) throws BusinessException {
        return executor.execute(new FindPaymentMeansByClientNif(nif));
    }

    @Override
    public Optional<VoucherDto> findVoucherByCode(String code) throws BusinessException {
        return executor.execute(new FindVoucherByCode(code));
    }

}
