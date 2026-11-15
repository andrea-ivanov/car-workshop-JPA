package uo.ri.cws.domain;

import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.util.assertion.ArgumentChecks;

public class Recommendation extends BaseEntity {

    // natural
    private boolean isUsedForVoucher;

    // artificial
    private Client recommender;
    private Client recommended;

    protected Recommendation() {
    }

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
    public String toString() {
        return "Recommendation [isUsedForVoucher=" + isUsedForVoucher + ", recommender=" + recommender
                + ", recommended=" + recommended + "]";
    }

}
