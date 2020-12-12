package com.daferarevalo.bibliotecapp.server

data class ComentarioServer(
    val id: String? = "",
    val usuario: String = "",
    val comentario: String = "",
    val fecha: String = "",
    val puntuacion: Int = 0
)