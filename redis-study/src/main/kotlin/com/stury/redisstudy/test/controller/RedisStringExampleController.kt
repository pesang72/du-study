package com.stury.redisstudy.test.controller

import com.stury.redisstudy.test.service.RedisStringExampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/string")
class RedisStringExampleController(
    private val redisStringExampleService: RedisStringExampleService
) {
    /**
     * # 값 저장
     * curl localhost:8080/string/set
     * # → "SET OK"
     *
     * # 값 조회
     * curl localhost:8080/string/get
     *
     * curl localhost:8080/string/mset
     *
     * curl localhost:8080/string/mget
     */

    @GetMapping("/set")
    fun setHello(): String {
        redisStringExampleService.setHelloWorld()
        return "SET OK"
    }

    @GetMapping("/get")
    fun getHello(): String? {
        return redisStringExampleService.getHello()
    }

    @GetMapping("/mset")
    fun mset(): String {
        val values = mapOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "value3"
        )
        redisStringExampleService.mset(values)
        return "MSET OK"
    }

    @GetMapping("/mget")
    fun mget(): List<Any?> {
        val keys = listOf("key1", "key2", "key3", "key4") // key4는 존재하지 않아도 테스트됨
        return redisStringExampleService.mget(keys)
    }
}