package co.edu.univalle.model;

public class Card {

    private String name;
    private int atk;
    private int def;
    private String imageUrl;

    public Card(String name, int atk, int def, String imageUrl){
        this.name = name;
        this.atk = atk;
        this.def = def;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", atk=" + atk +
                ", def=" + def +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
