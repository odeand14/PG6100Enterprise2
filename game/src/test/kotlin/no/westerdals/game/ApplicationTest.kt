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

    @Test
    fun createGameRequesttest(){

        //Making game request
        val id = RestAssured.given().basePath("/gameRequests/users/33")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //Get Game request by id
        val reponse = RestAssured.given().basePath("/gameRequests/${id}")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()
// TODO(reek): Test that the json response has the same value as the id we got in the first instance and that everythign is jk.
        println(reponse)


    }


    //TODO: only test potitive respons
    @Test
    fun acceptGameRequesttest(){


        val requestId = RestAssured.given().basePath("/gameRequests/users/33")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        val id = RestAssured.given().basePath("/gameRequest/accept/35/${requestId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

    }

    @Test
    fun postMoveTestNoGame(){


        val requestRes = RestAssured.given().basePath("/move/3/4/5/6")
                .post()
                .then()
                .statusCode(404)
                .extract().body().asString()

        Assert.assertEquals("{\"error\":\"NO GAME FOUND\",\"moveID\":\"\"}", requestRes)
    }

    @Test
    fun postMoveTest(){


        val requestId = RestAssured.given().basePath("/move/3/4/5/6")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        println("###############")
        println(requestId)
    }




}