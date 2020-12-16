package com.daferarevalo.bibliotecapp.server

import java.io.Serializable

data class ReservasServer(
        val id: String? = "",
        val titulo: String = "",
        val autor: String = "",
        val fechaVencimiento: String = "",
        val imagen: String = "",
        val nombreUsuario: String = "",
        val uidUsuario: String = ""
) : Serializable
