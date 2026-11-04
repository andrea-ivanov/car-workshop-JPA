package uo.ri.cws.domain;

import java.util.Objects;

import uo.ri.cws.domain.types.Money;
import uo.ri.cws.domain.types.VoucherCode;
import uo.ri.util.assertion.ArgumentChecks;

public class Voucher extends PaymentMean {

    private VoucherCode code;
    private String description;
    private Money availableAmount; // cents

    // ---- Constructors ----
    public Voucher(VoucherCode code, Money available) {
        this(code, "no-description", available);
    }

    public Voucher(VoucherCode code, String description, Money available) {
        ArgumentChecks.isNotNull(code);
        ArgumentChecks.isNotBlank(description);
        ArgumentChecks.isNotNull(available);
        ArgumentChecks.isTrue(available.isNonNegative(), "Available amount must be non-negative");

        this.code = code;
        this.description = description;
        this.availableAmount = Money.calc(available).toMoneyRoundedToCents();
    }

    // ------ Getters and setters (if needed) ----

    public Money getAvailableAmount() {
        return availableAmount;
    }

    public String getDescription() {
        return description;
    }

    public void changeDescription(String description) {
        ArgumentChecks.isNotBlank(description);
        this.description = description;
    }

    public VoucherCode getCode() {
        return code;
    }

    // ---- Business methods ----

    @Override
    /* package */ void charge(Money amount) {
        // Charges the amount to the payment mean and subtracts it from the available
        // amount
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    /* package */ void rewind(Money amount) {
        // Rewinds the amount to the payment mean and adds it to the available amount
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean canBeCharged(Money amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
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
        Voucher other = (Voucher) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Voucher [code=" + code + ", description=" + description + ", availableAmount=" + availableAmount + "]";
    }

}
