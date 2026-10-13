package com.pingtest.app.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Aktif ağın taşıma tipi (dile göre etiketlenir). */
enum class Transport { WIFI, CELLULAR, VPN, NONE, UNKNOWN }

/** Anlık bağlantı durumu: VPN açık mı, alttaki taşıma nedir. */
data class NetSnapshot(
    val vpn: Boolean,
    val transport: Transport
)

/**
 * Aktif ağın taşıma tipini (Wi-Fi / Mobil / Ethernet) ve herhangi bir VPN
 * tünelinin açık olup olmadığını okur. VPN açıkken tüm trafik tünelden
 * geçtiği için ölçülen gecikme hedefe değil VPN yoluna aittir.
 */
object NetStateReader {

    @Suppress("DEPRECATION")
    fun read(context: Context): NetSnapshot {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return NetSnapshot(false, Transport.UNKNOWN)

        var vpn = false
        var wifiOrEth = false
        var cellular = false

        try {
            for (network in cm.allNetworks) {
                val caps = cm.getNetworkCapabilities(network) ?: continue
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    vpn = true
                    continue
                }
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                ) wifiOrEth = true
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) cellular = true
            }
        } catch (_: Exception) {
            // Bazı özel ROM'larda allNetworks kısıtlı olabilir; sessizce devam.
        }

        val transport = when {
            wifiOrEth -> Transport.WIFI
            cellular -> Transport.CELLULAR
            vpn -> Transport.VPN
            else -> Transport.NONE
        }
        return NetSnapshot(vpn = vpn, transport = transport)
    }
}
