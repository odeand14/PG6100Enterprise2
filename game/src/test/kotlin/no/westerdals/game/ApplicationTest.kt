package no.westerdals.game

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    @LocalServerPort
    protected var port = 0


    @Autowired
    private lateinit var gamecrud: GameRepository
    @Autowired
    private lateinit var movescrud: MovesRepository
    @Autowired
    private lateinit var requestcrud: GameRequestRepository

    @Before
    fun setup() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/game"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        gamecrud.deleteAll()
        movescrud.deleteAll()
        requestcrud.deleteAll()

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
                .extract().body().asString()
// TODO(reek): Test that the json response has the same value as the id we got in the first instance and that everythign is jk.
        println(reponse)


    }


    //TODO: only test potitive respons
    @Test
    fun acceptGameRequesttest(){

        //making a game request
        val requestId = RestAssured.given().basePath("/gameRequests/users/33")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().basePath("/gameRequest/accept/35/${requestId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

    }

    @Test
    fun acceptGameRequesttestNoRequest(){

        val requestId = 78
        //accepting the game request with no request
        val id = RestAssured.given().basePath("/gameRequest/accept/35/${requestId}")
                .patch()
                .then()
                .statusCode(404)
                .extract().body().asString()

    }

    //Test to see if nothing happens if game dont exist
    @Test
    fun postMoveTestNoGame(){


        val requestRes = RestAssured.given().basePath("/move/3/4/0/0")
                .post()
                .then()
                .statusCode(404)
                .extract().body().asString()

        Assert.assertEquals("{\"error\":\"NO GAME FOUND\",\"gameStatus\":\"0\",\"moveID\":\"\"}", requestRes)
    }

    @Test
    fun postMoveTest(){


        val player1 = 33
        //making a game request
        val gameId = RestAssured.given().basePath("/gameRequests/users/${player1}")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().basePath("/gameRequest/accept/35/${gameId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()



        //player 1 does a move
        val respons = RestAssured.given().basePath("/move/${id}/${player1}/0/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


    }

    @Test
    fun postMoveTestCoordinatesOutOfBound(){


        val player1 = 33
        //making a game request
        val gameId = RestAssured.given().basePath("/gameRequests/users/${player1}")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().basePath("/gameRequest/accept/35/${gameId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()



        val respons = RestAssured.given().basePath("/move/${id}/${player1}/3/4")
                .post()
                .then()
                .statusCode(400)
                .extract().body().asString()


    }

    @Test
    fun postMoveSameCoordinateTwiceTest(){


        val player1 = 33
        val player2 = 55

        //making a game request
        val gameId = RestAssured.given().basePath("/gameRequests/users/${player1}")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().basePath("/gameRequest/accept/${player2}/${gameId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()



        val respons1 = RestAssured.given().basePath("/move/${id}/${player1}/1/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        val respons2 = RestAssured.given().basePath("/move/${id}/${player2}/1/2")
                .post()
                .then()
                .statusCode(400)
                .extract().body().asString()


    }


    //plays the hole game
    @Test
    fun player1winGameTest(){
        val player1 = 33
        val player2 = 55
        //making a game request
        val reqId = RestAssured.given().basePath("/gameRequests/users/${player1}")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameId = RestAssured.given().basePath("/gameRequest/accept/${player2}/${reqId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()



        //playing game
        RestAssured.given().basePath("/move/${gameId}/${player1}/0/0")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


        RestAssured.given().basePath("/move/${gameId}/${player2}/2/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        RestAssured.given().basePath("/move/${gameId}/${player1}/0/1")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()



        RestAssured.given().basePath("/move/${gameId}/${player2}/1/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


        val gameResult = RestAssured.given().basePath("/move/${gameId}/${player1}/0/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        //Assert.assertEquals( "{\"error\":\"NONE\",\"gameStatus\":\"1\",\"moveID\":\"5\"}",gameResult)
        Assert.assertTrue(gameResult.contains("\"gameStatus\":\"1"))
    }



    //plays the hole game
    @Test
    fun player2winGameTest(){
        val player1 = 33
        val player2 = 55
        //making a game request
        val reqId = RestAssured.given().basePath("/gameRequests/users/${player1}")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameId = RestAssured.given().basePath("/gameRequest/accept/${player2}/${reqId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()



        //playing game
        RestAssured.given().basePath("/move/${gameId}/${player1}/0/0")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


        RestAssured.given().basePath("/move/${gameId}/${player2}/2/0")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        RestAssured.given().basePath("/move/${gameId}/${player1}/2/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()



        RestAssured.given().basePath("/move/${gameId}/${player2}/1/1")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


        RestAssured.given().basePath("/move/${gameId}/${player1}/1/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()



        val gameResult = RestAssured.given().basePath("/move/${gameId}/${player2}/0/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        Assert.assertTrue(gameResult.contains("\"gameStatus\":\"2"))
        //Assert.assertEquals( "{\"error\":\"NONE\",\"gameStatus\":\"2\",\"moveID\":\"5\"}",gameResult)

    }




    //plays the hole game
    @Test
    fun drawGameTest(){
        val player1 = 33
        val player2 = 55
        //making a game request
        val reqId = RestAssured.given().basePath("/gameRequests/users/${player1}")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameId = RestAssured.given().basePath("/gameRequest/accept/${player2}/${reqId}")
                .patch()
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()



        //playing game
        RestAssured.given().basePath("/move/${gameId}/${player1}/0/0")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


        RestAssured.given().basePath("/move/${gameId}/${player2}/0/1")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        RestAssured.given().basePath("/move/${gameId}/${player1}/0/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()



        RestAssured.given().basePath("/move/${gameId}/${player2}/1/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()


        RestAssured.given().basePath("/move/${gameId}/${player1}/1/1")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()



        RestAssured.given().basePath("/move/${gameId}/${player2}/2/0")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        RestAssured.given().basePath("/move/${gameId}/${player1}/1/0")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()



        RestAssured.given().basePath("/move/${gameId}/${player2}/2/2")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        val gameResult = RestAssured.given().basePath("/move/${gameId}/${player1}/2/1")
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString()

        Assert.assertTrue(gameResult.contains("\"gameStatus\":\"3"))
        //Assert.assertEquals( "{\"error\":\"NONE\",\"gameStatus\":\"3\",\"moveID\":\"5\"}",gameResult)

    }




}