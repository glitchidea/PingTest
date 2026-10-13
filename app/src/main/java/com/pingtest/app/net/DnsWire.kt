package com.pingtest.app.net

import java.io.ByteArrayOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Root gerektirmeyen, ham (raw) UDP DNS sorgusu.
 * Bir DNS sunucusuna gerçek bir A kaydı sorgusu gönderip yanıt süresini ölçer.
 */
object DnsWire {

    data class Answer(val rttMs: Double, val aRecord: String?)

    /**
     * [server] adresindeki çözümleyiciye [name] için A kaydı sorar.
     * @throws java.net.SocketTimeoutException yanıt gelmezse
     */
    fun query(server: String, name: String, timeoutMs: Int): Answer {
        val id = (System.nanoTime() and 0xFFFF).toInt()
        val query = buildQuery(id, name)
        val start = System.nanoTime()
        DatagramSocket().use { sock ->
            sock.soTimeout = timeoutMs
            val addr = InetAddress.getByName(server)
            sock.send(DatagramPacket(query, query.size, addr, 53))
            val buf = ByteArray(2048)
            val resp = DatagramPacket(buf, buf.size)
            sock.receive(resp)
            val rtt = (System.nanoTime() - start) / 1_000_000.0

            if (resp.length < 12) throw IllegalStateException("Kısa DNS yanıtı")
            val rid = ((buf[0].toInt() and 0xFF) shl 8) or (buf[1].toInt() and 0xFF)
            if (rid != id) throw IllegalStateException("DNS kimliği uyuşmuyor")
            val flags = ((buf[2].toInt() and 0xFF) shl 8) or (buf[3].toInt() and 0xFF)
            val qr = (flags shr 15) and 1
            val rcode = flags and 0x0F
            if (qr != 1) throw IllegalStateException("DNS yanıtı değil")
            if (rcode != 0) throw IllegalStateException("DNS hata kodu=$rcode")

            val a = parseFirstA(buf, resp.length)
            return Answer(rtt, a)
        }
    }

    private fun buildQuery(id: Int, name: String): ByteArray {
        val out = ByteArrayOutputStream()
        out.write((id shr 8) and 0xFF)
        out.write(id and 0xFF)
        out.write(0x01); out.write(0x00) // RD (özyineleme istendi)
        out.write(0x00); out.write(0x01) // QDCOUNT
        out.write(0x00); out.write(0x00) // ANCOUNT
        out.write(0x00); out.write(0x00) // NSCOUNT
        out.write(0x00); out.write(0x00) // ARCOUNT
        for (part in name.trim('.').split('.')) {
            val bytes = part.toByteArray(Charsets.US_ASCII)
            if (bytes.isEmpty() || bytes.size > 63) throw IllegalArgumentException("Geçersiz DNS adı")
            out.write(bytes.size)
            out.write(bytes)
        }
        out.write(0x00)
        out.write(0x00); out.write(0x01) // QTYPE = A
        out.write(0x00); out.write(0x01) // QCLASS = IN
        return out.toByteArray()
    }

    /** Yanıt gövdesinden ilk A kaydını (IPv4) çıkarır. */
    private fun parseFirstA(buf: ByteArray, len: Int): String? {
        var pos = 12
        val qd = ((buf[4].toInt() and 0xFF) shl 8) or (buf[5].toInt() and 0xFF)
        val an = ((buf[6].toInt() and 0xFF) shl 8) or (buf[7].toInt() and 0xFF)

        // Soruları atla
        repeat(qd) {
            pos = skipName(buf, pos)
            pos += 4
        }
        // Cevaplar
        repeat(an) {
            if (pos >= len) return null
            pos = skipName(buf, pos)
            if (pos + 10 > len) return null
            val type = ((buf[pos].toInt() and 0xFF) shl 8) or (buf[pos + 1].toInt() and 0xFF)
            val rdLen = ((buf[pos + 8].toInt() and 0xFF) shl 8) or (buf[pos + 9].toInt() and 0xFF)
            val rdPos = pos + 10
            if (type == 1 && rdLen == 4 && rdPos + 4 <= len) {
                return "${buf[rdPos].toInt() and 0xFF}.${buf[rdPos + 1].toInt() and 0xFF}." +
                    "${buf[rdPos + 2].toInt() and 0xFF}.${buf[rdPos + 3].toInt() and 0xFF}"
            }
            pos = rdPos + rdLen
        }
        return null
    }

    /** Sıkıştırma işaretçilerini de hesaba katarak bir DNS adını atlar. */
    private fun skipName(buf: ByteArray, start: Int): Int {
        var p = start
        while (p < buf.size) {
            val b = buf[p].toInt() and 0xFF
            if (b == 0) return p + 1
            if (b and 0xC0 == 0xC0) return p + 2 // işaretçi
            p += 1 + b
        }
        return p
    }
}
