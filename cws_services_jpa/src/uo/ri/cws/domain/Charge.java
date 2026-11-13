package uo.ri.cws.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.cws.domain.types.Money;
import uo.ri.util.assertion.ArgumentChecks;
import uo.ri.util.assertion.StateChecks;

@Entity
@Table(name = "TCHARGES", uniqueConstraints = @UniqueConstraint(columnNames = { "INVOICE_ID", "PAYMENTMEAN_ID" }))
public class Charge extends BaseEntity {

    // Natural attributes
    @AttributeOverride(name = "amount", column = @Column(name = "CHARGE", precision = 19, scale = 2))
    private Money amount; // cents

    // Accidental attributes
    @ManyToOne
    private Invoice invoice;
    @ManyToOne
    private PaymentMean paymentMean;

    // ------ Constructors -----
    protected Charge() {
    }

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

    // ---- toString ----
    @Override
    public String toString() {
        return "Charge [invoice=" + invoice + ", paymentMean=" + paymentMean + ", amount=" + amount + "]";
    }

}
