package io.dmendezg.jwtspringwebflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtSpringWebfluxApplication

fun main(args: Array<String>) {
    runApplication<JwtSpringWebfluxApplication>(*args)
}
