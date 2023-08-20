package app.database.nexusgn.Data.Utilities

import androidx.compose.ui.Modifier

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) then(modifier(Modifier)) else this
}