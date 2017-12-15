package no.westerdals.e2e


// Created by Marius
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.awaitility.Awaitility.await
import org.awaitility.Awaitility.with
import org.awaitility.pollinterval.FibonacciPollInterval
import org.awaitility.pollinterval.FibonacciPollInterval.fibonacci
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.contains
import org.junit.Assert
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS


class GameIT {

    companion object {

        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)


        @ClassRule
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withExposedService("eureka", 8761)
                .withLocalCompose(true)

        private var counter = System.currentTimeMillis()

        @BeforeClass
        @JvmStatic
        fun initialize() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 80
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            with().pollInterval(fibonacci(SECONDS)).await().atMost(300, SECONDS)
                    .ignoreExceptions()
                    .until({

                        given().get("http://localhost:80/user").then().statusCode(401)

                        // TODO(reek): VERY IMPORTANT SWITCH THIS WITH OURS.
                        given().get("http://localhost:80/game/healtz")
                                .then().statusCode(200)


                        true
                    })
        }
    }


    @Test
    fun testUnauthorizedAccess() {

        given().get("/user")
                .then()
                .statusCode(401)
    }

    class NeededCookies(val session:String, val csrf: String)

    private fun registerUser(id: String, password: String): NeededCookies {

        val xsrfToken = given().contentType(ContentType.URLENC)
                .formParam("the_user", id)
                .formParam("the_password", password)
                .post("/signIn")
                .then()
                .statusCode(403)
                .extract().cookie("XSRF-TOKEN")

        val session=  given().contentType(ContentType.URLENC)
                .formParam("the_user", id)
                .formParam("the_password", password)
                .header("X-XSRF-TOKEN", xsrfToken)
                .cookie("XSRF-TOKEN", xsrfToken)
                .post("/signIn")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        return NeededCookies(session, xsrfToken)
    }

    private fun createUniqueId(): String {
        counter++
        return "foo_$counter"
    }


    @Test
    fun testLogin() {

        val id = createUniqueId()
        val pwd = "bar"

        val cookies = registerUser(id, pwd)

        given().get("/user")
                .then()
                .statusCode(401)

        //note the difference in cookie name
        given().cookie("SESSION", cookies.session)
                .get("/user")
                .then()
                .statusCode(200)
                .body("name", equalTo(id))
                .body("roles", contains("ROLE_USER"))


        given().auth().basic(id, pwd)
                .get("/user")
                .then()
                .statusCode(200)
                .cookie("SESSION")
                .body("name", equalTo(id))
                .body("roles", contains("ROLE_USER"))
    }


    // Utility classes, to reduce boilerplate.
    class TestGameUser(val name: String, val pwd: String, val cookies: NeededCookies)


    private fun noXSRFReq(cookies : NeededCookies) : RequestSpecification {
        return given().cookie("SESSION", cookies.session)
    }

    private fun XSRFReq(cookies : NeededCookies) : RequestSpecification {
        return noXSRFReq(cookies)
                .cookie("XSRF-TOKEN", cookies.csrf)
                .header("X-XSRF-TOKEN", cookies.csrf)
    }


    private fun createTestGameRequest(usr1 : TestGameUser) : Long {
        //making a game request
        return XSRFReq(usr1.cookies)
                .post("/game/api/gameRequests/user/${usr1.name}")
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()
    }

    private fun acceptTestGameRequest(requestId : Long, usr2 : TestGameUser) : Long {
        //accepting the game request
        return XSRFReq(usr2.cookies)
                .patch("/game/api/gameRequests/${requestId}/user/${usr2.name}")
                .then()
                .statusCode(200)
                .extract().body().asString().toLong()
    }

    private fun createTestGame(usr1 : TestGameUser, usr2 : TestGameUser) : Long {

        val requestId = createTestGameRequest(usr1)
        val gameId = acceptTestGameRequest(requestId, usr2)

        return gameId

    }

    // NB: This is done because we could not get it to build, if we referenced any of the other projects.
    // We are not proud of this, but a mans got to do, what a mans got to do. There is no pride in winning the battle,
    // if you lose the build.
    private data class TestGameResponseDto(
            var error: String = "",
            var gameStatus: Long = 0
    )

    private data class TestGameRequestEntity(
            var player1username: String = "",
            var id: Long? = null,
            var player2username: String? = null,
            var gameId: Long? = null
    )

    private fun postTestMove(gameId: Long, usr: TestGameUser, x: Long, y:Long) : TestGameResponseDto {
        return XSRFReq(usr.cookies)
                .post("/game/api/move/${gameId}/${x}/${y}/users/${usr.name}")
                .then()
                .statusCode(200)
                .extract().`as`(TestGameResponseDto::class.java)
    }

    // This is our test game for a perfecct match, where both players know
    // everything about the game state. This way we can test that just the
    // logic in the applicaton works.
    @Test
    fun testPlayAGamePerfect() {

        // We will need 2 players for this.

        val usr1 = createUniqueId()
        val pwd1 = "bar"
        val cok1 = registerUser(usr1, pwd1)

        val usr2 = createUniqueId()
        val pwd2 = "soap"
        val cok2 = registerUser(usr2,pwd2)


        val tusr1 = TestGameUser(usr1, pwd1, cok1)
        val tusr2 = TestGameUser(usr2, pwd2, cok2)

        val gameId = createTestGame(tusr1, tusr2)


        //player 1 does a move
        postTestMove(gameId, tusr1, 0, 0)
        postTestMove(gameId, tusr2, 1, 1)
        postTestMove(gameId, tusr1, 0, 1)
        postTestMove(gameId, tusr2, 1, 2)

        val gameResult = postTestMove(gameId, tusr1, 0, 2)

        Assert.assertEquals(1, gameResult.gameStatus)

    }

    // This game rests on polling to simulate how users would play over a website.
    @Test
    fun testPlayAGamePoll() {
        // We will need 2 players for this.

        val usr1 = createUniqueId()
        val pwd1 = "bar"
        val cok1 = registerUser(usr1, pwd1)

        val usr2 = createUniqueId()
        val pwd2 = "soap"
        val cok2 = registerUser(usr2,pwd2)


        val tusr1 = TestGameUser(usr1, pwd1, cok1)
        val tusr2 = TestGameUser(usr2, pwd2, cok2)

        // First player 1 creates a game request.
        val requestId = createTestGameRequest(tusr1)

        // in real life this would go on a loop, but for now, we are just going to call it once, to show that
        // it is not done yet.
        val pollResp1 = noXSRFReq(tusr1.cookies)
                .get("/game/api/gameRequests/${requestId}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(TestGameRequestEntity::class.java)

        assertNull(pollResp1.gameId)



        val gameId = acceptTestGameRequest(requestId, tusr2)

        // in real life this would go on a loop, but for now, we are just going to call it once, to show that
        // it is not done yet.
        val pollResp2 = noXSRFReq(tusr1.cookies)
                .get("/game/api/gameRequests/${requestId}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(TestGameRequestEntity::class.java)

        assertEquals(pollResp2.gameId, gameId)


        // we now show a poll step here, but then we don't bother anymore, as the process would only be repeated.

        // First player 2 tries to post, but since he is not allowed, he tries again later.
        val pollResp3 = XSRFReq(tusr2.cookies)
                .post("/game/api/move/${gameId}/1/1/users/${tusr2.name}")
                .then()
                .statusCode(400)
                .extract()
                .`as`(TestGameResponseDto::class.java)

        assertEquals(pollResp3.error,"Player 1 must make the first move")

        // player 1 makes the actuall move.

        //player 1 does a move against where he played last, because if it is equal to same user can't post twice,
        // we know that player 2 hasn't posted.
        postTestMove(gameId, tusr1, 0, 0)

        val pollResp4 = XSRFReq(tusr1.cookies)
                .post("/game/api/move/${gameId}/0/0/users/${tusr1.name}")
                .then()
                .statusCode(409)
                .extract()
                .`as`(TestGameResponseDto::class.java)

        assertEquals(pollResp4.error, "same user cant post twice")


        // now player 2 checks again, and this time he succeds. He does the same as player 1 to poll.
        postTestMove(gameId, tusr2, 1, 1)

        val pollResp5 = XSRFReq(tusr2.cookies)
                .post("/game/api/move/${gameId}/1/1/users/${tusr2.name}")
                .then()
                .statusCode(409)
                .extract()
                .`as`(TestGameResponseDto::class.java)

        assertEquals(pollResp5.error, "same user cant post twice")

        // player 1 now does the push against previous coordinates again. This time the error is placed before, so now
        // he know he can place a piece.
        val pollResp6 = XSRFReq(tusr1.cookies)
                .post("/game/api/move/${gameId}/0/0/users/${tusr1.name}")
                .then()
                .statusCode(409)
                .extract()
                .`as`(TestGameResponseDto::class.java)

        assertEquals(pollResp4.error, "scoordinates already used")

        // Player 1 now makes the original move. This process is repeated until game is over.
        postTestMove(gameId, tusr1, 0, 1)
        postTestMove(gameId, tusr2, 1, 2)

        val gameResult = postTestMove(gameId, tusr1, 0, 2)

        assertEquals(1, gameResult.gameStatus)

        // lastley player 2 pushes against his last move, but now he gets game finished.

        val pollResp9 = XSRFReq(tusr2.cookies)
                .post("/game/api/move/${gameId}/1/2/users/${tusr2.name}")
                .then()
                .statusCode(406)
                .extract()
                .`as`(TestGameResponseDto::class.java)

        assertEquals(pollResp9.error, "Game is finished")
    }

    // This is a messy game, where the players post in wrong order and such.
    // it should still ultimatly work, but it will test our error system.
    @Test
    fun testPlayAGameMessy() {
        // We will need 2 players for this.

        val usr1 = createUniqueId()
        val pwd1 = "bar"
        val cok1 = registerUser(usr1, pwd1)

        val usr2 = createUniqueId()
        val pwd2 = "soap"
        val cok2 = registerUser(usr2,pwd2)
    }

    // This test attempts to reproduce as many ways as possible for one of the
    // parties to attempt to cheat. In our enterpriiiissssseeeee app this is ofcurse
    // not possible, but we should still make the effort to check.
    @Test
    fun testPlayAGameCheaty() {
        // We will need 2 players for this.

        val usr1 = createUniqueId()
        val pwd1 = "bar"
        val cok1 = registerUser(usr1, pwd1)

        val usr2 = createUniqueId()
        val pwd2 = "soap"
        val cok2 = registerUser(usr2,pwd2)



    }

    // THis is a test for a 3rd party trying to post moves,
    // posting as someone else. This is really quite similar to the
    // cheating example, but as this might change in the future, and
    // this involves a 3rd party, we will try to fix it.
    @Test
    fun testPlayAGameTheify() {
        // We will need 3 players for this.


        // Brb
        val usr1 = createUniqueId()
        val pwd1 = "bar"
        val cok1 = registerUser(usr1, pwd1)

        val usr2 = createUniqueId()
        val pwd2 = "soap"
        val cok2 = registerUser(usr2,pwd2)

        val usr3 = createUniqueId()
        val pwd3 = "cry"
        val cok3 = registerUser(usr3,pwd3)

    }

    // NOT OUR TESTS.


    @Test
    fun testOpenCount(){

        val x = given().basePath("/user-service/usersInfoCount")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString().toInt()

        assertTrue(x >= 0 )
    }


    /**
     * Remember to create a test that checks if we can play against ourselves.
     * This should not be possible. player1id == player2id should no happen.
     *
     */


    @Test
    fun testForbiddenToChangeOthers() {

        val firstId = createUniqueId()
        val firstCookies = registerUser(firstId, "123")
        val firstPath = "/user-service/usersInfo/$firstId"

        /*
            In general, it can make sense to have the DTOs in their
            own module, so can be reused in the client directly.
            Otherwise, we would need to craft the JSON manually,
            as done in these tests
         */

        given().cookie("SESSION", firstCookies.session)
                .get("/user")
                .then()
                .statusCode(200)
                .body("name", equalTo(firstId))
                .body("roles", contains("ROLE_USER"))


        given().cookie("SESSION", firstCookies.session)
                .cookie("XSRF-TOKEN", firstCookies.csrf)
                .header("X-XSRF-TOKEN", firstCookies.csrf)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$firstId",
                        "name": "A",
                        "surname": "B",
                        "email": "a@a.com"
                    }
                    """)
                .put(firstPath)
                .then()
                .statusCode(201)


        val secondId = createUniqueId()
        val secondCookies = registerUser(secondId, "123")
        val secondPath = "/user-service/usersInfo/$secondId"

        given().cookie("SESSION", secondCookies.session)
                .cookie("XSRF-TOKEN", secondCookies.csrf)
                .header("X-XSRF-TOKEN", secondCookies.csrf)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$secondId",
                        "name": "bla",
                        "surname": "bla",
                        "email": "bla@bla.com"
                    }
                    """)
                .put(secondPath)
                .then()
                .statusCode(201)



        given().cookie("SESSION", firstCookies.session)
                .cookie("XSRF-TOKEN", firstCookies.csrf)
                .header("X-XSRF-TOKEN", firstCookies.csrf)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$secondId"
                    }
                    """)
                .put(secondPath)
                .then()
                .statusCode(403)
    }





}