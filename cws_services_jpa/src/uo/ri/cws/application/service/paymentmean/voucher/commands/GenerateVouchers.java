package uo.ri.cws.application.service.paymentmean.voucher.commands;

import java.util.List;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.exception.BusinessException;

/**
 * GenerateVouchers
 */
public class GenerateVouchers implements Command<int[]> {

    private static final Money amountVoucher = Money.of("500");
    private InvoiceRepository invoiceRepo = Factories.repository.forInvoice();

    @Override
    public int[] execute() throws BusinessException {
        List<Invoice> invoices = invoiceRepo.findOverAmountUnusedForVoucher(amountVoucher);
        return null;
    }

}
