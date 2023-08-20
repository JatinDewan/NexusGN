package app.database.nexusgn.Data.Utilities

import app.database.nexusgn.Data.ApiDataModel.Platform

fun List<Platform>.rangeFinder(maxRange: Int, showAll: Boolean): List<Platform> {
    return this.subList(0, if(showAll) this.size else { if (this.size > maxRange) maxRange else this.size })
}