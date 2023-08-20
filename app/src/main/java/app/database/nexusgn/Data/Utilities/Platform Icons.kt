package app.database.nexusgn.Data.Utilities

import android.content.Context
import app.database.nexusgn.Data.ApiDataModel.Platforms
import app.database.nexusgn.R

class IconManager(
    context: Context
) {
    private val contextProvider = context

    private fun stringProvider(name: Int): String {
        return contextProvider.getString(name)
    }

    fun distinctPlatforms(platforms: List<Platforms>): List<Pair<String, Int>> {
        val distinctPlatforms = mutableSetOf<Pair<String, Int>>()
        val platformMappings = mapOf(
            listOf(
                stringProvider(R.string.playstation),
                stringProvider(R.string.ps)
            ) to Pair(stringProvider(R.string.playstation), R.drawable.playstation),

            listOf(
                stringProvider(R.string.mac),
                stringProvider(R.string.ios),
                stringProvider(R.string.apple)
            ) to Pair(stringProvider(R.string.apple), R.drawable.apple),

            listOf(
                stringProvider(R.string.nintendo),
                stringProvider(R.string.wii),
                stringProvider(R.string.gameBoy),
                stringProvider(R.string.nes),
                stringProvider(R.string.gamecube)
            ) to Pair(stringProvider(R.string.nintendo), R.drawable.nintendo),

            listOf(stringProvider(R.string.xbox)) to Pair(stringProvider(R.string.xbox), R.drawable.xbox),
            listOf(stringProvider(R.string.pc)) to Pair(stringProvider(R.string.pc), R.drawable.windows),
            listOf(stringProvider(R.string.android)) to Pair(stringProvider(R.string.android), R.drawable.android),
            listOf(stringProvider(R.string.linux)) to Pair(stringProvider(R.string.linux), R.drawable.linux),
            listOf(stringProvider(R.string.sega),stringProvider(R.string.dreamcast)) to Pair(stringProvider(R.string.sega), R.drawable.sega),
            listOf(stringProvider(R.string.atari)) to Pair(stringProvider(R.string.atari), R.drawable.atari)
        )

        for (individualPlatforms in platforms) {
            val platformName = individualPlatforms.platform?.name?.lowercase()
            for ((keywords, platformInfo) in platformMappings) {
                if (keywords.any { platformName?.contains(it.lowercase()) == true }) {
                    distinctPlatforms.add(platformInfo)
                    break
                }
            }
        }

        return distinctPlatforms.toList()
    }
}