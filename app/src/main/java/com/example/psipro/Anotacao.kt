package com.example.psipro

import java.util.Date

data class Anotacao(
    val id: String,
    val pacienteId: String,
    val texto: String,
    val data: Date
) 