package no.westerdals.e2e


// Created by Andreas Ødegaard on 14.12.2017.
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.contains
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit


class DistributedSessionDockerIT {

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


            await().atMost(300, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until({
                        Thread.sleep(5000)
                        given().get("http://localhost:80/user").then().statusCode(401)

                        given().get("http://localhost:80/user-service/usersInfo")
                                .then().statusCode(401)

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

    @Test
    fun testOpenCount(){

        val x = given().basePath("/user-service/usersInfoCount")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString().toInt()

        assertTrue(x >= 0 )
    }



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


    @Test
    fun testGetHighscore(){

        val id = createUniqueId()
        val name = "foo"
        val pwd = "bar"

        val cookies = registerUser(id, pwd)


        given().cookie("SESSION", cookies.session)
                .cookie("XSRF-TOKEN", cookies.csrf)
                .header("X-XSRF-TOKEN", cookies.csrf)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$id",
                        "name": "$name",
                        "surname": "B",
                        "email": "a@a.com"
                    }
                    """)
                .put("/user-service/usersInfo/$id")
                .then()
                .statusCode(201)


        var count = given().cookie("SESSION", cookies.session)
                .accept(ContentType.JSON)
                .get("/highscore/highscoresCount")
                .then()
                .statusCode(200)
                .extract().body().asString().toInt()

        Assert.assertEquals(0, count)
    }
}