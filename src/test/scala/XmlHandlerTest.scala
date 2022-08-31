import org.scalatest.funsuite.AnyFunSuite
import org.scalamock.scalatest.MockFactory

class XmlHandlerTest extends AnyFunSuite with MockFactory {

  private val googleRssNews = "https://news.google.com/rss?hl=de&gl=DE&ceid=DE:de"
  private val googleRssXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                            |<rss version="2.0" xmlns:media="http://search.yahoo.com/mrss/">
                            |<channel><generator>NFE/5.0</generator>
                            |<title>Top-Meldungen - Google News</title>
                            |<link>https://news.google.com/?hl=de&amp;gl=DE&amp;ceid=DE:de</link>
                            |<language>de</language>
                            |<webMaster>news-webmaster@google.com</webMaster>
                            |<copyright>2022 Google Inc.</copyright>
                            |<lastBuildDate>Tue, 19 Jul 2022 20:43:13 GMT</lastBuildDate>
                            |<description>Google News</description>
                            |<item>
                            |<title>Gipfel in Teheran</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMigQFodHRwczovL3d3dy53ZWx0LmRlL3BvbGl0aWsvYXVzbGFuZC9wbHVzMjM5OTc5Mzg1L0dpcGZlbC1pbi1UZWhlcmFuLUpldHp0LXdpdHRlcnQtRXJkb2dhbi1zZWluZS1DaGFuY2UtZGFuay1QdXRpbnMtU2Nod2FlY2hlLmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Nach "Riesenerfolg" des 9-Euro-Tickets: Verkehrsminister Wissing erwägt Nachfolgeticket - n-tv NACHRICHTEN</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMihgFodHRwczovL3d3dy5uLXR2LmRlL3BvbGl0aWsvTmFjaC1SaWVzZW5lcmZvbGctZGVzLTktRXVyby1UaWNrZXRzLVZlcmtlaHJzbWluaXN0ZXItV2lzc2luZy1lcndhZWd0LU5hY2hmb2xnZXRpY2tldC1hcnRpY2xlMjM0NzMwOTQuaHRtbNIBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Nord Stream 1: EU-Kommission rechnet mit dauerhaftem Erdgas-Lieferstopp - heise online</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMibWh0dHBzOi8vd3d3LmhlaXNlLmRlL25ld3MvTm9yZC1TdHJlYW0tMS1FVS1Lb21taXNzaW9uLXJlY2huZXQtbWl0LWRhdWVyaGFmdGVtLUVyZGdhcy1MaWVmZXJzdG9wcC03MTgzNTQxLmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Hitze in Deutschland: Heißester Tag des Jahres - 40 Grad - tagesschau.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiQGh0dHBzOi8vd3d3LnRhZ2Vzc2NoYXUuZGUvaW5sYW5kL2hpdHpld2VsbGUtZGV1dHNjaGxhbmQtMTE3Lmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Nur noch drei Kandidaten für die Johnson-Nachfolge - FAZ - Frankfurter Allgemeine Zeitung</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMicGh0dHBzOi8vd3d3LmZhei5uZXQvYWt0dWVsbC9wb2xpdGlrL2F1c2xhbmQvdG9yaWVzLW51ci1ub2NoLWRyZWkta2FuZGlkYXRlbi1mdWVyLWpvaG5zb24tbmFjaGZvbGdlLTE4MTg0MzQ5Lmh0bWzSAXJodHRwczovL20uZmF6Lm5ldC9ha3R1ZWxsL3BvbGl0aWsvYXVzbGFuZC90b3JpZXMtbnVyLW5vY2gtZHJlaS1rYW5kaWRhdGVuLWZ1ZXItam9obnNvbi1uYWNoZm9sZ2UtMTgxODQzNDkuYW1wLmh0bWw?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Brandherde, Mittagshitze, Lüften: Zwölf Hitzemythen im Faktencheck - n-tv NACHRICHTEN</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiU2h0dHBzOi8vd3d3Lm4tdHYuZGUvcGFub3JhbWEvWndvZWxmLUhpdHplbXl0aGVuLWltLUZha3RlbmNoZWNrLWFydGljbGUyMzQ3NDc1Ni5odG1s0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Der Atomkraft-Deal, den (fast) keiner wollte - FAZ - Frankfurter Allgemeine Zeitung</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMidmh0dHBzOi8vd3d3LmZhei5uZXQvYWt0dWVsbC9wb2xpdGlrL2lubGFuZC9hdG9ta3JhZnQtZ2VnZW4tdGVtcG9saW1pdC1rcml0aWstYW4tdm9yc2NobGFnLXZvbi1qZW5zLXNwYWhuLTE4MTgzNjcyLmh0bWzSAXhodHRwczovL20uZmF6Lm5ldC9ha3R1ZWxsL3BvbGl0aWsvaW5sYW5kL2F0b21rcmFmdC1nZWdlbi10ZW1wb2xpbWl0LWtyaXRpay1hbi12b3JzY2hsYWctdm9uLWplbnMtc3BhaG4tMTgxODM2NzIuYW1wLmh0bWw?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Energiekrise: Christian Lindner will Pendlerpauschale erhöhen | ZEIT ONLINE - zeit.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiaWh0dHBzOi8vd3d3LnplaXQuZGUvcG9saXRpay9kZXV0c2NobGFuZC8yMDIyLTA3L2VuZXJnaWVrcmlzZS1jaHJpc3RpYW4tbGluZG5lci1wZW5kbGVycGF1c2NoYWxlLWVyaG9laHVuZ9IBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Corona-News-Ticker: Hamburg und SH verlängern aktuelle Regeln - NDR.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMie2h0dHBzOi8vd3d3Lm5kci5kZS9uYWNocmljaHRlbi9pbmZvL0Nvcm9uYS1OZXdzLVRpY2tlci1IYW1idXJnLXVuZC1TSC12ZXJsYWVuZ2Vybi1ha3R1ZWxsZS1SZWdlbG4sY29yb25hbGl2ZXRpY2tlcjE4NTAuaHRtbNIBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Petersberger Klimadialog: "Können die Klimakrise nicht aufschieben" | tagesschau.de - tagesschau.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiQ2h0dHBzOi8vd3d3LnRhZ2Vzc2NoYXUuZGUvaW5sYW5kL3BldGVyc2Jlcmdlci1kaWFsb2cta2xpbWEtMTAzLmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Ukraine: Wolodymyr Selenskyj entlässt weitere Geheimdienstoffiziere | ZEIT ONLINE - zeit.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiaGh0dHBzOi8vd3d3LnplaXQuZGUvcG9saXRpay9hdXNsYW5kLzIwMjItMDcvdWtyYWluZS13b2xvZHlteXItc2VsZW5za3lqLWVudGxhc3N1bmctZ2VoZWltZGllbnN0b2ZmaXppZXJl0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Nordmazedonien und Albanien: Prozess für EU-Beitrittsgespräche startet - tagesschau.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiVWh0dHBzOi8vd3d3LnRhZ2Vzc2NoYXUuZGUvYXVzbGFuZC9ldXJvcGEvZXUtYmVpdHJpdHQtbm9yZG1hemVkb25pZW4tYWxiYW5pZW4tMTAxLmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Ukraine droht mit Angriffen auf die Schwarzmeer-Halbinsel Krim - STERN.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMif2h0dHBzOi8vd3d3LnN0ZXJuLmRlL3BvbGl0aWsvYXVzbGFuZC91a3JhaW5lLW5ld3MtLXVrcmFpbmUtZHJvaHQtbWl0LWFuZ3JpZmZlbi1hdWYtZGllLXNjaHdhcnptZWVyLWhhbGJpbnNlbC1rcmltLTMyNTUyMzg0Lmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>40,2 Grad – Großbritannien erlebt heißesten Tag seit Beginn der Aufzeichnungen - Tagesspiegel</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiqgFodHRwczovL3d3dy50YWdlc3NwaWVnZWwuZGUvZ2VzZWxsc2NoYWZ0L2hpdHpld2VsbGUtaW4tZGV1dHNjaGxhbmQtdW5kLWV1cm9wYS00MC0yLWdyYWQtZ3Jvc3Nicml0YW5uaWVuLWVybGVidC1oZWlzc2VzdGVuLXRhZy1zZWl0LWJlZ2lubi1kZXItYXVmemVpY2hudW5nZW4vMjg1MjA0MjAuaHRtbNIBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Nach Bericht über Wiederaufnahme russischer Gaslieferungen: Dax steigt um fast drei Prozent - DER SPIEGEL</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMihgFodHRwczovL3d3dy5zcGllZ2VsLmRlL3dpcnRzY2hhZnQvbm9yZHN0cmVhbS0xLWRheC1zdGVpZ3QtdW0tZmFzdC1kcmVpLXByb3plbnQtbmFjaC1hbmdlYmxpY2hlbS1hLTllZjc1NDQyLWJkMTQtNDlkZC04NzQyLWJhOTlmYWMyMDRlYdIBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Komplettverstaatlichung: Frankreichs Milliardenangebot für EDF | tagesschau.de - tagesschau.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiTWh0dHBzOi8vd3d3LnRhZ2Vzc2NoYXUuZGUvd2lydHNjaGFmdC91bnRlcm5laG1lbi9lZGYtdmVyc3RhYXRsaWNodW5nLTEwMy5odG1s0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>DAX schließt mit deutlichen Aufschlägen -- Offenbar Wiederaufnahme von Nord Stream 1 geplant -- Tesla im Visier von Verbraucherschützern -- DiDi, IBM, Bilfinger, Delivery Hero, AstraZeneca im Fokus - finanzen.net</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiTGh0dHBzOi8vd3d3LmZpbmFuemVuLm5ldC9uYWNocmljaHQvYWt0aWVuL2hldXRlLWltLWZva3VzLTE5LTA3LTIwMjItMTE1NDE3MzHSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Wächter-Modus: Verbraucherzentrale verklagt Tesla - ComputerBase</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiVmh0dHBzOi8vd3d3LmNvbXB1dGVyYmFzZS5kZS8yMDIyLTA3L3dhZWNodGVyLW1vZHVzLXZlcmJyYXVjaGVyemVudHJhbGUtdmVya2xhZ3QtdGVzbGEv0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>„James Webb“-Teleskop von Mikrometeoroid beschädigt – Schaden ist nicht komplett zu korrigieren - fr.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiigFodHRwczovL3d3dy5mci5kZS93aXNzZW4vamFtZXMtd2ViYi10ZWxlc2tvcC1uYXNhLWVzYS11bml2ZXJzdW0tc3BpZWdlbC1iZXNjaGFlZGlndC1laW5zY2hsYWctbWlrcm9tZXRlb3JvaWQtc2NoYWRlbi1uZXdzLXpyLTkxNjc2MzMwLmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Private Raumfahrt: Zwei Raumfahrtunternehmen wollen 2024 zum Mars fliegen - Golem.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMic2h0dHBzOi8vd3d3LmdvbGVtLmRlL25ld3MvcHJpdmF0ZS1yYXVtZmFocnQtendlaS1yYXVtZmFocnR1bnRlcm5laG1lbi13b2xsZW4tMjAyNC16dW0tbWFycy1mbGllZ2VuLTIyMDctMTY3MDAyLmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>"Ruhiges" Schwarzes Loch außerhalb der Milchstraße: Keine Strahlung, nur Anziehungskraft - RND</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMikAFodHRwczovL3d3dy5ybmQuZGUvd2lzc2VuL3J1aGlnZXMtc2Nod2FyemVzLWxvY2gtYXVzc2VyaGFsYi1kZXItbWlsY2hzdHJhc3NlLWtlaW5lLXN0cmFobHVuZy1udXItYW56aWVodW5nc2tyYWZ0LUNKV0lTT0dLN1lLSE9HWVpTRjNKQUJCUDVVLmh0bWzSAaUBaHR0cHM6Ly93d3cucm5kLmRlL3dpc3Nlbi9ydWhpZ2VzLXNjaHdhcnplcy1sb2NoLWF1c3NlcmhhbGItZGVyLW1pbGNoc3RyYXNzZS1rZWluZS1zdHJhaGx1bmctbnVyLWFuemllaHVuZ3NrcmFmdC1DSldJU09HSzdZS0hPR1laU0YzSkFCQlA1VS5odG1sP291dHB1dFR5cGU9dmFsaWRfYW1w?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Blazare: Neutrinofabriken im Weltraum identifiziert - AstroNews</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiPWh0dHBzOi8vd3d3LmFzdHJvbmV3cy5jb20vbmV3cy9hcnRpa2VsLzIwMjIvMDcvMjIwNy0wMTMuc2h0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Ganz natürlich! Cathy Hummels zeigt sich komplett ungeschminkt - VIP.de, Star News</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiYmh0dHBzOi8vd3d3LnZpcC5kZS9jbXMvZ2Fuei1uYXR1ZXJsaWNoLWNhdGh5LWh1bW1lbHMtemVpZ3Qtc2ljaC1rb21wbGV0dC11bmdlc2NobWlua3QtNDk5NzY1OS5odG1s0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Meghan Markle: Sie lächelt die bösen Gerüchte weg – und kopiert Kate Middleton - BUNTE.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMihgFodHRwczovL3d3dy5idW50ZS5kZS9yb3lhbHMvYnJpdGlzY2hlcy1rb2VuaWdzaGF1cy9tZWdoYW4tbWFya2xlLXNpZS1sYWVjaGVsdC1kaWUtYm9lc2VuLWdlcnVlY2h0ZS13ZWctdW5kLWtvcGllcnQta2F0ZS1taWRkbGV0b24uaHRtbNIBigFodHRwczovL3d3dy5idW50ZS5kZS9yb3lhbHMvYnJpdGlzY2hlcy1rb2VuaWdzaGF1cy9tZWdoYW4tbWFya2xlLXNpZS1sYWVjaGVsdC1kaWUtYm9lc2VuLWdlcnVlY2h0ZS13ZWctdW5kLWtvcGllcnQta2F0ZS1taWRkbGV0b24uYW1wLmh0bWw?oc=5</link>
                            |</item>
                            |<item>
                            |<title>»Taxi Teheran«-Regisseur Jafar Panahi: Berlinale-Gewinner in Iran inhaftiert - DER SPIEGEL</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMid2h0dHBzOi8vd3d3LnNwaWVnZWwuZGUva3VsdHVyL2phZmFyLXBhbmFoaS1iZXJsaW5hbGUtZ2V3aW5uZXItaW4taXJhbi1pbmhhZnRpZXJ0LWEtYzBjNjlhMGYtMzQ0Zi00NWExLTg3NzgtMjEzMWE1MjM0YmJm0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Heidi-Klum-Model Céline Bethmann: Mats Hummels? DAS ist mein neuer Schatz! - BILD</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMihgFodHRwczovL3d3dy5iaWxkLmRlL3VudGVyaGFsdHVuZy9sZXV0ZS9sZXV0ZS9oZWlkaS1rbHVtLW1vZGVsLWNsaW5lLWJldGhtYW5uLW1hdHMtaHVtbWVscy1kYXMtaXN0LW1laW4tbmV1ZXItc2NoYXR6LTgwNzQ0MTc0LmJpbGQuaHRtbNIBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>FC Bayern meldet De Ligt-Transfer: Teuerste Innenverteidigung der Welt - Transfermarkt</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMihwFodHRwczovL3d3dy50cmFuc2Zlcm1hcmt0LmRlL2RlLWxpZ3QtdW50ZXJzY2hyZWlidC1pbi1tdW5jaGVuLWZjLWJheWVybi1qZXR6dC1taXQtdGV1ZXJzdGVyLWlubmVudmVydGVpZGlndW5nLWRlci13ZWx0L3ZpZXcvbmV3cy80MDgwMjLSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>FC Bayern: Experte mit deutlichen Worten zu Lewandowski-Zukunft - watson</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiamh0dHBzOi8vd3d3LndhdHNvbi5kZS9zcG9ydC9hbmFseXNlLzk4OTg0ODIzNi1mYy1iYXllcm4tZXhwZXJ0ZS1taXQta2xhcmVyLXByb2dub3NlLXp1LWxld2FuZG93c2tpLXp1a3VuZnTSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Hugo Houle siegt - Simon Geschke glänzt weiter am Berg - Sportschau</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiV2h0dHBzOi8vd3d3LnNwb3J0c2NoYXUuZGUvcmFkc3BvcnQvdG91cmRlZnJhbmNlL3RvdXItZGUtZnJhbmNlLWV0YXBwZS1zZWNoemVobi0xMTAuaHRtbNIBAA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Neuer Stürmer erkrankt: Hodentumor bei BVB-Star Sébastien Haller entdeckt - WELT</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMieWh0dHBzOi8vd3d3LndlbHQuZGUvc3BvcnQvYXJ0aWNsZTIzOTk5MTUxNS9OZXVlci1TdHVlcm1lci1lcmtyYW5rdC1Ib2RlbnR1bW9yLWJlaS1CVkItU3Rhci1TZWJhc3RpZW4tSGFsbGVyLWVudGRlY2t0Lmh0bWzSAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Malaria: Warum es bisher keine dauerhafte Immunität gibt | STERN.de - STERN.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMidGh0dHBzOi8vd3d3LnN0ZXJuLmRlL2dlc3VuZGhlaXQvZ2VzdW5kLWxlYmVuL21hbGFyaWEtLXdhcnVtLWVzLWJpc2hlci1rZWluZS1kYXVlcmhhZnRlLWltbXVuaXRhZXQtZ2lidC0zMjUzMDcwNi5odG1s0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>4 Faktoren gefährden die männliche Fruchtbarkeit - Vital</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiXGh0dHBzOi8vd3d3LnZpdGFsLmRlL2dlc3VuZGhlaXQvNC1mYWt0b3Jlbi1nZWZhZWhyZGVuLWRpZS1tYWVubmxpY2hlLWZydWNodGJhcmtlaXQtNjY0MS5odG1s0gEA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Nordic Walking verbessert Lebensqualität und lindert Depressionen - Heilpraxisnet.de</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMif2h0dHBzOi8vd3d3LmhlaWxwcmF4aXNuZXQuZGUvbmF0dXJoZWlscHJheGlzL25vcmRpYy13YWxraW5nLXZlcmJlc3NlcnQtbGViZW5zcXVhbGl0YWV0LXVuZC1saW5kZXJ0LWRlcHJlc3Npb25lbi0yMDIyMDcxOTU2MzQ1OC_SAQA?oc=5</link>
                            |</item>
                            |<item>
                            |<title>Eis bei Erkältung - gut oder nicht? - CHIP Praxistipps</title>
                            |<link>https://news.google.com/__i/rss/rd/articles/CBMiRWh0dHBzOi8vcHJheGlzdGlwcHMuZm9jdXMuZGUvZWlzLWJlaS1lcmthZWx0dW5nLWd1dC1vZGVyLW5pY2h0XzE0ODQyN9IBUGh0dHBzOi8vcHJheGlzdGlwcHMuZm9jdXMuZGUvZWlzLWJlaS1lcmthZWx0dW5nLWd1dC1vZGVyLW5pY2h0XzE0ODQyNz9sYXlvdXQ9YW1w?oc=5</link>
                            |</item>
                            |</channel>
                            |</rss>""".stripMargin


  test("Correct XML being downloaded") {
    val mockXmlHandler = mock[XmlHandler]
    (mockXmlHandler.downloadXml _).expects(*).returning(Option(googleRssXml)).once()

    val cernDownload = mockXmlHandler.downloadXml(googleRssNews) match {
      case Some(value) => value
      case None => "Failed"
    }

    assert(googleRssXml.split("\n") sameElements cernDownload.split("\n"))
  }


  test("getLinksFromRssFeed produces correct amount of links") {
    val mockXmlHandler = mock[XmlHandler]
    (mockXmlHandler.downloadXml _).expects(*).returning(Option(googleRssXml)).once()
    val xmlHandler = new XmlHandler()

    val googleResponse = mockXmlHandler.downloadXml(googleRssNews)

    val newsLinks: Seq[String] = googleResponse match {
      case Some(value) =>
        xmlHandler.getLinksFromRssFeed(value)
      case None => Seq()
    }

    assert(newsLinks.length === 34)
  }
}
