package com.example.cappucare

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

object HttpFalso {

    fun error(codigo: Int, mensaje: String): HttpException {
        val cuerpo = """{"mensaje":"$mensaje"}""".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(codigo, cuerpo))
    }
}
