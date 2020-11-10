package com.example.orderfoodandroidsever.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.orderfoodandroidsever.model.Request;
import com.example.orderfoodandroidsever.model.User;
import com.example.orderfoodandroidsever.remote.APIService;
import com.example.orderfoodandroidsever.remote.FCMRetrofitClient;
import com.example.orderfoodandroidsever.remote.IGeoCoodinates;
import com.example.orderfoodandroidsever.remote.RetrofitClient;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    //    public static final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String IS_FACE_ID = "face_id";
    public static final String KEY_ACCOUNT = "account";
    public static final String SHARED_PREFERENCE_NAME = "SettingGame";

    public static final String SHIPPER_TABLE = "Shippers";
    public static User current_user;
    public static Request current_request;
    public static final String UPDATE = "Cập Nhập";
    public static final String DELETE = "Xóa";
    public static final int PICK_IMAGE_REQUEST = 71;

    public static APIService getFCMService() {
        return FCMRetrofitClient.getClient(fcmURL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Đã đặt hàng";
        else if (status.equals("1"))
            return "Đang giao";
        else if (status.equals("2"))
            return "Đang Chuyển Hàng";
        else
            return "Đã Giao";
    }

    //Retrofit
    public static final String baseUrl = "https://maps.googleapis.com";

    //draw Route
    public static IGeoCoodinates getIGeoCoodinates() {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoodinates.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newWidth, Bitmap.Config.ARGB_8888);
        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0, pivotY = 0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());
        return date.toString();
    }

    public static void saveData(Context context, User myObject) {
        SharedPreferences mPrefs = context.getSharedPreferences("", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myObject); // myObject - instance of MyObject
        prefsEditor.putString(KEY_ACCOUNT, json);
        prefsEditor.commit();
    }

    public static User loadData(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(KEY_ACCOUNT, "");
        User obj = gson.fromJson(json, User.class);
        return obj;
    }

    public static boolean getSetting(Context context) {
        SharedPreferences sharedPreferences  = context.
                getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isVolume = sharedPreferences.getBoolean(IS_FACE_ID, false);
        return isVolume;
    }
}
