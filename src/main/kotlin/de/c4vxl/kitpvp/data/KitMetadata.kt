package de.c4vxl.kitpvp.data

import org.bukkit.Bukkit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class KitMetadata(
    var name: String,
    var createdBy: String,
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
) {
    val creatorPlayer get() =
        Bukkit.getOfflinePlayer(UUID.fromString(createdBy))
}
