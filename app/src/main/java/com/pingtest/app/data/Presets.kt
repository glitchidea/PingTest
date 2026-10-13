package com.pingtest.app.data

import com.pingtest.app.model.Target
import com.pingtest.app.model.TestMethod

/**
 * Hazır hedef şablonları ve serbest metinden hedef çözümleme.
 * Menüler bu şablonlardan oluşturulur (modüler yapı).
 */
object Presets {

    /** Bir şablon: kullanıcı bunu bir menüye koyabilir veya doğrudan test edebilir. */
    data class Template(val key: String, val label: String, val items: List<Target>)

    // ---------------------------------------------------------------- Arama motorları
    val searchEngines: List<Target> = listOf(
        "Google|google.com", "Google (TR)|google.com.tr", "Yandex|yandex.com", "Yandex (RU)|yandex.ru",
        "DuckDuckGo|duckduckgo.com", "Bing|bing.com", "Brave Search|search.brave.com",
        "Yahoo|search.yahoo.com", "Baidu|baidu.com", "Startpage|startpage.com", "Ecosia|ecosia.org",
        "Qwant|qwant.com", "Mojeek|mojeek.com", "MetaGer|metager.org", "Swisscows|swisscows.com",
        "Kagi|kagi.com", "Perplexity|perplexity.ai", "Yep|yep.com", "Marginalia|search.marginalia.nu",
        "SearXNG|searx.be"
    ).http("Arama motorları")

    // ---------------------------------------------------------------- Tarayıcılar
    val browsers: List<Target> = listOf(
        "Mozilla|mozilla.org", "Firefox|firefox.com", "Google Chrome|chrome.google.com",
        "Microsoft Edge|microsoftedge.microsoft.com", "Opera|opera.com", "Vivaldi|vivaldi.com",
        "Brave|brave.com", "Apple Safari|apple.com", "Arc|arc.net", "Chromium|chromium.org",
        "Tor Project|torproject.org", "UC Browser|ucweb.com", "Samsung Internet|samsung.com",
        "Epic Privacy|epicbrowser.com", "Maxthon|maxthon.com"
    ).http("Tarayıcılar")

    // ---------------------------------------------------------------- Popüler siteler
    val popularSites: List<Target> = listOf(
        "Wikipedia|wikipedia.org", "YouTube|youtube.com", "GitHub|github.com", "GitLab|gitlab.com",
        "Stack Overflow|stackoverflow.com", "Reddit|reddit.com", "X (Twitter)|x.com",
        "Facebook|facebook.com", "Instagram|instagram.com", "TikTok|tiktok.com",
        "LinkedIn|linkedin.com", "Pinterest|pinterest.com", "Telegram|telegram.org",
        "Discord|discord.com", "WhatsApp|whatsapp.com", "Netflix|netflix.com", "Spotify|spotify.com",
        "Twitch|twitch.tv", "Amazon|amazon.com", "eBay|ebay.com", "AliExpress|aliexpress.com",
        "Trendyol|trendyol.com", "Hepsiburada|hepsiburada.com", "n11|n11.com",
        "Sahibinden|sahibinden.com", "Medium|medium.com", "Quora|quora.com",
        "Cloudflare|cloudflare.com", "ChatGPT|chatgpt.com", "Claude|claude.ai",
        "Gmail|mail.google.com", "Outlook|outlook.com", "IMDb|imdb.com", "Booking|booking.com",
        "Docker Hub|hub.docker.com", "Archive.org|archive.org"
    ).http("Popüler siteler")


    // ---------------------------------------------------------------- VPS sağlayıcıları
    // Ülkeye göre gruplanır: "VPS · <Ülke>". Liste genişletilebilir.
    /** Ülke bazlı VPS/hosting sağlayıcıları. */
    private data class VpsCountry(val flag: String, val label: String, val items: List<String>)

    private val vpsData: List<VpsCountry> = listOf(
        VpsCountry("🇹🇷", "Türkiye", listOf(
            "Turhost|turhost.com", "Natro|natro.com", "Radore|radore.com",
            "Netdirekt|netdirekt.com.tr", "Çizgi Telekom|cizgi.net.tr", "Veridyen|veridyen.com",
            "Vargonen|vargonen.com", "PlusClouds|plusclouds.com", "Simyacı|simyaci.com",
            "DGN Teknoloji|dgn.net.tr", "Kayra Hosting|kayrahosting.com", "Sunucun|sunucun.com.tr",
            "Uzman Hosting|uzmanhosting.com", "Hosting Dünyası|hostingdunyasi.com", "İHS Telekom|ihs.com.tr",
            "Hostum|hostum.com", "Sadukey|sadukey.com", "Güzel Hosting|guzelhosting.com",
            "İstanbul DC|istanbuldc.com", "Medianova|medianova.com", "Ticimax|ticimax.com",
            "Atak Domain|atakdomain.com", "İsim Kayıt|isimkayit.com", "Nokta Hosting|noktahosting.com",
            "Güncel Hosting|guncelhosting.com", "Kare Hosting|karehosting.com", "Aerohost|aerohost.com",
            "Hostixo|hostixo.com", "Sadece Hosting|sadecehosting.com", "Poyraz Hosting|poyrazhosting.com",
            "Markum|markum.net", "Bursa Hosting|bursahosting.com", "Kalkan Hosting|kalkanhosting.com",
            "Netpia|netpia.com", "Veri Merkezi|verimerkezi.net", "Data Telekom|datatelekom.com.tr",
            "Türk Telekom|turktelekom.com.tr", "Superonline|superonline.net", "Vodafone TR|vodafone.com.tr",
            "TurkNet|turk.net", "Hostinger TR|hostinger.com.tr", "Hosting.com.tr|hosting.com.tr",
            "Fups|fups.com", "Kurumsal Hosting|kurumsalhosting.com", "Server Plus|serverplus.com.tr",
            "Beyaz Host|beyazhost.com", "Trabzon Host|trabzonhost.com", "Ege Hosting|egehosting.com",
            "Ankara Host|ankarhost.com", "İzmir Host|izmirhost.com", "Güney Host|guneyhost.com",
            "Ekol Host|ekolhost.com", "Ova Hosting|ovahosting.com", "Hosting Vitrini|hostingvitrini.com",
            "Turkticaret|turkticaret.net", "İdea Hosting|ideahosting.com"
        )),
        VpsCountry("🇩🇪", "Almanya", listOf(
            "Hetzner|hetzner.com", "Contabo|contabo.com", "IONOS|ionos.de",
            "Netcup|netcup.de", "Strato|strato.de", "All-Inkl|all-inkl.com",
            "Manitu|manitu.de", "myloc|myloc.de", "IP-Projects|ip-projects.de",
            "Webtropia|webtropia.com", "dogado|dogado.de", "PlusServer|plusserver.com",
            "gridscale|gridscale.io", "Alfahosting|alfahosting.de", "Serverdiscounter|serverdiscounter.de",
            "Profihost|profihost.com", "Timme Hosting|timmehosting.de", "Domainfactory|domainfactory.de",
            "Goneo|goneo.de", "Hosteurope|hosteurope.de", "Webgo|webgo.de",
            "Checkdomain|checkdomain.de", "Telekom|telekom.de", "Server4You|server4you.de",
            "Schlundtech|schlundtech.de", "Hosting.de|hosting.de", "Deinserverhost|deinserverhost.de",
            "Onyxhost|onyxhost.de", "Netdirekt DE|netdirekt.de", "Arvato|arvato.com",
            "InterNetX|internetx.com", "Regfish|regfish.de", "Speedbone|speedbone.de",
            "Netsign|netsign.de", "Sysprovide|sysprovide.de", "First Colo|first-colo.net",
            "Mivitec|mivitec.net", "RSN|rsn.de"
        )),
        VpsCountry("🇳🇱", "Hollanda", listOf(
            "TransIP|transip.nl", "LeaseWeb|leaseweb.com", "Serverius|serverius.net",
            "CloudVPS|cloudvps.com", "Antagonist|antagonist.nl", "Nedzone|nedzone.nl",
            "Versio|versio.nl", "Yourhosting|yourhosting.nl", "PCextreme|pcextreme.nl",
            "Hostnet|hostnet.nl", "Combahton|combahton.nl", "Denit|denit.nl",
            "Proserve|proserve.nl", "BIT|bit.nl", "Duocast|duocast.nl",
            "Vimexx|vimexx.nl", "Neostrada|neostrada.nl", "Argeweb|argeweb.nl",
            "Ziggo|ziggo.nl", "KPN|kpn.com", "Solcon|solcon.nl",
            "Signet|signet.nl", "Weserv|weserv.nl", "Mijn.host|mijn.host",
            "Hostinger NL|hostinger.nl", "LeaseWeb NL|leaseweb.nl", "NForce|nforce.com",
            "Fusa|fusa.nl", "OpenNode|opennode.nl"
        )),
        VpsCountry("🇬🇧", "İngiltere", listOf(
            "Krystal|krystal.io", "Fasthosts|fasthosts.co.uk", "UK2|uk2.net",
            "Gandi|gandi.net", "Bytemark|bytemark.co.uk", "Memset|memset.com",
            "BT|bt.com", "Heart Internet|heartinternet.uk", "34SP|34sp.com",
            "Tsohost|tsohost.com", "Kualo|kualo.com", "Netcetera|netcetera.co.uk",
            "iomart|iomart.com", "Pulsant|pulsant.com", "Redstation|redstation.com",
            "Nativespace|nativespace.co.uk", "Namesco|names.co.uk", "EasySpace|easyspace.com",
            "Easyspace|easyspace.com", "Daily|daily.co.uk", "One.com UK|one.com",
            "Ionos UK|ionos.co.uk", "Guru|guru.co.uk", "Hosting UK|hostinguk.net",
            "Melbourne|melbourne.co.uk", "Netshop|netshop.co.uk", "Cyber Host Pro|cyberhostpro.com",
            "Nimbus Hosting|nimbushosting.co.uk"
        )),
        VpsCountry("🇫🇷", "Fransa", listOf(
            "OVHcloud|ovhcloud.com", "Scaleway|scaleway.com", "Ikoula|ikoula.com",
            "EX2|ex2.com", "FirstHeberg|firstheberg.com", "PulseHeberg|pulseheberg.com",
            "LWS|lws.fr", "Infomaniak|infomaniak.com", "o2switch|o2switch.fr",
            "PlanetHoster|planethoster.com", "Nexylan|nexylan.com", "Hosteur|hosteur.com",
            "Clever Cloud|clever-cloud.com", "Alwaysdata|alwaysdata.com", "Gandi|gandi.net",
            "Ikoula|ikoula.fr", "Ikoula FR|ikoula.fr", "HetrixTools|hetrixtools.com",
            "Hostinger FR|hostinger.fr", "Ionos FR|ionos.fr", "Sivit|sivit.fr",
            "Typhon|typhon.fr", "Claranet FR|claranet.fr", "Prosodie|prosodie.com",
            "Clouder|clouder.com", "SIVIT|sivit.com"
        )),
        VpsCountry("🇮🇹", "İtalya", listOf(
            "Aruba|aruba.it", "Register.it|register.it", "Netsons|netsons.com",
            "Seeweb|seeweb.it", "Serverplan|serverplan.com", "Tophost|tophost.it",
            "Keliweb|keliweb.it", "Irideos|irideos.it", "Link.it|link.it",
            "Dominiok|dominiok.it", "Mondonet|mondonet.it", "Host.it|host.it",
            "Technorail|technorail.it", "Prometeus|prometeus.net", "Micso|micso.it",
            "Erdcom|erdcom.it", "Netwoods|netwoods.it", "Teknoservice|teknoservice.it",
            "Server24|server24.eu", "HostingSolutions|hostingsolutions.it", "Hosting Italy|hostingitaly.it",
            "Internet.bs|internet.bs", "HZone|hzone.it", "Sered|sered.net",
            "Nuxit|nuxit.com"
        )),
        VpsCountry("🇪🇸", "İspanya", listOf(
            "Raiola Networks|raiolanetworks.es", "Webempresa|webempresa.com", "Acens|acens.com",
            "Dinahosting|dinahosting.com", "Profesional Hosting|profesionalhosting.com", "Hostalia|hostalia.com",
            "Nominalia|nominalia.com", "Arsys|arsys.es", "CDmon|cdmon.com",
            "DonDominio|dondominio.com", "Iberweb|iberweb.com", "Ionos ES|ionos.es",
            "Hostinger ES|hostinger.es", "Sered ES|sered.net", "Axarnet|axarnet.es",
            "Sered|sered.net", "Web Hosting España|webhosting.es", "DonWeb ES|donweb.com"
        )),
        VpsCountry("🇵🇹", "Portekiz", listOf(
            "Amen|amen.pt", "PTisp|ptisp.pt", "Claranet|claranet.pt",
            "Webhs|webhs.pt", "Hostpoint|hostpoint.pt", "Fccn|fccn.pt"
        )),
        VpsCountry("🇧🇪", "Belçika", listOf(
            "Combell|combell.com", "one.com|one.com", "Versio BE|versio.be",
            "Hostbasket|hostbasket.com", "Belgian Cloud|belgiancloud.be", "Antagonist BE|antagonist.be"
        )),
        VpsCountry("🇱🇺", "Lüksemburg", listOf(
            "Gcore|gcore.com", "Root|root.lu", "Webtasy|webtasy.com"
        )),
        VpsCountry("🇨🇭", "İsviçre", listOf(
            "Infomaniak|infomaniak.com", "Hostpoint|hostpoint.ch", "Cyon|cyon.ch",
            "Swisscom|swisscom.ch", "Nine|nine.ch", "Exoscale|exoscale.com",
            "Hoststar|hoststar.ch", "Metanet|metanet.ch", "Green|green.ch"
        )),
        VpsCountry("🇦🇹", "Avusturya", listOf(
            "World4You|world4you.com", "Easyname|easyname.com", "A1|a1.net",
            "Nessus|nessus.at", "Domaintechnik|domaintechnik.at", "Hostprofis|hostprofis.at",
            "Wienhost|wienhost.at", "eTel|etel.at"
        )),
        VpsCountry("🇮🇪", "İrlanda", listOf(
            "Blacknight|blacknight.com", "Hosting Ireland|hostingireland.ie", "Novi|novi.ie",
            "Register365|register365.com", "Digiweb|digiweb.ie", "Host Ireland|hostireland.ie"
        )),
        VpsCountry("🇵🇱", "Polonya", listOf(
            "home.pl|home.pl", "dhosting|dhosting.pl", "LH.pl|lh.pl",
            "Zenbox|zenbox.pl", "MyDevil|mydevil.net", "OVHcloud PL|ovhcloud.com",
            "nazwa.pl|nazwa.pl", "Kei.pl|kei.pl", "Hostido|hostido.pl",
            "NetArt|netart.com", "Domeny.pl|domeny.pl"
        )),
        VpsCountry("🇨🇿", "Çekya", listOf(
            "Wedos|wedos.com", "Savana|savana.cz", "MasterDC|masterdc.cz",
            "Cesky Hosting|cesky-hosting.cz", "Zoner|zoner.cz", "Active24|active24.cz",
            "Forpsi|forpsi.com", "Ignum|ignum.cz"
        )),
        VpsCountry("🇸🇰", "Slovakya", listOf(
            "Webhouse|webhouse.sk", "Websupport|websupport.sk", "Exohosting|exohosting.sk",
            "Mojhosting|mojhosting.sk", "Sygnus|sygnus.cz"
        )),
        VpsCountry("🇭🇺", "Macaristan", listOf(
            "Rackhost|rackhost.hu", "DotRoll|dotroll.com", "Tárhely|tarhely.eu",
            "Maxer|maxer.hu", "Forpsi HU|forpsi.hu", "MikroSystem|microsystem.hu"
        )),
        VpsCountry("🇷🇴", "Romanya", listOf(
            "ROHOST|rohost.com", "Claus Web|clausweb.ro", "Gazduire|gazduire.ro",
            "Hosterion|hosterion.ro", "MXHost|mxhost.ro", "Zooku|zooku.ro",
            "IHost|ihost.ro"
        )),
        VpsCountry("🇧🇬", "Bulgaristan", listOf(
            "SuperHosting|superhosting.bg", "ICN|icn.bg", "Jump|jump.bg",
            "Host.bg|host.bg", "Neterra|neterra.net", "Coolbox|coolbox.bg"
        )),
        VpsCountry("🇷🇸", "Sırbistan", listOf(
            "MojHosting|mojhosting.com", "Plus Hosting|plus.rs", "Orion Telekom|oriontelekom.rs",
            "Beotel|beotel.net", "Unlimited|unlimited.rs"
        )),
        VpsCountry("🇭🇷", "Hırvatistan", listOf(
            "Plus Hosting HR|plus.hr", "Orbis|orbis.hr", "Hrvatski Telekom|t.ht.hr",
            "InfoGuard|infoguard.hr"
        )),
        VpsCountry("🇸🇮", "Slovenya", listOf(
            "Si.mobil|simobil.si", "Hostko|hostko.si", "Domovanje|domovanje.com",
            "Arnes|arnes.si"
        )),
        VpsCountry("🇧🇦", "Bosna-Hersek", listOf(
            "BH Telecom|bhtelecom.ba", "Mtel|mtel.ba", "Logosoft|logosoft.ba"
        )),
        VpsCountry("🇲🇰", "Kuzey Makedonya", listOf(
            "Makedonski Telekom|telekom.mk", "Hostmakedonija|hostmakedonija.mk", "Mkhost|mkhost.mk"
        )),
        VpsCountry("🇦🇱", "Arnavutluk", listOf(
            "Abcom|abcom.al", "Host.al|host.al"
        )),
        VpsCountry("🇲🇩", "Moldova", listOf(
            "Alexhost|alexhost.com", "Moldhost|moldhost.md", "StarNet|starnet.md"
        )),
        VpsCountry("🇲🇹", "Malta", listOf(
            "Bell Net|bell.net.mt", "Melita|melita.com", "GO|go.com.mt"
        )),
        VpsCountry("🇨🇾", "Kıbrıs", listOf(
            "Cyta|cyta.com.cy", "PrimeTel|primetel.com.cy", "Hostcyprus|hostcyprus.com"
        )),
        VpsCountry("🇬🇷", "Yunanistan", listOf(
            "Papaki|papaki.gr", "Top Host|top.host", "HostMySite|hostmysite.gr",
            "Netsteps|netsteps.gr", "Cretaforce|cretaforce.gr", "Interworks|interworks.gr",
            "Forthnet|forthnet.gr", "Cosmote|cosmote.gr", "Wind Hellas|wind.gr",
            "Hyperhost|hyperhost.gr", "Greekhost|greekhost.gr", "Enartia|enartia.com",
            "Cyclades|cyclades.gr", "Hostgreece|hostgreece.gr", "Web Hosting Greece|webhosting.gr",
            "OTEnet|otenet.gr", "Greece Hosting|greecehosting.gr", "Hostme|hostme.gr",
            "Alpha Hosting|alphahosting.gr"
        )),
        VpsCountry("🇺🇦", "Ukrayna", listOf(
            "HostIQ|hostiq.ua", "Zomro|zomro.com", "Fozzy|fozzy.com",
            "Mirohost|mirohost.net", "Ukraine.com.ua|ukraine.com.ua", "Cityhost|cityhost.ua",
            "Ht.ua|ht.ua", "Deltahost|deltahost.ua", "Andromeda|andromeda.ua",
            "Volia|volia.com", "Kyivstar|kyivstar.ua", "HostPro|hostpro.com.ua",
            "Zomro UA|zomro.com.ua", "Gigacloud|gigacloud.ua", "Freehost|freehost.com.ua",
            "Ukrhost|ukrhost.com"
        )),
        VpsCountry("🇷🇺", "Rusya", listOf(
            "Selectel|selectel.ru", "Timeweb|timeweb.ru", "REG.RU|reg.ru",
            "Beget|beget.com", "FirstVDS|firstvds.ru", "VDSina|vdsina.ru",
            "RUVDS|ruvds.com", "Sprinthost|sprinthost.ru", "Yandex Cloud|yandex.cloud",
            "VK Cloud|cloud.vk.com", "Ihor|ihor.ru", "Fornex|fornex.com",
            "Masterhost|masterhost.ru", "SpaceWeb|sweb.ru", "Advanta|advanta.org",
            "FastVPS|fastvps.ru", "Justhost|justhost.ru", "Miran|miran.ru",
            "2Domains|2domains.ru", "ABCD Hosting|abcdhosting.ru", "Hoster.ru|hoster.ru",
            "Zenon|zenon.net", "ITL|itl.ru", "Sweb|sweb.ru",
            "HostLand|hostland.ru", "Fozzy RU|fozzy.ru", "Oblako|oblako.ru",
            "Cloudlite|cloudlite.ru", "VDSina RU|vdsina.com"
        )),
        VpsCountry("🇧🇾", "Belarus", listOf(
            "HostFly|hostfly.by", "Hoster.by|hoster.by", "Domain.by|domain.by",
            "ActiveCloud|activecloud.by"
        )),
        VpsCountry("🇱🇹", "Litvanya", listOf(
            "Hostinger|hostinger.com", "Serveriai|serveriai.lt", "Cloud.lt|cloud.lt",
            "Duomenucentras|duomenucentras.lt", "IV|iv.lt"
        )),
        VpsCountry("🇱🇻", "Letonya", listOf(
            "Nano IT|nanoit.lv", "Latnet|latnet.lv", "Hosting.lv|hosting.lv",
            "Dattatec|dattatec.lv"
        )),
        VpsCountry("🇪🇪", "Estonya", listOf(
            "Zone|zone.ee", "Veebimajutus|veebimajutus.ee", "Elkdata|elkdata.ee",
            "Hostinger EE|hostinger.com"
        )),
        VpsCountry("🇫🇮", "Finlandiya", listOf(
            "Louhi|louhi.fi", "Nebula|nebula.fi", "Zoner FI|zoner.fi",
            "Planeetta|planeetta.fi", "Heteml FI|heteml.fi", "Hostingpalvelu|hostingpalvelu.fi"
        )),
        VpsCountry("🇸🇪", "İsveç", listOf(
            "Bahnhof|bahnhof.se", "GleSYS|glesys.se", "Binero|binero.se",
            "Loopia|loopia.se", "One.com SE|one.com", "City Network|citynetwork.se",
            "Oderland|oderland.se", "FS Data|fsdata.se", "PRQ|prq.se"
        )),
        VpsCountry("🇳🇴", "Norveç", listOf(
            "Domeneshop|domeneshop.no", "ProISP|proisp.no", "Syse|syse.no",
            "Webhuset|webhuset.no", "Unoeuro|unoeuro.com", "Host Norway|host.no"
        )),
        VpsCountry("🇩🇰", "Danimarka", listOf(
            "Curanet|curanet.dk", "Simply|simply.com", "DanDomain|dandomain.dk",
            "Unoeuro DK|unoeuro.com", "Meebox|meebox.net", "ScanNet|scannet.dk"
        )),
        VpsCountry("🇮🇸", "İzlanda", listOf(
            "1984|1984.is", "Origo|origo.is", "Hysing|hysing.is",
            "FlokiNET|flokinet.is"
        )),
        VpsCountry("🇮🇶", "Irak", listOf(
            "Earthlink|earthlink.iq", "Mobinet|mobinet.iq", "Newroz Telecom|newroztelecom.com",
            "Korek Telecom|korektel.com", "Asiacell|asiacell.com", "Zain Iraq|iraq.zain.com",
            "IQ Hosting|iqhosting.net", "Rasan|rasan.net", "FastNet|fastnet.iq",
            "TigrisNet|tigrisnet.net", "IraqServer|iraqserver.net", "Delta Telecom|delta.iq",
            "Dijlah|dijlah.net", "Alfa Iraq|alfa.iq", "ScopeSky|scopesky.iq",
            "Fibernet|fibernet.iq", "Speednet|speednet.iq", "Iraq Hosting|iraqhosting.net",
            "Kurdistan Host|kurdistanhost.com", "Barzani Host|barzanihost.com"
        )),
        VpsCountry("🇮🇷", "İran", listOf(
            "Pars Pack|parspack.com", "Iran Server|iranserver.com", "Mihan Web Host|mihanwebhost.com",
            "Afranet|afranet.com", "IRANSERVER|iranserver.com", "Netafraz|netafraz.com"
        )),
        VpsCountry("🇸🇦", "Suudi Arabistan", listOf(
            "STC|stc.com.sa", "Mobily|mobily.com.sa", "Saudi Cloud|saudicloud.sa",
            "NourNet|nour.net.sa", "Etihad Atheeb|go.com.sa"
        )),
        VpsCountry("🇦🇪", "BAE", listOf(
            "Moro|moro.ae", "Emirates Internet|emirates.net.ae", "Etisalat|etisalat.ae",
            "Injazat|injazat.com", "eHosting DataFort|ehdf.com", "Raqmiyat|raqmiyat.com",
            "Amazon AWS ME|aws.amazon.com", "Oracle ME|oracle.com", "Microsoft Azure ME|azure.microsoft.com",
            "Alibaba Cloud ME|alibabacloud.com", "Hostinger AE|hostinger.ae", "Emirates Hosting|emirateshosting.com"
        )),
        VpsCountry("🇶🇦", "Katar", listOf(
            "Ooredoo|ooredoo.qa", "Vodafone QA|vodafone.qa", "MEEZA|meeza.com.qa"
        )),
        VpsCountry("🇰🇼", "Kuveyt", listOf(
            "Zain KW|kw.zain.com", "Kuwait Hosting|kuwaithosting.com", "Gulfnet|gulfnet.com.kw"
        )),
        VpsCountry("🇧🇭", "Bahreyn", listOf(
            "Batelco|batelco.com", "Zain BH|bh.zain.com"
        )),
        VpsCountry("🇴🇲", "Umman", listOf(
            "Omantel|omantel.om", "Ooredoo OM|ooredoo.om", "Gulfcyber|gulfcyber.com"
        )),
        VpsCountry("🇯🇴", "Ürdün", listOf(
            "Orange JO|orange.jo", "Zain JO|jo.zain.com", "Jordan Hosting|jordanhosting.com"
        )),
        VpsCountry("🇱🇧", "Lübnan", listOf(
            "Ogero|ogero.gov.lb", "IDM|idm.net.lb", "Cyberia|cyberia.net.lb"
        )),
        VpsCountry("🇮🇱", "İsrail", listOf(
            "Hostpro|hostpro.co.il", "LiveDNS|livedns.co.il", "Kamatera|kamatera.com",
            "Bezeq|bezeq.co.il", "MedOne|medone.co.il", "UI|ui.co.il",
            "Azure IL|azure.microsoft.com", "AWS IL|aws.amazon.com", "Google Cloud IL|cloud.google.com",
            "Hostinger IL|hostinger.co.il", "Server IL|server.co.il", "Mizrahi Host|mizrahi-host.co.il"
        )),
        VpsCountry("🇸🇾", "Suriye", listOf(
            "Syriatel|syriatel.sy", "MTN Syria|mtn.sy"
        )),
        VpsCountry("🇾🇪", "Yemen", listOf(
            "YemenNet|yemen.net.ye", "Y Telecom|y.net.ye"
        )),
        VpsCountry("🇪🇬", "Mısır", listOf(
            "TE Data|tedata.net", "Link Egypt|link.com.eg", "Vodafone EG|vodafone.com.eg",
            "Etisal|etisal.com", "Nile Online|nile-online.com", "Hostinger EG|hostinger.com.eg",
            "Egypt Hosting|egypt-hosting.com", "Delta Host|deltahost.com.eg", "Vodafone EG Hosting|vodafone.com.eg"
        )),
        VpsCountry("🇿🇦", "Güney Afrika", listOf(
            "Xneelo|xneelo.co.za", "Afrihost|afrihost.com", "Hetzner SA|hetzner.co.za",
            "Gridhost|gridhost.co.za", "Elitehost|elitehost.co.za", "1-grid|1-grid.com",
            "Cybersmart|cybersmart.co.za", "Hostinger ZA|hostinger.co.za", "AWS Cape Town|aws.amazon.com",
            "Azure SA|azure.microsoft.com", "Host Africa|hostafrica.co.za", "Truehost ZA|truehost.co.za"
        )),
        VpsCountry("🇳🇬", "Nijerya", listOf(
            "Whogohost|whogohost.com", "Qservers|qservers.net", "DomainKing|domainking.ng",
            "Smartweb|smartweb.com.ng", "MainOne|mainone.net"
        )),
        VpsCountry("🇰🇪", "Kenya", listOf(
            "Truehost|truehost.co.ke", "Safaricom|safaricom.co.ke", "HostPinnacle|hostpinnacle.co.ke",
            "Kenya Website Experts|kenyawebsiteexperts.co.ke"
        )),
        VpsCountry("🇬🇭", "Gana", listOf(
            "Ghana Web Hosting|ghanawebhosting.com", "Ecoband|ecoband.net", "iSpace|ispace.com.gh"
        )),
        VpsCountry("🇲🇦", "Fas", listOf(
            "Genious|genious.net", "MTDS|mtds.com", "Hostoweb|hostoweb.com",
            "Atlas Hosting|atlashosting.ma"
        )),
        VpsCountry("🇹🇳", "Tunus", listOf(
            "Topnet|topnet.tn", "Hexabyte|hexabyte.tn", "Globalnet|globalnet.tn"
        )),
        VpsCountry("🇩🇿", "Cezayir", listOf(
            "Algerie Telecom|algerietelecom.dz", "Icosnet|icosnet.com", "Smart Solutions|smartsolutions.dz"
        )),
        VpsCountry("🇱🇾", "Libya", listOf(
            "Libya Telecom|ltc.com.ly", "Almadar|almadar.ly"
        )),
        VpsCountry("🇸🇩", "Sudan", listOf(
            "Sudatel|sudatel.sd", "Zain SD|sd.zain.com"
        )),
        VpsCountry("🇪🇹", "Etiyopya", listOf(
            "Ethio Telecom|ethiotelecom.et", "Zergaw|zergaw.com"
        )),
        VpsCountry("🇹🇿", "Tanzanya", listOf(
            "Raha|raha.co.tz", "HostPinnacle TZ|hostpinnacle.co.tz", "Extreme Web|extremeweb.co.tz"
        )),
        VpsCountry("🇺🇬", "Uganda", listOf(
            "MTN UG|mtn.co.ug", "Hostalite|hostalite.com"
        )),
        VpsCountry("🇿🇼", "Zimbabve", listOf(
            "TelOne|telone.co.zw", "Webdev|webdev.co.zw"
        )),
        VpsCountry("🇿🇲", "Zambiya", listOf(
            "Zamtel|zamtel.zm", "Bongohive|bongohive.co.zm"
        )),
        VpsCountry("🇨🇲", "Kamerun", listOf(
            "Camtel|camtel.cm", "Matrix Telecom|matrix.cm"
        )),
        VpsCountry("🇨🇮", "Fildişi Sahili", listOf(
            "Orange CI|orange.ci", "Afnet|afnet.net"
        )),
        VpsCountry("🇸🇳", "Senegal", listOf(
            "Sonatel|sonatel.sn", "Arc Informatique|arc.sn"
        )),
        VpsCountry("🇦🇴", "Angola", listOf(
            "Unitel|unitel.ao", "Angola Cables|angolacables.co.ao"
        )),
        VpsCountry("🇲🇿", "Mozambik", listOf(
            "TDM|tdm.co.mz", "Vodacom MZ|vodacom.co.mz"
        )),
        VpsCountry("🇨🇳", "Çin", listOf(
            "Alibaba Cloud|alibabacloud.com", "Tencent Cloud|tencentcloud.com", "Huawei Cloud|huaweicloud.com",
            "Baidu Cloud|cloud.baidu.com", "UCloud|ucloud.cn", "QingCloud|qingcloud.com",
            "Kingsoft Cloud|ksyun.com", "West.cn|west.cn", "JD Cloud|jdcloud.com",
            "Wangsu|wangsu.com", "Aliyun|aliyun.com", "Tencent Cloud CN|cloud.tencent.com",
            "Huawei Cloud CN|huaweicloud.com", "Baidu AI Cloud|cloud.baidu.com", "UpYun|upyun.com",
            "NetEase Cloud|netease.com", "Jcloud|jcloud.com", "BGP Hosting|bgp.net"
        )),
        VpsCountry("🇯🇵", "Japonya", listOf(
            "Sakura Internet|sakura.ad.jp", "ConoHa|conoha.jp", "Kagoya|kagoya.com",
            "GMO Cloud|gmocloud.com", "NTT|ntt.com", "Xserver|xserver.ne.jp",
            "Lolipop|lolipop.jp", "Mixhost|mixhost.jp", "StarServer|starserver.jp",
            "WebARENA|webarena.jp", "ABLENET|ablenet.jp", "WADAX|wadax.jp",
            "CoreServer|coreserver.jp", "Value Server|value-server.com", "Raks|raks.jp",
            "IIJ|iij.ad.jp", "KDDI|kddi.com", "JPRS|jprs.co.jp",
            "Value Domain|value-domain.com", "Raks|raks.com", "Zenlogic|zenlogic.jp",
            "NTT PC|nttpc.co.jp", "Fujitsu|fujitsu.com", "Toyota Digital|toyota-digital.com"
        )),
        VpsCountry("🇰🇷", "Güney Kore", listOf(
            "Gabia|gabia.com", "Cafe24|cafe24.com", "KT Cloud|ktcloud.com",
            "Naver Cloud|ncloud.com", "SmileServ|smileserv.com", "Whois|whois.co.kr",
            "Hosting.kr|hosting.kr", "Korea Server|koreaserver.co.kr"
        )),
        VpsCountry("🇭🇰", "Hong Kong", listOf(
            "Alibaba Cloud HK|alibabacloud.com", "HKT|hkt.com", "iAdvantage|iadvantage.net",
            "Pachosting|pachosting.hk", "HostHK|hosthk.com"
        )),
        VpsCountry("🇹🇼", "Tayvan", listOf(
            "Hinet|hinet.net", "Chief|chief.com.tw", "Pumo|pumo.com.tw",
            "Seednet|seed.net.tw", "ServerZoo|serverzoo.com"
        )),
        VpsCountry("🇸🇬", "Singapur", listOf(
            "DigitalOcean SGP|digitalocean.com", "Vultr SGP|vultr.com", "OVHcloud SG|ovhcloud.com",
            "Alibaba Cloud SG|alibabacloud.com", "Singtel|singtel.com", "Exabytes SG|exabytes.sg",
            "Rackspace SG|rackspace.com", "HostSG|hostsg.com"
        )),
        VpsCountry("🇲🇾", "Malezya", listOf(
            "Shinjiru|shinjiru.com", "Exabytes|exabytes.com", "Serverfreak|serverfreak.com",
            "IPSERVERONE|ipserverone.com", "Vodien|vodien.com", "Digital Ocean MY|digitalocean.com"
        )),
        VpsCountry("🇮🇩", "Endonezya", listOf(
            "Biznet|biznetnetworks.com", "IDCloudHost|idcloudhost.com", "Rumahweb|rumahweb.com",
            "Niagahoster|niagahoster.co.id", "Domainesia|domainesia.com", "Masterweb|masterweb.com",
            "Qwords|qwords.com", "Dewaweb|dewaweb.com"
        )),
        VpsCountry("🇹🇭", "Tayland", listOf(
            "Z.com|z.com", "HostAtom|hostatom.com", "Thaihost|thaihost.com",
            "Bangkok Host|bangkokhost.com", "Ahosting|ahosting.co.th", "Readyidc|readyidc.com"
        )),
        VpsCountry("🇻🇳", "Vietnam", listOf(
            "Viettel|viettel.vn", "VNPT|vnpt.vn", "FPT|fpt.vn",
            "PA Vietnam|pavietnam.vn", "Z.com VN|z.com", "MatBao|matbao.net",
            "Tentod|tentod.com"
        )),
        VpsCountry("🇵🇭", "Filipinler", listOf(
            "PLDT|pldt.com", "Globe|globe.com.ph", "Hostinger PH|hostinger.ph",
            "Web.com.ph|web.com.ph"
        )),
        VpsCountry("🇮🇳", "Hindistan", listOf(
            "E2E Networks|e2enetworks.com", "Hostinger IN|hostinger.in", "ResellerClub|resellerclub.com",
            "Kamatera|kamatera.com", "BigRock|bigrock.in", "MilesWeb|milesweb.com",
            "HostGator IN|hostgator.in", "CtrlS|ctrls.in", "ESDS|esds.co.in",
            "Znetlive|znetlive.com", "HostingRaja|hostingraja.in", "GoDaddy IN|godaddy.com",
            "Nettigritty|nettigritty.com", "Web Werks|webwerks.in", "Hostinger IN|hostinger.com",
            "Bluehost IN|bluehost.in", "Hostgator IN|hostgator.in", "Netmagic|netmagic.net",
            "Sify|sify.com", "Radiant|radiant.net.in", "Web Hosting India|webhostingindia.in",
            "Aerohost IN|aerohost.in", "HostMeNow|hostmenow.com"
        )),
        VpsCountry("🇵🇰", "Pakistan", listOf(
            "Nayatel|nayatel.com", "PTCL|ptcl.com.pk", "Hostinger PK|hostinger.pk",
            "RapidCompute|rapidcompute.com"
        )),
        VpsCountry("🇧🇩", "Bangladeş", listOf(
            "ExonHost|exonhost.com", "Hostever|hostever.net", "DianaHost|dianahost.com",
            "BTCL|btcl.gov.bd"
        )),
        VpsCountry("🇱🇰", "Sri Lanka", listOf(
            "LankaHost|lankahost.net", "SLT|slt.lk", "Hostinger LK|hostinger.lk",
            "Web Hosting LK|webhosting.lk"
        )),
        VpsCountry("🇳🇵", "Nepal", listOf(
            "Nepal Hosting|nepalhosting.com", "Web Surfer|websurfer.com.np", "Hostever NP|hostever.net"
        )),
        VpsCountry("🇲🇲", "Myanmar", listOf(
            "Mytel|mytel.com.mm", "Myanma Posts|mpt.com.mm"
        )),
        VpsCountry("🇰🇭", "Kamboçya", listOf(
            "Metfone|metfone.com.kh", "Smart Axiata|smart.com.kh"
        )),
        VpsCountry("🇲🇳", "Moğolistan", listOf(
            "Mobicom|mobicom.mn", "Unitel MN|unitel.mn"
        )),
        VpsCountry("🇰🇿", "Kazakistan", listOf(
            "Kazakhtelecom|telecom.kz", "Beeline KZ|beeline.kz", "PS Internet|ps.kz",
            "Hoster.kz|hoster.kz"
        )),
        VpsCountry("🇺🇿", "Özbekistan", listOf(
            "Uztelecom|uztelecom.uz", "Beeline UZ|beeline.uz", "Uzhosting|uzhosting.uz"
        )),
        VpsCountry("🇦🇿", "Azerbaycan", listOf(
            "Azercell|azercell.com", "Delta Telecom|delta-telecom.net", "AzerTelecom|azertelecom.az"
        )),
        VpsCountry("🇬🇪", "Gürcistan", listOf(
            "Magticom|magticom.ge", "Silknet|silknet.com", "Host Georgia|host.ge"
        )),
        VpsCountry("🇦🇲", "Ermenistan", listOf(
            "Ucom|ucom.am", "ArmenTel|armentel.am"
        )),
        VpsCountry("🇺🇸", "ABD", listOf(
            "DigitalOcean|digitalocean.com", "Linode|linode.com", "Vultr|vultr.com",
            "AWS|aws.amazon.com", "Azure|azure.microsoft.com", "Google Cloud|cloud.google.com",
            "Oracle Cloud|oracle.com", "IBM Cloud|ibm.com", "Rackspace|rackspace.com",
            "Liquid Web|liquidweb.com", "DreamHost|dreamhost.com", "Hostwinds|hostwinds.com",
            "InterServer|interserver.com", "Kamatera|kamatera.com", "Hivelocity|hivelocity.net",
            "Fly.io|fly.io", "Render|render.com", "Railway|railway.app",
            "Vercel|vercel.com", "Netlify|netlify.com", "Heroku|heroku.com",
            "Namecheap|namecheap.com", "GoDaddy|godaddy.com", "SiteGround|siteground.com",
            "Cloudways|cloudways.com", "Bluehost|bluehost.com", "HostGator|hostgator.com",
            "InMotion Hosting|inmotionhosting.com", "A2 Hosting|a2hosting.com", "GreenGeeks|greengeeks.com",
            "ScalaHosting|scalahosting.com", "HostPapa|hostpapa.com", "Sharktech|sharktech.net",
            "ColoCrossing|colocrossing.com", "QuadraNet|quadranet.com", "Psychz|psychz.net",
            "ServerMania|servermania.com", "Fiberhub|fiberhub.com", "HostDime|hostdime.com",
            "RackNerd|racknerd.com", "DataPacket|datapacket.com", "UltaHost|ultahost.com",
            "Hurricane Electric|he.net", "Oracle|oracle.com", "Hostinger US|hostinger.com",
            "Ionos US|ionos.com", "AccuWeb|accuwebhosting.com", "KnownHost|knownhost.com",
            "MDDHosting|mddhosting.com", "Interserver|interserver.net", "BuyVM|buyvm.net",
            "HostHatch|hosthatch.com", "GreenCloud|greencloudvps.com", "Cloudfanatic|cloudfanatic.net",
            "HostUS|hostus.us", "SimpleNode|simplenode.com"
        )),
        VpsCountry("🇨🇦", "Kanada", listOf(
            "OVHcloud CA|ovhcloud.com", "Sherweb|sherweb.com", "iWeb|iweb.com",
            "HostPapa CA|hostpapa.com", "Web Hosting Canada|whc.ca", "CanSpace|canspace.ca",
            "Netfirms|netfirms.com", "Cirrus Hosting|cirrushosting.com", "Amanah|amanah.com"
        )),
        VpsCountry("🇲🇽", "Meksika", listOf(
            "Hostinger MX|hostinger.mx", "Neubox|neubox.com", "Telmex|telmex.com",
            "Akky|akky.com", "HostGator MX|hostgator.mx", "DonWeb MX|donweb.com"
        )),
        VpsCountry("🇧🇷", "Brezilya", listOf(
            "Locaweb|locaweb.com.br", "KingHost|kinghost.com.br", "UOL Host|uolhost.uol.com.br",
            "Magalu Cloud|magalucloud.com", "HostGator BR|hostgator.com.br", "Umbler|umbler.com",
            "RedeHost|redehost.com.br", "Hostinger BR|hostinger.com.br", "Hostoo|hostoo.io",
            "Nexcess BR|nexcess.net", "Hostinger BR|hostinger.com", "Hostinger Brazil|hostinger.com.br",
            "Locaweb Cloud|locaweb.com.br", "Host1|host1.com.br", "Hostinger BRT|hostinger.com.br",
            "Hostmidia|hostmidia.com.br", "Hostinger BH|hostinger.com.br"
        )),
        VpsCountry("🇦🇷", "Arjantin", listOf(
            "DonWeb|donweb.com", "Hostinger AR|hostinger.com.ar", "Wiroos|wiroos.com",
            "Dattatec|dattatec.com"
        )),
        VpsCountry("🇨🇱", "Şili", listOf(
            "Hostinger CL|hostinger.cl", "Nexus|nexus.cl", "Sonda|sonda.com",
            "DonWeb CL|donweb.com"
        )),
        VpsCountry("🇨🇴", "Kolombiya", listOf(
            "Hostinger CO|hostinger.co", "Colombia Hosting|colombiahosting.com.co", "HostDime CO|hostdime.com.co"
        )),
        VpsCountry("🇵🇪", "Peru", listOf(
            "Hostinger PE|hostinger.pe", "Gtd Peru|gtd.com.pe", "HostDime PE|hostdime.com.pe"
        )),
        VpsCountry("🇺🇾", "Uruguay", listOf(
            "Hostinger UY|hostinger.com.uy", "Antel|antel.com.uy"
        )),
        VpsCountry("🇵🇾", "Paraguay", listOf(
            "Copaco|copaco.com.py", "Hostinger PY|hostinger.com.py"
        )),
        VpsCountry("🇧🇴", "Bolivya", listOf(
            "Entel BO|entel.bo", "Hostinger BO|hostinger.com.bo"
        )),
        VpsCountry("🇻🇪", "Venezuela", listOf(
            "Cantv|cantv.com.ve", "Hostinger VE|hostinger.com.ve"
        )),
        VpsCountry("🇬🇹", "Guatemala", listOf(
            "Tigo GT|tigo.com.gt", "Hostinger GT|hostinger.com.gt"
        )),
        VpsCountry("🇨🇷", "Kosta Rika", listOf(
            "ICE|grupoice.com", "Hostinger CR|hostinger.co.cr"
        )),
        VpsCountry("🇵🇦", "Panama", listOf(
            "Cable & Wireless PA|cwpanama.com", "Hostinger PA|hostinger.com.pa"
        )),
        VpsCountry("🇩🇴", "Dominik Cumhuriyeti", listOf(
            "Claro DO|claro.com.do", "Hostinger DO|hostinger.com.do"
        )),
        VpsCountry("🇦🇺", "Avustralya", listOf(
            "VentraIP|ventraip.com.au", "DigitalPacific|digitalpacific.com.au", "Crucial|crucial.com.au",
            "Servers Australia|serversaustralia.com.au", "Panthur|panthur.com.au", "Netregistry|netregistry.com.au",
            "Web24|web24.com.au", "Mammoth|mammoth.com.au", "Zuver|zuver.net.au",
            "TPP Wholesale|tppwholesale.com.au"
        )),
        VpsCountry("🇳🇿", "Yeni Zelanda", listOf(
            "SiteHost|sitehost.nz", "Freeparking|freeparking.co.nz", "Voyager|voyager.nz",
            "1st Domains|1stdomains.co.nz", "Vibe|vibe.co.nz"
        )),
        VpsCountry("🌍", "Global (off-shore)", listOf(
            "RareCloud|rarecloud.io", "DartNode|dartnode.com", "FlokiNET|flokinet.is",
            "Shinjiru|shinjiru.com", "1984 Hosting|1984.is", "PRQ|prq.se",
            "OrangeWebsite|orangewebsite.com", "Njalla|njalla.com", "Bahnhof|bahnhof.se",
            "BuyVM|buyvm.net", "HostHatch|hosthatch.com", "GreenCloud|greencloudvps.com"
        )),
    )

    private fun vpsTargets(c: VpsCountry): List<Target> = c.items.map { entry ->
        val (label, host) = entry.split("|", limit = 2)
        Target(label = label, host = host, method = TestMethod.HTTP, group = "${c.flag} ${c.label}")
    }

    /** Tüm VPS sağlayıcıları (ülkelere göre gruplu). */
    val vps: List<Target> = vpsData.flatMap { vpsTargets(it) }

    /** Ülke başına ayrı şablonlar: "🇹🇷 Türkiye — VPS" ... */
    fun vpsTemplates(): List<Template> = vpsData.map { c ->
        Template("vps_" + c.label, "${c.flag} ${c.label} — VPS", vpsTargets(c))
    }

    // ---------------------------------------------------------------- DNS & genel
    val dns: List<Target> = listOf(
        Target(label = "Cloudflare", host = "1.1.1.1", method = TestMethod.DNS, group = "DNS"),
        Target(label = "Cloudflare Alt", host = "1.0.0.1", method = TestMethod.DNS, group = "DNS"),
        Target(label = "Google", host = "8.8.8.8", method = TestMethod.DNS, group = "DNS"),
        Target(label = "Google Alt", host = "8.8.4.4", method = TestMethod.DNS, group = "DNS"),
        Target(label = "Quad9", host = "9.9.9.9", method = TestMethod.DNS, group = "DNS"),
        Target(label = "OpenDNS", host = "208.67.222.222", method = TestMethod.DNS, group = "DNS"),
        Target(label = "AdGuard", host = "94.140.14.14", method = TestMethod.DNS, group = "DNS"),
        Target(label = "Yandex", host = "77.88.8.8", method = TestMethod.DNS, group = "DNS")
    )

    val common: List<Target> = listOf(
        Target(label = "Cloudflare 1.1.1.1", host = "1.1.1.1", method = TestMethod.AUTO, group = "Genel"),
        Target(label = "Google DNS 8.8.8.8", host = "8.8.8.8", method = TestMethod.AUTO, group = "Genel"),
        Target(label = "Quad9 9.9.9.9", host = "9.9.9.9", method = TestMethod.AUTO, group = "Genel"),
        Target(label = "Google (HTTPS)", host = "google.com", method = TestMethod.HTTP, group = "Genel")
    )

    /** İnternet testi: arama motorları + tarayıcılar + popüler siteler. */
    val internet: List<Target> = searchEngines + browsers + popularSites

    /** Hazır şablonlar (menülere konulabilir). */
    /** Tüm şablonlar — performans için YALNIZ BİR KEZ hesaplanır (lazy). */
    val allTemplates: List<Template> by lazy {
        listOf(
            Template("internet", "🌐 İnternet testi (arama + tarayıcı + siteler)", internet),
            Template("search", "🔎 Arama motorları", searchEngines),
            Template("browsers", "🧭 Tarayıcılar", browsers),
            Template("sites", "⭐ Popüler siteler", popularSites),
            Template("vps", "🗄️ Tüm VPS sağlayıcıları (116 ülke)", vps),
            Template("dns", "📡 DNS sunucuları", dns),
            Template("general", "⚙️ Genel adresler", common)
        ) + vpsTemplates()
    }

    fun preset(kind: String): List<Target> = when (kind) {
        "dns" -> dns
        "common", "general" -> common
        "internet" -> internet
        "search" -> searchEngines
        "browsers" -> browsers
        "sites" -> popularSites
        "vps" -> vps
        else -> dns + common
    }

    /** "Etiket|host" listesini HTTP hedeflerine çevirir. */
    private fun List<String>.http(group: String): List<Target> = map { entry ->
        val (label, host) = entry.split("|", limit = 2)
        Target(label = label, host = host, method = TestMethod.HTTP, group = group)
    }

    /**
     * Serbest metni hedeflere çevirir. Desteklenen satır biçimleri:
     *   8.8.8.8
     *   8.8.8.8:443
     *   1.1.1.1           , Cloudflare
     *   dns://8.8.8.8
     *   tcp://1.2.3.4:443
     *   icmp://1.2.3.4
     *   https://example.com
     *   # ile başlayan satırlar ve " #" sonrası yorumlar yok sayılır.
     */
    fun parse(text: String): List<Target> {
        val out = ArrayList<Target>()
        for (raw in text.split('\n', '\r')) {
            var line = raw.trim()
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue
            val hash = line.indexOf(" #")
            if (hash > 0) line = line.substring(0, hash).trim()

            var endpoint = line
            var label = ""
            val sep = line.indexOf(',')
            val tab = line.indexOf('\t')
            when {
                sep > 0 -> {
                    endpoint = line.substring(0, sep).trim()
                    label = line.substring(sep + 1).trim()
                }
                tab > 0 -> {
                    endpoint = line.substring(0, tab).trim()
                    label = line.substring(tab + 1).trim()
                }
                else -> {
                    val m = Regex("\\s{2,}").find(line)
                    if (m != null) {
                        endpoint = line.substring(0, m.range.first).trim()
                        label = line.substring(m.range.last + 1).trim()
                    }
                }
            }
            if (endpoint.isEmpty()) continue

            var method = TestMethod.AUTO
            var url: String? = null
            var hostPort = endpoint

            val schemeIdx = endpoint.indexOf("://")
            if (schemeIdx > 0) {
                val scheme = endpoint.substring(0, schemeIdx).lowercase()
                val rest = endpoint.substring(schemeIdx + 3)
                when (scheme) {
                    "dns" -> method = TestMethod.DNS
                    "tcp" -> method = TestMethod.TCP
                    "icmp", "ping" -> method = TestMethod.ICMP
                    "http", "https" -> {
                        method = TestMethod.HTTP
                        url = endpoint
                    }
                }
                hostPort = rest
            }
            if (hostPort.isEmpty()) continue

            // dns://ip?zone → DNS sorgu adı (yetkili TLD sunucuları için)
            var queryName: String? = null
            val qIdx = hostPort.indexOf('?')
            if (qIdx >= 0) {
                queryName = hostPort.substring(qIdx + 1).ifBlank { null }
                hostPort = hostPort.substring(0, qIdx)
            }
            if (hostPort.isEmpty()) continue

            var host = hostPort
            var port: Int? = null
            val hostPortNoPath = hostPort.substringBefore('/')
            if (hostPortNoPath.startsWith("[")) {
                val close = hostPortNoPath.indexOf(']')
                if (close > 0) {
                    host = hostPortNoPath.substring(1, close)
                    val after = hostPortNoPath.substring(close + 1)
                    if (after.startsWith(":")) port = after.substring(1).toIntOrNull()
                }
            } else {
                val colon = hostPortNoPath.lastIndexOf(':')
                if (colon > 0 && hostPortNoPath.indexOf(':') == colon) {
                    host = hostPortNoPath.substring(0, colon)
                    port = hostPortNoPath.substring(colon + 1).toIntOrNull()
                } else {
                    host = hostPortNoPath
                }
            }
            host = host.trim()
            if (host.isEmpty()) continue

            out.add(
                Target(
                    label = label.ifBlank { host },
                    host = host,
                    port = port,
                    method = method,
                    url = url,
                    dnsName = queryName
                )
            )
        }
        return out.distinctBy { "${it.method}|${it.host.lowercase()}|${it.port}|${it.label.lowercase()}" }
    }
}
