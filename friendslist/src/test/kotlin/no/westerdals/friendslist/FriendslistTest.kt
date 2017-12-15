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
        RestAssured.baseURI = "http://127.0.0.1"
        RestAssured.basePath = "/friendslist"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        crud.deleteAll()
    }

    @Test
    fun testInvalidID() {
        given().basePath("/123123")
                .get()
                .then()
                .statusCode(404)
    }

    @Test
    fun testGetCount() {
        val count = given().basePath("/friendslistCount")
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

//    private fun getFriends(n: Int){
//        given().basePath("/friendslist").auth().basic("admin","admin")
//                .accept(ContentType.JSON)
//                .get()
//                .then()
//                .statusCode(200)
//                .body("size()", equalTo(n))
//    }
//
//    @Test
//    fun testCreate() {
//
//        getFriends(0)
//
//        val user = "admin"
//        val password = "admin"
//
//        val dto = FriendslistEntity("",1,2,"2017-12-14T20:03:12")
//
//        given().basePath("/friendslist/").auth().basic(user, password)
//                .contentType(ContentType.JSON)
//                .body(dto)
//                .post()
//                .then()
//                .statusCode(201)
//
//        getFriends(1)
//    }
//
//    @Test
//    fun testUnAuthorizedCreate(){
//
////        checkSize(0)
//
//        val user = "un"
//        val password = "authed"
//
//        val dto = FriendslistEntity(null,1,2,"2017-12-14T20:03:12")
//
//        given().basePath("/friendslist/").auth().basic(user, password)
//                .contentType(ContentType.JSON)
//                .body(dto)
//                .post()
//                .then()
//                .statusCode(401)
//
////        checkSize(0)
//    }
}