package de.c4vxl.kitpvp.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.c4vxl.kitpvp.Main
import de.c4vxl.kitpvp.data.struct.kit.ServerKit

/**
 * Access point for reading out server kits
 */
object ServerKits {
    private val configFile get() =
        Main.instance.dataFolder.resolve("serverKits.json")

    /**
     * Returns a list of server kits
     */
    val kits get() =
        configFile.readText().takeUnless { it.isBlank() || it.isEmpty() }
            ?.let { Gson().fromJson(it, object : TypeToken<List<ServerKit>>() {}) ?: null }
            ?: emptyList()

}