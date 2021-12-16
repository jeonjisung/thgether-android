package kr.co.oliveandwine.ssgether.Adapter.Item;

public class CustomListItem {

    private String name;
    private String kcal;
    private String contents;

    public CustomListItem(){

    }

    public CustomListItem(String name, String kcal, String contents){
        this.name = name;
        this.kcal = kcal;
        this.contents = contents;

    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

}
