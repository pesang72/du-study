package com.stury.redisstudy.leaderboard.dto


data class RankResponse(
    val userId: String,
    val score: Int,
    val rank: Long
)