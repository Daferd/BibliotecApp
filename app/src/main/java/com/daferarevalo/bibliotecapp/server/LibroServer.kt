package com.daferarevalo.bibliotecapp.server

import java.io.Serializable

data class LibroServer(
    val id: String? = "",
    val titulo: String = "",
    val autor: String = "",
    val imagen: String = "",
    val categoria: String = "",
    val signatura: String = "",
    val estado: String = ""
) : Serializable

