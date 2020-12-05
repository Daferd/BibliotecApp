package com.daferarevalo.bibliotecapp.server

data class ComentarioServer(
    val id: String? = "",
    val usuario: String = "",
    val comentario: String = "",
    val puntuacion: Int = 0
)