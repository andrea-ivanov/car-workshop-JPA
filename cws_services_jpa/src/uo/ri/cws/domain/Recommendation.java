package uo.ri.cws.domain;

public class Recommendation {

    private Client recommender;
    private Client recomended;

    public Recommendation(Client recommender, Client recommended) {
        this.recommender = recommended;
        this.recomended = recommended;
    }

    public Client getRecommender() {
        return recommender;
    }

    public Client getRecommended() {
        return recomended;
    }

    void _setRecommender(Client recommender2) {
        this.recommender = recommender2;
    }

    void _setRecommended(Client recommended) {
        this.recomended = recommended;
    }

}
