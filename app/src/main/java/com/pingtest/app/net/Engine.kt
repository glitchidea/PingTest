package com.pingtest.app.net

import com.pingtest.app.model.AppSettings
import com.pingtest.app.model.Status
import com.pingtest.app.model.Target
import com.pingtest.app.model.TestMethod
import com.pingtest.app.model.TestResult

/**
 * Yöntem seçimini ve otomatik (AUTO) denemeyi yöneten motor.
 * AUTO sırası: TCP(443) → TCP(80) → ICMP → (IP ise) DNS. İlk çalışan kazanır.
 */
object Engine {

    private val ipv4 = Regex("^\\d{1,3}(\\.\\d{1,3}){3}$")
    fun looksLikeIp(host: String): Boolean = host.contains(':') || ipv4.matches(host)

    fun run(target: Target, s: AppSettings): TestResult {
        val host = target.host.trim()
        return when (target.method) {
            TestMethod.ICMP -> IcmpTester.test(host, s.count, s.timeoutMs)
            TestMethod.DNS -> DnsTester.test(host, target.dnsName ?: s.dnsQueryName, s.count, s.timeoutMs)
            TestMethod.HTTP -> HttpTester.test(host, target.port, target.url, s.count, s.timeoutMs)
            TestMethod.TCP -> TcpTester.test(host, target.port ?: 443, s.count, s.timeoutMs)
            TestMethod.AUTO -> auto(host, target, s)
        }
    }

    private fun auto(host: String, target: Target, s: AppSettings): TestResult {
        val ports = buildList {
            if (target.port != null) add(target.port)
            else {
                add(443)
                add(80)
            }
        }
        var last: TestResult? = null
        for (p in ports) {
            val r = TcpTester.test(host, p, s.count, s.timeoutMs)
            if (r.reachable) return r
            last = r
        }
        val icmp = IcmpTester.test(host, s.count, s.timeoutMs)
        if (icmp.reachable) return icmp

        if (looksLikeIp(host)) {
            val dns = DnsTester.test(host, target.dnsName ?: s.dnsQueryName, s.count, s.timeoutMs)
            if (dns.reachable) return dns
            if (icmp.status != Status.UNSUPPORTED) return icmp
            return last ?: dns
        }
        return if (icmp.status != Status.UNSUPPORTED) icmp else (last ?: icmp)
    }
}
