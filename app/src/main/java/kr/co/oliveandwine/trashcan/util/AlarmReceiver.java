package kr.co.oliveandwine.trashcan.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    private AlarmInterface alarmInterface;

    public void setListener(AlarmInterface listener) {
        this.alarmInterface = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (alarmInterface == null) {
//            alarmInterface = new PedometerService();
//        }
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            return;
        }
        alarmInterface.receiveAlarm();
    }
}
