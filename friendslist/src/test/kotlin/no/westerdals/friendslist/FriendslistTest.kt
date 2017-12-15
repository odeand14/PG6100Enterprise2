package no.westerdals.friendslist

import com.fasterxml.jackson.databind.ObjectMapper
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

    private fun countFriends(): Int {
        return given().basePath("/friendslistCount").auth().basic("admin","admin")
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString().toInt()
    }

    @Test
    fun testUnauthedRequest() {
        given().basePath("/")
                .get()
                .then()
                .statusCode(401)
    }

    @Test
    fun testGetFriendsCount() {
        val count = countFriends()

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

    private fun createDummyFriend(user: String, pass: String): Array<Int?> {
        val dto = FriendslistEntity(null,1,2,"2017-12-14T20:03:12")

        val response = given().basePath("").auth().basic(user, pass)
                .contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .extract()
        val jackson = ObjectMapper()
        val friend = try {
            jackson.readValue(response.body().asString(), FriendslistEntity::class.java)
        } catch (e: Exception) {
            null
        }

        return arrayOf(response.statusCode(), friend?.id)
    }

    @Test
    fun testUnAuthorizedFriendCreate() {
        val friendsBeforeInsert = countFriends()
        assertEquals(401, createDummyFriend("un", "authed")[0])
        assertEquals(friendsBeforeInsert, countFriends())
    }

    @Test
    fun testCreateFriend() {
        val friendsBeforeInsert = countFriends()
        assertEquals(200, createDummyFriend("admin", "admin")[0])
        assertEquals(friendsBeforeInsert + 1, countFriends())
    }

    @Test
    fun testDeleteFriend() {
        val friendId = createDummyFriend("admin", "admin")[1]
        val friendsBeforeDelete = countFriends()

        given().basePath("/${friendId}").auth().basic("admin","admin")
                .contentType(ContentType.JSON)
                .delete()
                .then()
                .statusCode(204)

        assertEquals(friendsBeforeDelete - 1, countFriends())
    }
}