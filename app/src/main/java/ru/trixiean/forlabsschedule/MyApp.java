package ru.trixiean.forlabsschedule;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;

import ru.trixiean.forlabsschedule.Utils.Saver;
import ru.trixiean.forlabsschedule.Utils.TypefaceUtil;

/**
 * Created by Trixiean on 06.05.2017.
 * https://play.google.com/store/apps/developer?id=Trixiean
 */

@AcraCore(buildConfigClass = BuildConfig.class)
public class MyApp extends Application {
    private static MyApp mSingleTon;

    public static MyApp getSingleTon() {
        return mSingleTon;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.JSON);
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .setMailTo("nikita.1911@yandex.ru")
                .setSubject("- - - Forlabs Crash Report - - -")
                .setEnabled(true);
        ACRA.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSingleTon = this;

        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/ComfortaaRegular.ttf");
        TypefaceUtil.setDefaultFont(getApplicationContext(), "MONOSPACE", "fonts/ComfortaaRegular.ttf");


        Saver.set(PreferenceManager.getDefaultSharedPreferences(this));
    }

}
