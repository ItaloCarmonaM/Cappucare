package com.example.cappucare.viewmodel

import com.example.cappucare.data.remote.ApiException

object MensajesApi {

    fun de(error: Throwable): String =
        if (error is ApiException && !error.message.isNullOrBlank()) {
            error.message!!
        } else {
            "Ocurrió un error inesperado"
        }
}