package com.example.bolasepak;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

//Kode ini dibuat dengan mengambil referensi dari link berikut
//https://github.com/SenSaa/Pedometer
public class StepCounterService extends Service implements SensorEventListener{
    SensorManager sensorManager;
    Sensor stepCounterSensor;
    int appStep;
    int stepCounter;
    int newStepCounter;
    boolean serviceStopped;
    Intent intent;
    private static final String TAG = "StepCounterService";
    public static final String BROADCAST_ACTION = "com.websmithing.yusuf.mybroadcast";
    private final Handler handler = new Handler();
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Service", "Start");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCounterSensor, 0);

        Cursor c = db.rawQuery("select * from user",null);
        c.moveToFirst();

        appStep = c.getInt(c.getColumnIndexOrThrow("step"));
        Log.d("StepCounter I/O", "read : "+Integer.toString(appStep));

        stepCounter = 0;
        newStepCounter = 0;

        serviceStopped = false;

        handler.removeCallbacks(updateBroadcastData);
        handler.post(updateBroadcastData);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        db.execSQL("update user set 'step'= "+Integer.toString(newStepCounter));
        Log.d("StepCounter I/O", "write : "+Integer.toString(newStepCounter));

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), StepCounterService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int countSteps = (int) event.values[0];
            if (stepCounter == 0) {
                stepCounter = (int) event.values[0];
            }
            newStepCounter = (countSteps - stepCounter) + appStep;
        }

        Log.d("StepCounter add", String.valueOf(newStepCounter));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        //update step ketika service di restart
        db.execSQL("update user set 'step'= "+Integer.toString(newStepCounter));
        Log.d("StepCounter I/O", "write : "+Integer.toString(newStepCounter));

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), StepCounterService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) {
                broadcastSensorValue();
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void broadcastSensorValue() {
        Log.d(TAG, "Data to Activity");
        intent.putExtra("Counted_Step_Int", newStepCounter);
        intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
        sendBroadcast(intent);
    }
}