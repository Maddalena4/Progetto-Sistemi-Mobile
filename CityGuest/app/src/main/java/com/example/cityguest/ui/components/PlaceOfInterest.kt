package com.example.cityguest.ui.components

import com.google.android.gms.maps.model.LatLng

/**
 * Classe utilizzata per rappresentare un Punto di Interesse.
 *
 * @property id L'identificativo alfanumerico univoco del luogo.
 * @property name Il nome ufficiale del luogo da mostrare all'utente.
 * @property description Una descrizione turistica o storica del POI.
 * @property location Le coordinate geografiche ([LatLng]) utilizzate per il posizionamento su mappa.
 * @property basePoints Il valore base in punti che l'utente può guadagnare interagendo o visitando questo luogo.
 * @property imageRes Il nome o riferimento alla risorsa immagine associata al luogo.
 * @property visits Il numero di visite effettuate per questo specifico luogo.
 * @property stars La valutazione in stelle media o specifica dell'utente.
 */
data class PlaceOfInterest(
    val id: String,
    val name: String,
    val description: String,
    val location: LatLng,
    val basePoints: Int,
    val imageRes: String,
    val visits: Int = 0,
    val stars: Int = 0
)