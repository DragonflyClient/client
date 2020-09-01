package net.inceptioncloud.dragonfly.account

data class DragonflyAccount(
    var token: String? = null,
    val identifier: String,
    val uuid: String,
    val username: String,
    val creationDate: Long,
    val permissionLevel: Int,
    var linkedMinecraftAccounts: List<String>? = null
)