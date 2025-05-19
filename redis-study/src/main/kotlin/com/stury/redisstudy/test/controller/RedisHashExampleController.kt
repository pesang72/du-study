package com.stury.redisstudy.test.controller

import com.stury.redisstudy.test.dto.HashTest
import com.stury.redisstudy.test.service.RedisHashExampleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hash")
class RedisHashExampleController(
    private val service: RedisHashExampleService
) {
    /**
     * # HSET
     * curl -X POST 'localhost:8080/hash/set?key=user:1001' \
     * -H 'Content-Type: application/json' \
     * -d '{"name": "Alice", "email": "alice@example.com", "age": 30}'
     *
     * # HGETALL
     * curl 'localhost:8080/hash/getall?key=user:1001'
     *
     * # HGET
     * curl 'localhost:8080/hash/get?key=user:1001&field=name'
     *
     * # HDEL
     * curl -X DELETE 'localhost:8080/hash/del?key=user:1001&field=age'
     *
     * # HEXISTS
     * curl 'localhost:8080/hash/exists?key=user:1001&field=email'
     */

    @PostMapping("/set")
    fun set(@RequestParam key: String, @RequestBody profile: HashTest): String {
        service.hset(key, profile)
        return "HSET OK"
    }

    @GetMapping("/getall")
    fun getAll(@RequestParam key: String): HashTest? {
        return service.hgetall(key, HashTest::class.java)
    }

    @GetMapping("/get")
    fun get(@RequestParam key: String, @RequestParam field: String): Any? {
        return service.hget(key, field)
    }

    @DeleteMapping("/del")
    fun del(@RequestParam key: String, @RequestParam field: String): String {
        service.hdel(key, field)
        return "HDEL OK"
    }

    @GetMapping("/exists")
    fun exists(@RequestParam key: String, @RequestParam field: String): Boolean {
        return service.hexists(key, field)
    }
}
