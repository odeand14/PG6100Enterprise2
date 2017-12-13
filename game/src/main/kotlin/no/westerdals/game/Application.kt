package no.westerdals.game

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


@SpringBootApplication
class Application {

    @Bean(name = arrayOf("OBJECT_MAPPER_BEAN"))
    fun jsonObjectMapper(): ObjectMapper {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                /*
                    JSON does not specify how dates should be represented, whereas JavaScript does.
                    And in JavaScript it is ISO 8601.
                    So, to represent dates to send over a network consumed by different clients,
                    it is reasonable to send them in ISO 8601 instead of a numeric timestamp.
                    Here we make sure timestamps are not used in marshalling of JSON data.

                    Example:
                    2001-01-05T13:15:30Z
                 */
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                //make sure we can use Java 8 dates
                .modules(JavaTimeModule())
                .build()
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}