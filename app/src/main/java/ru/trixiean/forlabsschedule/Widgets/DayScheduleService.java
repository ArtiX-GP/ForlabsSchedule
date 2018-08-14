package ru.trixiean.forlabsschedule.Widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (18.02.2018)
 */

public class DayScheduleService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DayScheduleDataFactory(getApplicationContext(), intent);
    }
}
