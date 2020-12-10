package com.daferarevalo.bibliotecapp.server

import java.io.Serializable

data class ReservasUsuarioServer(
    val id: String? = "",
    val titulo: String = "",
    val autor: String = "",
    val fecha: String = "",
    val imagen: String = ""
) : Serializable