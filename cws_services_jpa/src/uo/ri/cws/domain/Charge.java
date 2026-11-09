package uo.ri.cws.domain;

import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

public class Charge {

    // Natural attributes
    private Money amount; // cents

    // Accidental attributes
    private Invoice invoice;
    private PaymentMean paymentMean;

    // ------ Constructors -----

    /* package */ Charge(Invoice invoice, PaymentMean paymentMean, Money amount) {
        // validates the arguments
        // store the amount rounded to cents
        // increments the paymentMean accumulated -> paymentMean.charge( amount )
        // links invoice, this and paymentMean
        ArgumentChecks.isNotNull(invoice);
        ArgumentChecks.isNotNull(paymentMean);
        ArgumentChecks.isNotNull(amount);

        Money normalized = Money.calc(amount).toMoneyRoundedToCents();
        ArgumentChecks.isTrue(normalized.greaterThan((Money.ZERO)), "El importe debe ser positivo");

        paymentMean.charge(normalized);
        this.amount = normalized;
        Associations.SettlesWithdraws.atomicLink(invoice, this, paymentMean);
    }

    // ---- Getters ----

    public Money getAmount() {
        return amount;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public PaymentMean getPaymentMean() {
        return paymentMean;
    }

    // ---- Bussiness methods ----

    /**
     * Restores and the value to the payment mean and unlinks the charge from the
     * invoice and the payment mean.
     */
    /* package */ void rewind() {
        // decrements the payment mean accumulated ( paymentMean.rewind( amount) )
        StateChecks.isTrue(invoice != null && paymentMean != null, "El cargo ya había sido devuelto");
        paymentMean.rewind(amount);
        Associations.SettlesWithdraws.atomicUnlink(this);
    }

    // ---- Association helpers ----

    void _setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    void _setPaymentMean(PaymentMean paymentMean) {
        this.paymentMean = paymentMean;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((invoice == null) ? 0 : invoice.hashCode());
        result = prime * result + ((paymentMean == null) ? 0 : paymentMean.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Charge other = (Charge) obj;
        if (invoice == null) {
            if (other.invoice != null)
                return false;
        } else if (!invoice.equals(other.invoice))
            return false;
        if (paymentMean == null) {
            if (other.paymentMean != null)
                return false;
        } else if (!paymentMean.equals(other.paymentMean))
            return false;
        return true;
    }

    // ---- toString ----
    @Override
    public String toString() {
        return "Charge [invoice=" + invoice + ", paymentMean=" + paymentMean + ", amount=" + amount + "]";
    }

}
