package kr.co.oliveandwine.ssgether;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import kr.co.oliveandwine.ssgether.Adapter.Item.WalkLogItem;

//========== 모바일상에서 데이터를 저장하기 위한 클래스(영구적) ==========
public class S_Preference {

    //========== [전역 변수 선언 ==========
    public static final String PREFERENCES_NAME = "rebuild_preference";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    //========== [프리퍼런스 생성] ==========
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setWalkLogArrayPref(Context context, String key, ArrayList<WalkLogItem> values) {
        SharedPreferences prefs = S_Preference.getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(values);
        editor.putString(key, json);
        editor.commit();
        editor.apply();
    }

    public static ArrayList<WalkLogItem> getWalkLogArrayPref(Context context, String key) {
        SharedPreferences prefs = S_Preference.getPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<WalkLogItem>>() {
        }.getType();
        ArrayList<WalkLogItem> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    //========== [String 값 저장] ==========
    /**
     * String 값 저장
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    //========== [boolean 값 저장] ==========
    /**
     * boolean 값 저장
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
        editor.apply();
    }

    //========== [int 값 저장] ==========
    /**
     * int 값 저장
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
        editor.apply();
    }

    //========== [String 값 호출] ==========
    /**
     * String 값 로드
     */
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    //========== [boolean 값 호출] ==========
    /**
     * boolean 값 로드
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }

    //========== [int 값 호출] ==========
    /**
     * int 값 로드
     */
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }

    //========== [특정 key 값 삭제] ==========
    /**
     * 키 값 삭제
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

    //========== [전체 key 값 삭제] ==========
    /**
     * 모든 저장 데이터 삭제
     */
    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

}//TODO 클래스 종료