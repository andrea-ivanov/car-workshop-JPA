package uo.ri.cws.application.service.invoice.create.commands;

import java.math.BigDecimal;
import java.util.Map;

import uo.ri.cws.application.util.command.Command;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessException;

public class SettleInvoice implements Command<Void> {

    public SettleInvoice(String invoiceId, Map<String, BigDecimal> charges) {
        ArgumentChecks.isNotBlank(invoiceId);

    }

    @Override
    public Void execute() throws BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

}
