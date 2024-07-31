package com.gakulakov.appopenerwidget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetRemoveViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetRemoteViewsFactory(this.applicationContext, intent)
    }
}