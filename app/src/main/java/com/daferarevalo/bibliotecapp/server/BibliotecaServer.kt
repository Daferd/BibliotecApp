package com.daferarevalo.bibliotecapp.server

import java.io.Serializable

data class BibliotecaServer(
    val id: String? = "",
    val titulo: String? = "",
    val direccion: String = "",
    val imagen: String = "",
    val longitud: Double = 0.0,
    val latitud: Double = 0.0
) : Serializable