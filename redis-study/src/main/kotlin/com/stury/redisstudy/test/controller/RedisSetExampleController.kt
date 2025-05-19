package com.stury.redisstudy.test.controller

import com.stury.redisstudy.test.service.RedisSetExampleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/set")
class RedisSetExampleController(
    private val service: RedisSetExampleService
) {
    /**
     * # SADD
     * curl -X POST 'localhost:8080/set/add?key=user:1001:tags' \
     * -H 'Content-Type: application/json' \
     * -d '["redis", "kotlin", "backend"]'
     *
     * # SMEMBERS
     * curl 'localhost:8080/set/members?key=user:1001:tags'
     *
     * # SISMEMBER
     * curl 'localhost:8080/set/exists?key=user:1001:tags&value=redis'
     *
     * # SREM
     * curl -X POST 'localhost:8080/set/remove?key=user:1001:tags' \
     * -H 'Content-Type: application/json' \
     * -d '["kotlin"]'
     *
     * # SUNION
     * curl 'localhost:8080/set/union?key1=user:1001:tags&key2=user:1002:tags'
     *
     * # SINTER
     * curl 'localhost:8080/set/inter?key1=user:1001:tags&key2=user:1002:tags'
     *
     * # SDIFF
     * curl 'localhost:8080/set/diff?key1=user:1001:tags&key2=user:1002:tags'
     */

    @PostMapping("/add")
    fun add(@RequestParam key: String, @RequestBody values: List<String>): String {
        service.sadd(key, *values.toTypedArray())
        return "SADD OK"
    }

    @PostMapping("/remove")
    fun remove(@RequestParam key: String, @RequestBody values: List<String>): String {
        service.srem(key, *values.toTypedArray())
        return "SREM OK"
    }

    @GetMapping("/members")
    fun members(@RequestParam key: String): Set<String> = service.smembers(key)

    @GetMapping("/exists")
    fun exists(@RequestParam key: String, @RequestParam value: String): Boolean =
        service.sismember(key, value)

    @GetMapping("/count")
    fun count(@RequestParam key: String): Long = service.scard(key)

    @GetMapping("/union")
    fun union(@RequestParam key1: String, @RequestParam key2: String): Set<String> =
        service.sunion(key1, key2)

    @GetMapping("/inter")
    fun inter(@RequestParam key1: String, @RequestParam key2: String): Set<String> =
        service.sinter(key1, key2)

    @GetMapping("/diff")
    fun diff(@RequestParam key1: String, @RequestParam key2: String): Set<String> =
        service.sdiff(key1, key2)


}
