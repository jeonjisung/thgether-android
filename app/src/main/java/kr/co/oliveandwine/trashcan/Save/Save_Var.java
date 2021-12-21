package kr.co.oliveandwine.trashcan.Save;


import android.widget.EditText;

import kr.co.oliveandwine.trashcan.mapFragment;

/*
 * 여러 클래스에서 간편하게 사용할 수 있도록 많은 변수, 메소드, 함수들을 집약 해둔 클래스
 * 사용 양식 Save_Var.getInstance().????
 */

public class Save_Var {
    private mapFragment mapFragment;
    private EditText contentText;
    private int trashCode;
    private boolean isPublic;
    private int walkCount = 0;

    private String walkStop_time;

    public String getWalkStopTime() {
        return walkStop_time;
    }

    public void setWalkStopTime(String walkStop_time) {
        this.walkStop_time = walkStop_time;
    }

    public int getWalkCount() {
        return walkCount;
    }

    public void setWalkCount(int walkCount) {
        this.walkCount = walkCount;
    }

    public EditText getContentText() {
        return contentText;
    }

    public void setContentText(EditText contentText) {
        this.contentText = contentText;
    }

    public mapFragment getMapFragment() {
        return mapFragment;
    }

    public void setMapFragment(mapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getTrashCode() {
        return trashCode;
    }

    public void setTrashCode(int trashCode) {
        this.trashCode = trashCode;
    }

    private static Save_Var instance = null;

    public static synchronized Save_Var getInstance(){
        if(null == instance){
            instance = new Save_Var();
        }
        return instance;
    }


}
