package uo.ri.cws.application.service.paymentmean.crud.commands;

import java.util.Optional;

import uo.ri.conf.Factories;
import uo.ri.cws.application.query.PaymentMeanQueries;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.PaymentMean;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.exception.BusinessChecks;
import uo.ri.util.exception.BusinessException;

/**
 * DeletePaymentMean
 */
public class DeletePaymentMean implements Command<Void> {

    private String id;
    private PaymentMeanRepository pmRepo = Factories.repository.forPaymentMean();
    private PaymentMeanQueries pmQueries = Factories.query.forPaymentMean();

    public DeletePaymentMean(String id) {
        ArgumentChecks.isNotBlank(id, "Id de medio de pago inválido");
        this.id = id;
    }

    @Override
    public Void execute() throws BusinessException {

        Optional<PaymentMean> opm = pmRepo.findById(id);
        BusinessChecks.exists(opm, "No existe método de pago con este id");

        PaymentMean pm = opm.get();
        BusinessChecks.isTrue(pm.canBeDeleted(), "Este método de pago no se puede eliminar");
        BusinessChecks.isFalse(pmQueries.hasCharges(id), "No se puede eliminar un medio de pago con cargos");

        Associations.Holds.unlink(pm);
        pmRepo.remove(pm);
        return null;
    }

}
