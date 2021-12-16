package kr.co.oliveandwine.ssgether.Adapter.Item;

public class WalkLogItem {
    private String time;
    private String kcal;
    private int walkcount;

    public WalkLogItem(String time, String kcal, int walkcount) {
        super();
        this.time = time;
        this.kcal = kcal;
        this.walkcount = walkcount;
    }

    public String getTime() {
        return time;
    }

    public int getWalkcount() {
        return walkcount;
    }

    public String getKcal() {
        return kcal;
    }
}
