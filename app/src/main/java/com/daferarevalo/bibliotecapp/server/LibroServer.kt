package com.daferarevalo.bibliotecapp.server

import java.io.Serializable

data class LibroServer(
    val id: String? = "",
    val titulo: String = "",
    val autor: String = "",
    val imagen: String = "",
    val categoria: String = "",
    val signatura: String = "",
    val estado: String = "",
    val puntuacion: Int = 0,
    val cantidadDePuntuaciones: Int = 0,
    val promedio: Float = 0.0f,
    val fechaVencimiento: String = ""
) : Serializable

