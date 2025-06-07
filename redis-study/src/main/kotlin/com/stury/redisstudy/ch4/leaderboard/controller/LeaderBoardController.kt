package com.stury.redisstudy.ch4.leaderboard.controller

import com.stury.redisstudy.ch4.leaderboard.dto.RankResponse
import com.stury.redisstudy.ch4.leaderboard.dto.ScoreRequest
import com.stury.redisstudy.ch4.leaderboard.service.LeaderboardService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {

    @PostMapping
    fun createScore(@RequestBody request: ScoreRequest) {
        leaderboardService.createScore(request.userId, request.score)
    }

    @PatchMapping
    fun updateScore(@RequestBody request: ScoreRequest) {
        leaderboardService.updateScore(request.userId, request.score)
    }

    @GetMapping("/top5")
    fun getTop5(): List<RankResponse> {
        return leaderboardService.getTop5()
    }

    @GetMapping("/around/{userId}")
    fun getAroundMe(@PathVariable userId: String): List<RankResponse> {
        return leaderboardService.getAroundMe(userId)
    }
}
