package no.westerdals.friendslist

import io.swagger.annotations.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.repository.CrudRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*

@Repository
interface FriendslistRepository : CrudRepository<FriendslistEntity, String>

@Api(value = "/friendslist", description = "Interaction with friendslist")

@RestController
class RestApi(
        private val crud: FriendslistRepository
) {

    @ApiOperation("Get all friends")
    @ApiResponse(code = 200, message = "List with Friendslist objects")
    @GetMapping(path = arrayOf("/"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getAllFriends(): ResponseEntity<List<FriendslistEntity>> {

        return ResponseEntity.ok(crud.findAll().toList())
    }

    @ApiOperation("Get number of friends in friendslist")
    @ApiResponse(code = 200, message = "Number of Friendslist objects")
    @GetMapping(path = arrayOf("/friendslistCount"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getAllFriendsCount(): ResponseEntity<Long> {
        return ResponseEntity.ok(crud.count())
    }

    @ApiOperation("Get a single friend")
    @ApiResponse(code = 200 or 404, message = "A Friendslist object")
    @GetMapping(path = arrayOf("/{friendId}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getOneFriend(@ApiParam("Id of a friend")
            @PathVariable friendId: String?): ResponseEntity<FriendslistEntity>? {

        val response = crud.findOne(friendId)
                ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(response)
    }

    @ApiOperation("Add a friend")
    @ApiResponse(code = 200, message = "The created Friendslist object")
    @PostMapping(path = arrayOf("/"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun addFriend(@RequestBody json: String?): ResponseEntity<String>? {

        val jackson = ObjectMapper()

        val newFriend = jackson.readValue(json, FriendslistEntity::class.java)

        val response = try {
            crud.save(newFriend)
        } catch (e: Exception) {
            return null
        }

        return ResponseEntity.ok(jackson.writeValueAsString(response))
    }

    @ApiOperation("Remove a friend")
    @ApiResponse(code = 204 or 404, message = "No content")
    @DeleteMapping(path = arrayOf("/{friendId}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun removeFriend(@ApiParam("Id of a friend")
            @PathVariable friendId: String?): ResponseEntity.BodyBuilder {

        val index = crud.findOne(friendId)

        val statusCode = if (crud.exists(index.toString())) 204 else 404

        return ResponseEntity.status(statusCode)
    }

}









