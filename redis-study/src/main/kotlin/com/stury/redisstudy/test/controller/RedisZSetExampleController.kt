package com.stury.redisstudy.test.controller

import com.stury.redisstudy.test.service.RedisZSetExampleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/zset")
class RedisZSetExampleController(
    private val service: RedisZSetExampleService
) {
    /**
     * # ZADD
     * curl -X POST 'localhost:8080/zset/add?key=leaderboard&member=user1&score=100'
     * curl -X POST 'localhost:8080/zset/add?key=leaderboard&member=user2&score=250'
     * curl -X POST 'localhost:8080/zset/add?key=leaderboard&member=user3&score=180'
     *
     * # ZRANGE (오름차순)
     * curl 'localhost:8080/zset/range?key=leaderboard&start=0&end=-1'
     *
     * # ZREVRANGE (내림차순)
     * curl 'localhost:8080/zset/revrange?key=leaderboard&start=0&end=-1'
     *
     * # ZRANK
     * curl 'localhost:8080/zset/rank?key=leaderboard&member=user2'
     *
     * # ZSCORE
     * curl 'localhost:8080/zset/score?key=leaderboard&member=user2'
     *
     * # ZREM
     * curl -X DELETE 'localhost:8080/zset/remove?key=leaderboard&member=user3'
     *
     * # ZCARD
     * curl 'localhost:8080/zset/count?key=leaderboard'
     *
     */

    @PostMapping("/add")
    fun zadd(@RequestParam key: String, @RequestParam member: String, @RequestParam score: Double): String {
        service.zadd(key, member, score)
        return "ZADD OK"
    }

    @GetMapping("/range")
    fun zrange(@RequestParam key: String, @RequestParam start: Long, @RequestParam end: Long): Set<String> {
        return service.zrange(key, start, end)
    }

    @GetMapping("/revrange")
    fun zrevrange(@RequestParam key: String, @RequestParam start: Long, @RequestParam end: Long): Set<String> {
        return service.zrevrange(key, start, end)
    }

    @GetMapping("/rank")
    fun zrank(@RequestParam key: String, @RequestParam member: String): Long? {
        return service.zrank(key, member)
    }

    @GetMapping("/score")
    fun zscore(@RequestParam key: String, @RequestParam member: String): Double? {
        return service.zscore(key, member)
    }

    @DeleteMapping("/remove")
    fun zrem(@RequestParam key: String, @RequestParam member: String): String {
        service.zrem(key, member)
        return "ZREM OK"
    }

    @GetMapping("/count")
    fun zcard(@RequestParam key: String): Long {
        return service.zcard(key)
    }
}
