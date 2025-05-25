package com.stury.redisstudy.leaderboard.dto

data class ScoreRequest(
    val userId: String,
    val score: Int
)
