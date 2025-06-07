package com.stury.redisstudy.ch4.leaderboard.dto


data class RankResponse(
    val userId: String,
    val score: Int,
    val rank: Long
)