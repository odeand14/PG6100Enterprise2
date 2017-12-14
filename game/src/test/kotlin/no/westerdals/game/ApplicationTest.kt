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
        RestAssured.basePath = "/api"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        gamecrud.deleteAll()
        movescrud.deleteAll()
        requestcrud.deleteAll()

    }


    @Test
    fun createGameRequestTest() {

        val username = "foo"
        val password = "123"

        //Making game request
        val id = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //Get Game-request by id
        val reponse = RestAssured.given().auth().basic(username, password)
                .get("/gameRequests/${id}")
                .then()
                .statusCode(200)
                .extract().body().asString()
// TODO(reek): Test that the json response has the same value as the id we got in the first instance and that everythign is jk.
        println(reponse)


    }


    //TODO: only test potitive respons
    @Test
    fun acceptGameRequestTest() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"


        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()
    }


    @Test
    fun acceptGameRequestWithCreatorUsernameTest() {

        val username = "foo"
        val password = "123"


        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().auth().basic(username, password)
                .patch("/user/${username}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(409)
                .extract().body().asString()
    }


    @Test
    fun acceptGameRequestNoRequestTest() {

        val username = "foo"
        val password = "123"
        val requestId = 343;

        //accepting the game request with no request
        val id = RestAssured.given().auth().basic(username, password)
                .patch("/user/${username}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(404)
                .extract().body().asString()
    }

    //Test to see if nothing happens if game dont exist
    @Test
    fun postMoveNoGameTest() {
        val username = "foo"
        val password = "123"

        val requestRes = RestAssured.given().auth().basic(username, password)
                .post("users/3/move/4/0/0")
                .then()
                .statusCode(404)
                .extract().body().asString()

        Assert.assertEquals("{\"error\":\"NO GAME FOUND\",\"gameStatus\":\"0\",\"moveID\":\"\"}", requestRes)
    }

    @Test
    fun postMoveTest() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        val respons = RestAssured.given().auth().basic(username2, password)
                .post("/users/${username}/move/${gameid}/0/1")
                .then()
                .statusCode(200)
                .extract().body().asString()


    }

    @Test
    fun postMoveTestCoordinatesOutOfBound() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        val respons = RestAssured.given().auth().basic(username2, password)
                .post("/users/${username}/move/${gameid}/3/4")
                .then()
                .statusCode(400)
                .extract().body().asString()

    }


    @Test
    fun postMoveSameCoordinateTwiceTest() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        val respons = RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/1/1")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        val respons2 = RestAssured.given().auth().basic(username2, password)
                .post("/users/${username}/move/${gameid}/1/1")
                .then()
                .statusCode(409)
                .extract().body().asString()


    }


    //plays the whole game
    @Test
    fun player1winGameTest() {
        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/0/0")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/1/1")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/0/1")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/1/2")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //player 1 does a move
        val gameResult = RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/0/2")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //Assert.assertEquals( "{\"error\":\"NONE\",\"gameStatus\":\"1\",\"moveID\":\"5\"}",gameResult)
        Assert.assertTrue(gameResult.contains("\"gameStatus\":\"1"))
    }


    //plays the hole game
    @Test
    fun player2winGameTest() {
        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/0/0")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/2/0")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/2/2")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/1/1")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/1/2")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        val gameResult = RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/0/2")
                .then()
                .statusCode(200)
                .extract().body().asString()




        Assert.assertTrue(gameResult.contains("\"gameStatus\":\"2"))
        //Assert.assertEquals( "{\"error\":\"NONE\",\"gameStatus\":\"2\",\"moveID\":\"5\"}",gameResult)

    }


    //plays the hole game
    @Test
    fun drawGameTest() {
        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/user/${username}/gameRequests")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/user/${username2}/gameRequest/accept/${requestId}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/0/0")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/0/1")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/0/2")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/1/2")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/1/1")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/2/0")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/1/0")
                .then()
                .statusCode(200)
                .extract().body().asString()

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/users/${username2}/move/${gameid}/2/2")
                .then()
                .statusCode(200)
                .extract().body().asString()


        //player 1 does a move
        val gameResult = RestAssured.given().auth().basic(username, password)
                .post("/users/${username}/move/${gameid}/2/1")
                .then()
                .statusCode(200)
                .extract().body().asString()



        Assert.assertTrue(gameResult.contains("\"gameStatus\":\"3"))
        //Assert.assertEquals( "{\"error\":\"NONE\",\"gameStatus\":\"3\",\"moveID\":\"5\"}",gameResult)

    }


}