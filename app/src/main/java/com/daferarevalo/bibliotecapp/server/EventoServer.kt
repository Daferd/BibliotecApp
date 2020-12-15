package com.daferarevalo.bibliotecapp.server

data class EventoServer(
    val id: String? = "",
    val titulo: String = "",
    val fecha: String = "",
    val ubicacion: String = "",
    val hora: String = "",
    var asistentes: Int = 0
)