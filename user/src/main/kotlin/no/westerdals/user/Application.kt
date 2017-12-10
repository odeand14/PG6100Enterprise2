package no.westerdals.user

// Created by Andreas Ødegaard on 10.12.2017.
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}