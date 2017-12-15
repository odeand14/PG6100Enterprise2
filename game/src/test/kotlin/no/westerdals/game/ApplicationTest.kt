package no.westerdals.game

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.westerdals.game.dto.GameResponseDto
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
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
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
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
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
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val id = RestAssured.given().auth().basic(username, password)
                .patch("/gameRequests/${requestId}/user/${username}")
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
                .patch("/gameRequests/${requestId}/user/${username}")
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
                .post("/move/4/0/0/users/3")
                .then()
                .statusCode(404)
                .extract().`as`(GameResponseDto::class.java)

        Assert.assertEquals("Game not found", requestRes.error)
    }

    @Test
    fun postMoveTest() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        val respons = RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/0/1/users/${username}")
                .then()
                .statusCode(200)
                .extract().`as`(GameResponseDto::class.java)

        Assert.assertEquals("", respons.error)
        Assert.assertEquals(0, respons.gameStatus)


    }

    @Test
    fun postMoveTestCoordinatesOutOfBound() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        val respons = RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/3/4/users/${username}")
                .then()
                .statusCode(400)
                .extract().`as`(GameResponseDto::class.java)

        Assert.assertEquals("coordinates out of bound",respons.error)
    }


    @Test
    fun postMoveSameCoordinateTwiceTest() {

        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        val respons = RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/1/1/users/${username}")
                .then()
                .statusCode(200)
                .extract().`as`(GameResponseDto::class.java)
        Assert.assertEquals("", respons.error)
        Assert.assertEquals(0, respons.gameStatus)


        //player 2 does a move
        val respons2 = RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/1/1/users/${username}")
                .then()
                .statusCode(409)
                .extract().`as`(GameResponseDto::class.java)

        Assert.assertEquals("coordinates already used", respons2.error)

    }


    //plays the whole game
    @Test
    fun player1winGameTest() {
        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/0/0/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/1/1/users/${username2}")
                .then()
                .statusCode(200)


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/0/1/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/1/2/users/${username2}")
                .then()
                .statusCode(200)


        //player 1 does a winning move
        val gameResult = RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/0/2/users/${username}")
                .then()
                .statusCode(200)
                .extract().`as`(GameResponseDto::class.java)


        Assert.assertEquals(1, gameResult.gameStatus)
    }


    //plays the whole game
    @Test
    fun player2winGameTest() {
        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/0/0/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/2/0/users/${username2}")
                .then()
                .statusCode(200)

        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/2/2/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/1/1/users/${username2}")
                .then()
                .statusCode(200)

        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/1/2/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        val gameResult = RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/0/2/users/${username2}")
                .then()
                .statusCode(200)
                .extract().`as`(GameResponseDto::class.java)


        Assert.assertEquals(2, gameResult.gameStatus)
    }


    //plays the hole game
    @Test
    fun drawGameTest() {
        val username = "foo"
        val username2 = "bar"
        val password = "123"

        //making a game request
        val requestId = RestAssured.given().auth().basic(username, password)
                .post("/gameRequests/user/${username}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        //accepting the game request
        val gameid = RestAssured.given().auth().basic(username2, password)
                .patch("/gameRequests/${requestId}/user/${username2}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/0/0/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/0/1/users/${username2}")
                .then()
                .statusCode(200)


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/0/2/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/1/2/users/${username2}")
                .then()
                .statusCode(200)

        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/1/1/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/2/0/users/${username2}")
                .then()
                .statusCode(200)


        //player 1 does a move
        RestAssured.given().auth().basic(username, password)
                .post("/move/${gameid}/1/0/users/${username}")
                .then()
                .statusCode(200)

        //player 2 does a move
        RestAssured.given().auth().basic(username2, password)
                .post("/move/${gameid}/2/2/users/${username2}")
                .then()
                .statusCode(200)


        //player 1 does a move
        val gameResult = RestAssured.given().accept(ContentType.JSON).auth().basic(username, password)
                .post("/move/${gameid}/2/1/users/${username}")
                .then()
                .statusCode(200)
                .extract().`as`(GameResponseDto::class.java)


        Assert.assertEquals(3, gameResult.gameStatus)
    }


}