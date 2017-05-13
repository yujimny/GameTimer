package com.yujimny.jp.android.gametimer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nttdocomo.android.sdaiflib.SendOther;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();

    public static final String ACTION_BUTTON_PRESS = "com.yujimny.jp.android.gametimer.action.ACTION_BUTTON_PRESS";

    private static final String EXTRA_PARAM1 = "com.yujimny.jp.android.gametimer.extra.PARAM1";

    private static final byte LINKING_IF_PATTERN_ID = 0x20;
    private static final byte LINKING_IF_COLOR_ID = 0x30;

    private static final byte COLOR_ID_RED = 0x01; // Pochiruは赤のみ

    private Handler mHandler = new Handler();

    private TextToSpeech mTts;

    private long mStartTime;
    private boolean mHasStarted = false;
    private int mCount = 0;
    private boolean mReadyToSpeek = false;
    private int mTimerMainMinutes, mTimerAdditionalMinutes;

    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(mTts == null) mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = Locale.JAPAN;
                    if (mTts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                        mTts.setLanguage(locale);
                        Log.i(TAG, "Init TTS SUCCESS!");
                    } else {
                        Log.d(TAG, "Error SetLocale");
                    }
                    mReadyToSpeek = true;
                } else {
                    Log.d(TAG, "Init TTS ERROR!");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (null != mTts) {
            mTts.shutdown();
        }

        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);
        mTimerMainMinutes = data.getInt("TimerMain", getResources().getInteger(R.integer.default_main_timer_minutes));
        mTimerAdditionalMinutes = data.getInt("TimerAdditional", getResources().getInteger(R.integer.default_additional_timer_minutes));

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BUTTON_PRESS.equals(action)) {
                final int buttonId = intent.getIntExtra(EXTRA_PARAM1, -1);
                handleActionButtonPress(buttonId);
            }
        }

        return START_STICKY;
    }

    public static void startActionButtonPress(Context context, int buttonId) {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(ACTION_BUTTON_PRESS);
        intent.putExtra(EXTRA_PARAM1, buttonId);
        context.startService(intent);
    }

    private void handleActionButtonPress(int buttonId) {
        if(buttonId == -1) {
            return;
        }

        if(buttonId == 2) {
            if(mHasStarted) {
                long elapsedTime = TimeUnit.MILLISECONDS.toMinutes(SystemClock.uptimeMillis() - mStartTime);
                speak(String.format(Locale.JAPAN, getResources().getString(R.string.speak_timer_pause), elapsedTime));
            } else {
                startTimer();
                mHasStarted = true;
            }
        }
        if(buttonId == 7) {
            finishTimer();
            stopForeground(true);
            mHasStarted = false;
        }
    }

    private final Runnable runAdditionalTime = new Runnable() {
        @Override
        public void run() {
            mCount++;
            String message = String.format(Locale.JAPAN, getResources().getString(R.string.speak_timer_2nd_alert),
                    mTimerMainMinutes + (mTimerAdditionalMinutes * mCount));
            speak(message);
            sendOther((byte)0x23);
            if(mHasStarted) mHandler.postDelayed(runAdditionalTime, TimeUnit.MINUTES.toMillis(mTimerAdditionalMinutes));
        }
    };

    private final Runnable runStartTimer = new Runnable() {
        @Override
        public void run() {
            String message = String.format(Locale.JAPAN, getResources().getString(R.string.speak_timer_1st_alert), mTimerMainMinutes);
            speak(message);
            changeNotification(message);
            sendOther((byte)0x26);
            if(mHasStarted) mHandler.postDelayed(runAdditionalTime, TimeUnit.MINUTES.toMillis(mTimerAdditionalMinutes));
        }
    };

    private void finishTimer() {
        mHandler.removeCallbacksAndMessages(null);
        mCount = 0;
        long elapsedTime = TimeUnit.MILLISECONDS.toMinutes(SystemClock.uptimeMillis() - mStartTime);
        String message = String.format(Locale.JAPAN, getResources().getString(R.string.speak_timer_finish), elapsedTime);
        speak(message);
    }

    private void startTimer() {
        String message = String.format(Locale.JAPAN, getResources().getString(R.string.speak_timer_start), mTimerMainMinutes);
        speak(message);
        changeNotification(message);
        mStartTime = SystemClock.uptimeMillis();
        mHandler.postDelayed(runStartTimer, TimeUnit.MINUTES.toMillis(mTimerMainMinutes));
    }

    private void sendOther(byte patternId) {
        SendOther sendOther = new SendOther(getApplicationContext());
        sendOther.setIllumination(
                new byte[] {
                        LINKING_IF_PATTERN_ID,
                        patternId,
                        LINKING_IF_COLOR_ID,
                        COLOR_ID_RED
                });
        sendOther.send();
    }

    @SuppressWarnings("deprecation")
    private void speak(final String text) {
        if(!mReadyToSpeek) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speak(text);
                }
            }, 1000); // 1秒くらい見ておけばさらにリトライしなくても大丈夫だろう
            return;
        }
        Log.v(TAG, text);
        String utteranceId = "utteranceId";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            mTts.speak(text, TextToSpeech.QUEUE_ADD, map);
        }
    }

    private void changeNotification(String message){
        Log.d(TAG, message);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pend = PendingIntent.getActivity(this, 0, intent, 0);
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getText(R.string.app_name))
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setContentIntent(pend).build();
        startForeground(1, n);
    }
}
