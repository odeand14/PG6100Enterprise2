package no.westerdals.highscore


// Created by Andreas Ødegaard on 10.12.2017.
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HighscoreApplicationTest{

    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var highscoreCrud: HighscoreEntityRepository


    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.basePath = "/highscore"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        highscoreCrud.deleteAll()
    }


    @Test
    fun testInvalidID(){

        given().basePath("/highscores/123123").get()
                .then()
                .statusCode(401)
    }


    @Test
    fun testOpenCount(){

        val count = given().basePath("/highscoresCount")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString().toInt()

        assertEquals(0, count)
    }



    @Test
    fun testGetAll(){

        checkSize(0)
    }


    private fun checkSize(n: Int){
        given().basePath("/highscores").auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(n))
    }


    @Test
    fun testCreate(){

        checkSize(0)

        val user = "admin"
        val password = "admin"

        val dto = HighscoreEntity(null,1,3,"Jonny","Billy")

        given().basePath("/highscores/").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)

        checkSize(1)
    }

    @Test
    fun testUnAuthorizedCreate(){

        checkSize(0)

        val user = "adminos"
        val password = "adminos"

        val dto = HighscoreEntity(null,1,3,"Jonny","Billy")

        given().basePath("/highscores/").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(401)

        checkSize(0)
    }


    @Test
    fun testChangeField(){

        checkSize(0)

        val user2 = "John"

        val user = "admin"
        val password = "admin"

        val dto = HighscoreEntity(null,1,3,"Jonny",user2)

        val id = given().basePath("/highscores").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().body().asString().toLong()

        val changed = user2 + "_foo"

        dto.user2 = changed
        dto.id = id

        given().basePath("/highscores").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/$id")
                .then()
                .statusCode(204)

        checkSize(1)

        given().basePath("/highscores").auth().basic(user,password)
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(200)
                .body("user2", equalTo(changed))
    }



    @Test
    fun testForbiddenToChangeWrongId(){

        checkSize(0)

        val firstId = 1234L
        val user = "admin"
        val password = "admin"

        given().basePath("/highscores").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(HighscoreEntity(firstId,1,3,"Jonny","Billy"))
                .put("/$firstId")
                .then()
                .statusCode(201)

        checkSize(1)

        val secondId = 2345L

        given().basePath("/highscores").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(HighscoreEntity(secondId,4,6,"Teo","Todd"))
                .put("/$secondId")
                .then()
                .statusCode(201)

        checkSize(2)


        given().basePath("/highscores").auth().basic(user,password)
                .contentType(ContentType.JSON)
                .body(HighscoreEntity(firstId,345,34,"forbidden","forbidden"))
                .put("/$secondId")
                .then()
                .statusCode(409)

        checkSize(2)
    }
}