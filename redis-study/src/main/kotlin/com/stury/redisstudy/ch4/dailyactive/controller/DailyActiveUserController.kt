package com.stury.redisstudy.ch4.dailyactive.controller

import com.stury.redisstudy.ch4.dailyactive.service.DailyActiveUserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ch4/dau")
class DailyActiveUserController(
    private val dailyActiveUserService: DailyActiveUserService
) {
    @PostMapping("/add")
    fun addActiveUser(
        @RequestParam date: String,
        @RequestParam userId: String
    ) {
        dailyActiveUserService.addActiveUser(date, userId)
    }

    @GetMapping("/{date}")
    fun getDailyActiveUserCount(@PathVariable date: String): Long {
        return dailyActiveUserService.getDailyActiveUserCount(date)
    }
}