package uo.ri.cws.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import uo.ri.cws.domain.base.BaseEntity;
import uo.ri.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TRECOMMENDATIONS", uniqueConstraints = @UniqueConstraint(columnNames = { "RECOMMENDER_ID",
        "RECOMMENDED_ID" }))
public class Recommendation extends BaseEntity {

    // natural
    @Column(name = "USEDFORVOUCHER")
    private boolean isUsedForVoucher;

    // artificial
    @ManyToOne
    private Client recommender;
    @OneToOne
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
