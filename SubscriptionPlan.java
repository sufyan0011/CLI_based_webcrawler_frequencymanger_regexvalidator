public class SubscriptionPlan {
    private String websiteName;
    private String priceRange;
    private String contact;
    private String email;
    private String rating;

    public SubscriptionPlan(String websiteName, String priceRange, String contact, String email, String rating) {
        this.websiteName = websiteName;
        this.priceRange = priceRange;
        this.contact = contact;
        this.email = email;
        this.rating = rating;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return String.format(
                "Website: %s\n" +
                        "Price Range: %s\n" +
                        "Contact: %s\n" +
                        "Email: %s\n" +
                        "Rating: %s\n" +
                        "------------------------------",
                websiteName, priceRange, contact, email, rating
        );
    }
}
