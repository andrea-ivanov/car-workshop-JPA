package uo.ri.cws.domain;

import uo.ri.util.assertion.ArgumentChecks;

public class Recommendation {

    // natural
    private boolean isUsedForVoucher;

    // artificial
    private Client recommender;
    private Client recommended;

    public Recommendation(Client recommender, Client recommended) {
        ArgumentChecks.isNotNull(recommender, "El recomendador no puede ser nulo");
        ArgumentChecks.isNotNull(recommended, "El recomendado no puede ser nulo");
        ArgumentChecks.isFalse(recommender.equals(recommended), "Un recomendador no se puede recomendar a sí mismo");

        Associations.Recommends.link(recommender, this, recommended);
    }

    public Client getRecommender() {
        return recommender;
    }

    public Client getRecommended() {
        return recommended;
    }

    void _setRecommender(Client recommender2) {
        this.recommender = recommender2;
    }

    void _setRecommended(Client recommended) {
        this.recommended = recommended;
    }

    public boolean isUsedForVoucher() {
        return this.isUsedForVoucher;
    }

    public void markAsUsedForVoucher() {
        this.isUsedForVoucher = true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((recommender == null) ? 0 : recommender.hashCode());
        result = prime * result + ((recommended == null) ? 0 : recommended.hashCode());
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
        Recommendation other = (Recommendation) obj;
        if (recommender == null) {
            if (other.recommender != null)
                return false;
        } else if (!recommender.equals(other.recommender))
            return false;
        if (recommended == null) {
            if (other.recommended != null)
                return false;
        } else if (!recommended.equals(other.recommended))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Recommendation [isUsedForVoucher=" + isUsedForVoucher + ", recommender=" + recommender
                + ", recommended=" + recommended + "]";
    }

}
