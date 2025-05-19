package com.stury.redisstudy.test.controller

import com.stury.redisstudy.test.service.RedisListExampleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/list")
class RedisListExampleController(
    private val service: RedisListExampleService
) {
    /**
     * # LPUSH
     * curl -X POST localhost:8080/list/lpush -H "Content-Type: application/json" -d '["A", "B", "C"]'
     *
     * # RPUSH
     * curl -X POST localhost:8080/list/rpush -H "Content-Type: application/json" -d '["1", "2", "3"]'
     *
     * # LRANGE
     * curl 'http://localhost:8080/list/lrange?start=0&end=-1'
     *
     * # LINDEX
     * curl localhost:8080/list/lindex?index=2
     *
     * # LPOP
     * curl localhost:8080/list/lpop
     *
     * # RPOP
     * curl localhost:8080/list/rpop
     *
     * # LTRIM (남길 범위 설정)
     * curl -X POST localhost:8080/list/ltrim?start=0&end=1
     */

    @PostMapping("/lpush")
    fun lpush(@RequestBody body: List<String>): String {
        service.lpush(*body.toTypedArray())
        return "LPUSH OK"
    }

    @PostMapping("/rpush")
    fun rpush(@RequestBody body: List<String>): String {
        service.rpush(*body.toTypedArray())
        return "RPUSH OK"
    }

    @GetMapping("/lpop")
    fun lpop(): String? = service.lpop()

    @GetMapping("/rpop")
    fun rpop(): String? = service.rpop()

    @GetMapping("/lrange")
    fun lrange(
        @RequestParam start: Long,
        @RequestParam end: Long
    ): List<String> = service.lrange(start, end)

    @GetMapping("/lindex")
    fun lindex(@RequestParam index: Long): String? = service.lindex(index)

    @PostMapping("/ltrim")
    fun ltrim(@RequestParam start: Long, @RequestParam end: Long): String {
        service.ltrim(start, end)
        return "LTRIM OK"
    }
}