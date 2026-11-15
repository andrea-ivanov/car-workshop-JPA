package uo.ri.cws.infrastructure.persistence.jpa.query;

import java.math.BigDecimal;
import java.util.List;

import uo.ri.cws.application.query.PaymentMeanQueries;
import uo.ri.cws.application.service.paymentmean.VoucherGenerationService.VoucherSummaryDto;
import uo.ri.cws.infrastructure.persistence.jpa.util.Jpa;
import uo.ri.util.assertion.ArgumentChecks;

/**
 * PaymentMeanJpaQueries
 */
public class PaymentMeanJpaQueries implements PaymentMeanQueries {

    @Override
    public boolean hasCharges(String paymentMeanId) {
        ArgumentChecks.isNotBlank(paymentMeanId, "Id del metodo de pago inválido");
        return 0 < Jpa.getManager()
                .createNamedQuery("PaymentMean.countCharges", Long.class)
                .setParameter(1, paymentMeanId)
                .getSingleResult();
    }

    @Override
    public List<VoucherSummaryDto> getVouchersSummary() {
        return Jpa.getManager()
                .createNamedQuery("Voucher.getSummary", Object[].class)
                .getResultList()
                .stream()
                .map(row -> new VoucherSummaryDto((String) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        (BigDecimal) row[4],
                        (BigDecimal) row[5],
                        (BigDecimal) row[6]))
                .toList();
    }

}
