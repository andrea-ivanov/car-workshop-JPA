package uo.ri.cws.application.service.invoice.create.commands;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.PaymentMean;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

public class SettleInvoice implements Command<Void> {

    private String invoiceId;
    private Map<String, BigDecimal> charges;
    private InvoiceRepository invoiceRepo = Factories.repository.forInvoice();
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();

    public SettleInvoice(String invoiceId, Map<String, BigDecimal> charges) {
        checkValidArguments(invoiceId, charges);
        this.invoiceId = invoiceId;
        this.charges = Map.copyOf(charges);
    }

    private void checkValidArguments(String invoiceId, Map<String, BigDecimal> charges) {
        ArgumentChecks.isNotBlank(invoiceId, "Id de la factura inválido");
        ArgumentChecks.isNotNull(charges, "La lista de cargos no puede ser nula");
        ArgumentChecks.isFalse(charges.isEmpty(), "La lista de cargos no puede estar vacía");
        charges.forEach((id, amount) -> {
            ArgumentChecks.isNotBlank(id);
            ArgumentChecks.isNotNull(amount);
        });
    }

    @Override
    public Void execute() throws BusinessException {
        Invoice invoice = getInvoice();

        // Comprobación cargos
        List<PaymentMean> payments = pmRepo.findByIds(charges.keySet());
        BusinessChecks.isTrue(charges.size() == payments.size(), "Los métodos de pago no concuerdan");

        Map<PaymentMean, Money> requestedCharges = validateAndMapCharges(invoice, payments);

        invoice.settle(requestedCharges);
        return null;
    }

    /**
     * Obtiene todos los pagos validando que se puedan cargar correctamente uno por
     * uno. Comprueba que la suma de todos los cargos coinciden con el precio final
     * de la factura.
     * 
     * @param invoice
     * @param payments
     * @return
     * @throws BusinessException
     */
    private Map<PaymentMean, Money> validateAndMapCharges(Invoice invoice, List<PaymentMean> payments)
            throws BusinessException {
        Map<PaymentMean, Money> requestedCharges = new HashMap<>();
        Money.MoneyCalculator totalCharges = Money.calc();

        for (PaymentMean pm : payments) {
            Money amount = Money.of(charges.get(pm.getId())).roundToCents();
            BusinessChecks.isTrue(amount.greaterThan(Money.ZERO), "El importe del cargo debe ser positivo");
            BusinessChecks.isTrue(pm.canBeCharged(amount), "No se puede cargar el importe con el medio de pago");

            requestedCharges.put(pm, amount);
            totalCharges.add(amount);
        }

        BusinessChecks.isTrue(totalCharges.toMoneyRoundedToCents().equals(invoice.getTotal()),
                "La suma de los cargos no coincide con el total de la factura");
        return requestedCharges;
    }

    /**
     * Obtiene la factura que se quiere cerrar comprobando que su estado es el
     * correcto.
     * 
     * @return
     * @throws BusinessException
     */
    private Invoice getInvoice() throws BusinessException {
        Optional<Invoice> oi = invoiceRepo.findById(invoiceId);
        BusinessChecks.exists(oi, "La factura no existe");
        Invoice invoice = oi.get();
        BusinessChecks.isFalse(invoice.isSettled(), "La factura ya está pagada");
        return invoice;
    }

}
