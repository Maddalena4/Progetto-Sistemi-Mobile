package com.example.cityguest.data

import com.example.cityguest.ui.components.PlaceOfInterest
import com.google.android.gms.maps.model.LatLng

object PoiData {

    val pointsOfInterest = listOf(
        // Luoghi di Roma
        PlaceOfInterest("101", "Colosseo", "L'anfiteatro più famoso del mondo romano.", LatLng(41.8902, 12.4922), 150, "Roma"),
        PlaceOfInterest("102", "Fontana di Trevi", "La celebre fontana barocca.", LatLng(41.9009, 12.4833), 100, "Roma"),
        PlaceOfInterest("103", "Pantheon", "Antico tempio romano ora basilica.", LatLng(41.8986, 12.4769), 120, "Roma"),

        // Luoghi di Verona
        PlaceOfInterest("201", "Arena di Verona", "Anfiteatro romano ancora in uso per l'opera.", LatLng(45.4382, 10.9944), 140, "Verona"),
        PlaceOfInterest("202", "Casa di Giulietta", "Il famoso balcone della tragedia di Shakespeare.", LatLng(45.4419, 11.0000), 110, "Verona"),
        PlaceOfInterest("203", "Piazza delle Erbe", "La piazza più antica di Verona.", LatLng(45.4422, 10.9975), 90, "Verona"),

        // Luoghi di Forlì
        PlaceOfInterest("1", "Duomo di Forlì", "Un'abbazia storica nel quale si trova la cappella della Madonna del Fuoco.", LatLng(44.2221, 12.0390), 100, "Forlì"),
        PlaceOfInterest("2", "Abbazia di San Mercuriale", "Famoso campanile e chiostro duecentesco.", LatLng(44.2226, 12.0422), 120, "Forlì"),
        PlaceOfInterest("3", "Rocca di Ravaldino", "Fortezza medievale legata a Caterina Sforza.", LatLng(44.2175, 12.0360), 150, "Forlì"),

        // Luoghi di Cesena
        PlaceOfInterest("301", "Biblioteca Malatestiana", "Prima biblioteca civica al mondo e patrimonio UNESCO.", LatLng(44.1394, 12.2443), 150, "Cesena"),
        PlaceOfInterest("302", "Rocca Malatestiana", "Fortezza medievale panoramica situata sul colle Garampo.", LatLng(44.1378, 12.2405), 130, "Cesena"),
        PlaceOfInterest("303", "Piazza del Popolo", "Il cuore pulsante di Cesena con la Fontana Masini.", LatLng(44.1384, 12.2428), 90, "Cesena"),

        // Luoghi di Bologna
        PlaceOfInterest("401", "Le Due Torri", "Le storiche torri degli Asinelli e della Garisenda.", LatLng(44.4942, 11.3468), 140, "Bologna"),
        PlaceOfInterest("402", "Piazza Maggiore", "La piazza principale di Bologna circondata da palazzi medievali.", LatLng(44.4938, 11.3426), 100, "Bologna"),
        PlaceOfInterest("403", "Basilica di San Petronio", "Una delle chiese più grandi d'Europa, rimasta incompiuta.", LatLng(44.4931, 11.3431), 120, "Bologna"),

        // Luoghi di Milano
        PlaceOfInterest("501", "Duomo di Milano", "La maestosa cattedrale gotica simbolo della città.", LatLng(45.4642, 9.1916), 150, "Milano"),
        PlaceOfInterest("502", "Galleria Vittorio Emanuele II", "Il sontuoso 'salotto di Milano' con negozi e caffè storici.", LatLng(45.4655, 9.1897), 100, "Milano"),
        PlaceOfInterest("503", "Castello Sforzesco", "Grande fortezza rinascimentale che ospita musei d'arte.", LatLng(45.4704, 9.1793), 130, "Milano"),

        // Luoghi di Venezia
        PlaceOfInterest("601", "Piazza San Marco", "La piazza più famosa di Venezia con la Basilica e il Campanile.", LatLng(45.4342, 12.3385), 150, "Venezia"),
        PlaceOfInterest("602", "Ponte di Rialto", "Il ponte più antico e celebre che attraversa il Canal Grande.", LatLng(45.4380, 12.3359), 120, "Venezia"),
        PlaceOfInterest("603", "Palazzo Ducale", "Capolavoro del gotico veneziano e sede del Doge.", LatLng(45.4337, 12.3404), 140, "Venezia")
    )

}