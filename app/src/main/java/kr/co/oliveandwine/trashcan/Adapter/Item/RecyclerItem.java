package kr.co.oliveandwine.trashcan.Adapter.Item;

import java.util.ArrayList;
import java.util.List;

/*
 * RecyclerView 에 들어갈 Item 모델 형식
 */

public class RecyclerItem {
    private int profileImage;
    private String name;
    private String content;
    private String time;

    private ArrayList<String> strings = new ArrayList<>();

    public int type;

    public static final int TYPE_1 = 0;

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RecyclerItem(){
    }

    public RecyclerItem(String name, String content) {
        //this.profileImage = profileImage;
        this.name = name;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
