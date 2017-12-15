package no.westerdals.friendslist

import io.restassured.RestAssured
import io.restassured.RestAssured.*
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
class FriendslistTest {

    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var crud: FriendslistRepository

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.basePath = "/friendslist"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        crud.deleteAll()
    }

    @Test
    fun testInvalidFriendID() {
        given().basePath("/123123").auth().basic("admin","admin")
                .get()
                .then()
                .statusCode(404)
    }

    @Test
    fun testGetFriendsCount() {
        val count = given().basePath("/friendslistCount").auth().basic("admin","admin")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString().toInt()

        assertEquals(0, count)
    }

    @Test
    fun testGetAllFriends() {
        given().basePath("").auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body(equalTo("[]"))
    }

    private fun getFriends(n: Int){
        val response = given().basePath("/friendslist").auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .get()
                .then()
                .extract()
        val statusCode = response.statusCode()
        val body = response.body()
        if (statusCode == 200) {
            assertEquals(n, arrayOf(body).size)
        } else {
            assertEquals(n, 0)
        }
    }

    @Test
    fun testUnAuthorizedFriendCreate() {

        getFriends(0)

        val dto = FriendslistEntity("1",1,2,"2017-12-14T20:03:12")

        given().basePath("/friendslist/").auth().basic("un","authed")
                .contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(401)

        getFriends(0)
    }

    @Test
    fun testCreateFriend() {

        getFriends(0)

        val dto = FriendslistEntity("",1,2,"2017-12-14T20:03:12")

        given().basePath("/friendslist/").auth().basic("admin","admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)

        getFriends(1)
    }

    @Test
    fun testDeleteFriend() {

        getFriends(1)

        given().basePath("/friendslist/1").auth().basic("admin","admin")
                .contentType(ContentType.JSON)
                .delete()
                .then()
                .statusCode(401)

        getFriends(0)
    }
}