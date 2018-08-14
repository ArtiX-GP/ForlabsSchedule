package ru.trixiean.forlabsschedule.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

import ru.trixiean.forlabsschedule.R;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (13.02.2018)
 */

public class ThemeWrapper {

    private final static int THEME_DEFAULT = 0;
    private final static int THEME_DARCULA = 1;
    private final static int THEME_RED_WINE = 2;
    private final static int THEME_NEON = 3;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void setTheme(Activity activity, int theme) {
        Saver.save("ThemeID", String.valueOf(theme));
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityApplyTheme(Activity activity, boolean isTransparentStatusBar) {
        Log.e("Err", String.valueOf(Integer.parseInt(Saver.get().getString("ThemeID", "0"))));
        switch (Integer.parseInt(Saver.get().getString("ThemeID", "0"))) {
            default:
            case THEME_DEFAULT:
                if (isTransparentStatusBar)
                    activity.setTheme(R.style.AppTheme_TransparentStatusBar);
                else
                    activity.setTheme(R.style.AppTheme);
                break;
            case THEME_DARCULA:
                if (isTransparentStatusBar)
                    activity.setTheme(R.style.AppTheme_Darcula_TransparentStatusBar);
                else
                    activity.setTheme(R.style.AppTheme_Darcula);
                break;
            case THEME_RED_WINE:
                if (isTransparentStatusBar) {
                    activity.setTheme(R.style.AppTheme_RedWine_TransparentStatusBar);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        View decor = activity.getWindow().getDecorView();
                        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                } else
                    activity.setTheme(R.style.AppTheme_RedWine);
                break;
            case THEME_NEON:
                if (isTransparentStatusBar)
                    activity.setTheme(R.style.AppTheme_Neon_TransparentStatusBar);
                else
                    activity.setTheme(R.style.AppTheme_Neon);
                break;
        }
    }
}
