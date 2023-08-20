package app.database.nexusgn.Data.Implementations

import android.app.Application
import android.content.Context

class ContextProviderImpl(
    private val appContext: Application
) {

    fun getContext(): Context {
        return appContext.applicationContext
    }

}