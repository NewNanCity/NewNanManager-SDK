package com.nanmanager.bukkit.http

import com.newnanmanager.generated.apis.HealthApi
import com.newnanmanager.generated.apis.IPServiceApi
import com.newnanmanager.generated.apis.MonitorServiceApi
import com.newnanmanager.generated.apis.PlayerServerServiceApi
import com.newnanmanager.generated.apis.PlayerServiceApi
import com.newnanmanager.generated.apis.ServerServiceApi
import com.newnanmanager.generated.apis.TokenServiceApi
import com.newnanmanager.generated.apis.TownServiceApi
import okhttp3.Call

/** Generated endpoint clients sharing the facade's configured transport. */
internal class GeneratedApiSet(
    baseUrl: String,
    callFactory: Call.Factory
) {
    val health = HealthApi(baseUrl, callFactory)
    val players = PlayerServiceApi(baseUrl, callFactory)
    val servers = ServerServiceApi(baseUrl, callFactory)
    val towns = TownServiceApi(baseUrl, callFactory)
    val tokens = TokenServiceApi(baseUrl, callFactory)
    val ips = IPServiceApi(baseUrl, callFactory)
    val playerServers = PlayerServerServiceApi(baseUrl, callFactory)
    val monitor = MonitorServiceApi(baseUrl, callFactory)
}
