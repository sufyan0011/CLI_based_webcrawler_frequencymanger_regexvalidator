public class Recipe {
    private String title;
    private String sides;
    private String mealType;
    private int calories;
    private String preparationTime;
    private String website;
    private String weblink;
    private String order;
    private String email;
    private String contact;

    public Recipe(String title, String sides, String mealType, int calories, String preparationTime,
                  String website, String weblink, String order, String email, String contact) {
        this.title = title;
        this.sides = sides;
        this.mealType = mealType;
        this.calories = calories;
        this.preparationTime = preparationTime;
        this.website = website;
        this.weblink = weblink;
        this.order = order;
        this.email = email;
        this.contact = contact;
    }

    // Getters
    public String getTitle() { return title; }
    public String getSides() { return sides; }
    public String getMealType() { return mealType; }
    public int getCalories() { return calories; }
    public String getPreparationTime() { return preparationTime; }
    public String getWebsite() { return website; }
    public String getWeblink() { return weblink; }
    public String getOrder() { return order; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return String.format(
                "Title: %s\nSides: %s\nMeal Type: %s\nCalories: %d\nPreparation Time: %s\nWebsite: %s\n",
                title, sides, mealType, calories, preparationTime, website
        );
    }
}
