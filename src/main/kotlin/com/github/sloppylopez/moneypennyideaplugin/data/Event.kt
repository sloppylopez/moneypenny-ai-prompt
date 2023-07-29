package com.github.sloppylopez.moneypennyideaplugin.data

import java.time.LocalDateTime

data class Event(val time: LocalDateTime, val description: String, val isUser: Boolean)