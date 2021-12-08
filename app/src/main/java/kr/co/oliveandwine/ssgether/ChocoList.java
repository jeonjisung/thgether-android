package kr.co.oliveandwine.ssgether;

import android.app.Activity;
import android.graphics.Color;

import com.pd.chocobar.ChocoBar;

import kr.co.oliveandwine.ssgether.Save.Save_Var;

/* 새로운 형식의 토스트 메세지를 리스트화 한 것
* 사용 방법 ChocoList.getInstance().chocoGreen(getActivity, "메세지");*/

public class ChocoList {
    public void chocoGreen(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(1200)
                .green()
                .show();
    }

    public void chocoRed(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(1200)
                .red()
                .show();
    }

    public void chocoOrange(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(ChocoBar.LENGTH_SHORT)
                .orange()
                .show();
    }

    public void chocoCyan(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(ChocoBar.LENGTH_SHORT)
                .cyan()
                .show();
    }

    public void chocoBlack(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(ChocoBar.LENGTH_SHORT)
                .black()
                .show();
    }

    public void chocoBad(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(1200)
                .setBackgroundColor(Color.rgb(43, 187, 120))
                .bad()
                .show();
    }

    public void chocoGood(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(1200)
                .setBackgroundColor(Color.rgb(43, 187, 120))
                .good()
                .show();
    }

    public void chocoLove(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(ChocoBar.LENGTH_SHORT)
                .love()
                .show();
    }

    public void chocoBuild(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(ChocoBar.LENGTH_SHORT)
                .build()
                .show();
    }

    public void chocoManbo(Activity activity, String string){
        ChocoBar.builder().setActivity(activity)
                .setText(string)
                .setDuration(100)
                .green()
                .show();
    }

    private static ChocoList instance = null;

    public static synchronized ChocoList getInstance(){
        if(null == instance){
            instance = new ChocoList();
        }
        return instance;
    }
}
