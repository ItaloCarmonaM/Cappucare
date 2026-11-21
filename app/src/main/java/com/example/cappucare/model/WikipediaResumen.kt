package com.example.cappucare.model

import com.google.gson.annotations.SerializedName
data class WikipediaResumen(
    val type: String? = null,
    val title: String? = null,
    val description: String? = null,
    val extract: String? = null,
    @SerializedName("content_urls") val contentUrls: WikipediaUrls? = null
) {
    // Enlace al artículo completo (versión web)
    val urlArticulo: String?
        get() = contentUrls?.desktop?.page

    val tieneInformacion: Boolean
        get() = type != "disambiguation" && !extract.isNullOrBlank()
}

data class WikipediaUrls(val desktop: WikipediaPagina? = null)

data class WikipediaPagina(val page: String? = null)