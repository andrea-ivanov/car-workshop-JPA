![flujo_recomendaciones](recursos_estudio/image.png)

En Recommendation.java (la papeleta): Guarda a las dos personas (Client):

private Client recommender; 
→
→ ¿Quién recomendó? (Ana). Su getter es getRecommender().
private Client recommended; 
→
→ ¿A quién recomendó? (Beto). Su getter es getRecommended(). (Nota: en tu Recommendation.java actual lo tienes escrito con una sola m, recomended; ponle las dos m recommended para que coincida con el estándar y con JPA).

En Client.java (la persona): Un cliente NO guarda directamente objetos Client, sino que guarda las papeletas (Recommendation):

private Set<Recommendation> recommendations = new HashSet<>(); (plural) 
→
→ Las papeletas de la gente a la que YO he recomendado (Ana tiene aquí las papeletas de Beto y Carla). Su getter es getRecommendations().
private Recommendation recommended; (o mejor aún: private Recommendation recommendation; en singular) 
→
→ La única papeleta gracias a la cual YO llegué al taller (Beto tiene aquí la papeleta #1; Ana tiene null porque a ella no la recomendó nadie).
Consejo de legibilidad: En Client.java ahora mismo lo llamaste private Recommendation recommended;. Funciona, pero llamarlo private Recommendation recommendation; (en singular) es mucho más legible porque:
Es de tipo Recommendation (no Client).
Su getter se llama getRecommendation() y su setter _setRecommendation(...).
Así distingues al instante:
recommendation (singular) = la recomendación que recibí (0..1).
recommendations (plural) = las recomendaciones que hice (0..*).