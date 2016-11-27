package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.graph.GraphActivity;


public class StockWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            Intent intent = new Intent(context, StockWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            rv.setRemoteAdapter(R.id.widget_stock_list, intent);
            rv.setEmptyView(R.id.widget_stock_list, R.id.empty_state_text_view);

            Intent launchGraphActivityIntent = new Intent(context, GraphActivity.class);
            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(launchGraphActivityIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_stock_list, pendingIntent);

            appWidgetManager.updateAppWidget(id, rv);
            appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.widget_stock_list);
        }
    }
}
