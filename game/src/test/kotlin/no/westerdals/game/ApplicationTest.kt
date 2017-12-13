package no.westerdals.game

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    @LocalServerPort
    protected var port = 0


    @Before
    fun setup() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/game"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()


    }


    @Test
    fun testGametest(){

        val str = RestAssured.given().basePath("/game")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString()

        Assert.assertEquals("Gametest", str)
    }


}