package kr.co.oliveandwine.ssgether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import kr.co.oliveandwine.ssgether.Save.Save_Var;

public class Widget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId){
        CharSequence widgetText = context.getString(R.string.appwidget_test);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        RemoteViews views2 = new RemoteViews(context.getPackageName(), R.layout.welcome_slide1);
//        views.setTextViewText(R.id.appwidget_test, );
        views.addView(R.id.inner_view, views2);


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        for(int appWidgetId : appWidgetIds){
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onEnabled(Context context){
//        LocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onDisabled(Context context){}

}