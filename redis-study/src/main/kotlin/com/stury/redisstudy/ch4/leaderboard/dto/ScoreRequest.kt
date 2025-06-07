package com.stury.redisstudy.ch4.leaderboard.dto

data class ScoreRequest(
    val userId: String,
    val score: Int
)
