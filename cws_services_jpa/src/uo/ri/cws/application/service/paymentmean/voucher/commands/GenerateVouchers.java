package uo.ri.cws.application.service.paymentmean.voucher.commands;

import java.util.List;
import java.util.UUID;

import uo.ri.conf.Factories;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.Voucher;
import uo.ri.cws.domain.types.Money;
import uo.ri.cws.domain.types.VoucherCode;
import uo.ri.util.exception.BusinessException;

/**
 * GenerateVouchers
 */
public class GenerateVouchers implements Command<int[]> {

    // datos bono por 3 órdenes de trabajo
    private static final int MIN_WORKORDERS = 3;
    private static final Money AMOUNT_VOUCHER_20 = Money.of("20");
    private static final String VOUCHER_20_DESCR = "Por tres órdenes de trabajo";

    // datos bono por 3 recomendaciones
    private static final int MIN_RECOMMENDATIONS = 3;
    private static final Money AMOUNT_VOUCHER_25 = Money.of("25");
    private static final String VOUCHER_25_DESCR = "Por recomendación";

    // datos bono por factura de más de 500
    private static final Money MIN_INVOICE_AMOUNT = Money.of("500");
    private static final Money AMOUNT_VOUCHER_30 = Money.of("30");
    private static final String VOUCHER_30_DESCR = "Por factura de más de 500";

    private InvoiceRepository invoiceRepo = Factories.repository.forInvoice();
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();
    private ClientRepository cRepo = Factories.repository.forClient();

    @Override
    public int[] execute() throws BusinessException {
        int vouchers20 = generateVouchers20();
        int vouchers25 = generateVouchers25();
        int vouchers30 = generateVouchers30();
        return new int[] { vouchers20, vouchers25, vouchers30 };
    }

    /**
     * Genera bonos de 20€ por tres órdenes de trabajo facturadas y pagadas.
     * 
     * @return
     */
    private int generateVouchers20() {
        List<Client> clients = cRepo.findWithWorkOrdersUnusedForVoucher(MIN_WORKORDERS);
        int count = 0;
        for (Client c : clients) {
            while (c.getWorkOrdersAvailableForVoucher20().size() >= MIN_WORKORDERS) {
                Voucher v = createVoucher(c, VOUCHER_20_DESCR, AMOUNT_VOUCHER_20);
                pmRepo.add(v);
                c.markThreeWorkOrdersAsUsedForVoucher20();
                count++;
            }
        }
        return count;
    }

    /**
     * Genera bonos de 25€ por tres recomendaciones. Tanto cliente que recomienda
     * como recomendado deben tener al menos una factura facturada y pagada.
     * 
     * @return
     */
    private int generateVouchers25() {
        List<Client> clients = cRepo.findWithRecommendationsUnusedForVoucher(MIN_RECOMMENDATIONS);
        int count = 0;
        for (Client c : clients) {
            while (c.isEligibleForVoucher25()) {
                Voucher v = createVoucher(c, VOUCHER_25_DESCR, AMOUNT_VOUCHER_25);
                pmRepo.add(v);
                c.markThreeRecommendationsAsUsedForVoucher25();
                count++;
            }
        }
        return count;
    }

    /**
     * Genera bonos de 30€ para facturas cuyo precio haya sido de más de 500€.
     * 
     * @return
     */
    private int generateVouchers30() {
        List<Invoice> invoices = invoiceRepo.findOverAmountUnusedForVoucher(MIN_INVOICE_AMOUNT);
        int count = 0;
        for (Invoice i : invoices) {
            if (!i.canGenerateVoucher30())
                continue;
            Client c = getClientFromInvoice(i);
            Voucher v = createVoucher(c, VOUCHER_30_DESCR, AMOUNT_VOUCHER_30);
            pmRepo.add(v);
            i.markAsUsedForVoucher();
            count++;
        }
        return count;
    }

    /*
     * Obtiene el cliente de la siguiente orden de trabajo iterando las ordenes de
     * trabajo de la lista en facturas
     */
    private Client getClientFromInvoice(Invoice i) {
        return i.getWorkOrders().iterator().next().getVehicle().getClient();
    }

    private Voucher createVoucher(Client c, String description, Money amount) {
        VoucherCode code = VoucherCode.of("VOUCHER-" + UUID.randomUUID());
        Voucher voucher = new Voucher(code, description, amount);
        Associations.Holds.link(c, voucher);
        return voucher;
    }
}
