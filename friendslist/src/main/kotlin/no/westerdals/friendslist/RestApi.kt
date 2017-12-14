package no.westerdals.friendslist

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.repository.CrudRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*

@Repository
interface FriendslistRepository : CrudRepository<FriendslistEntity, String>

@RestController
class RestApi(
        private val crud: FriendslistRepository
) {

    @GetMapping(path = arrayOf("/"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getAllFriends(): ResponseEntity<List<FriendslistEntity>> {

        return ResponseEntity.ok(crud.findAll().toList())
    }

    @GetMapping(path = arrayOf("/{friendId}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getOneFriend(@PathVariable friendId: String?): ResponseEntity<FriendslistEntity> {

        val response = crud.findOne(friendId)
                ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(response)
    }

    @PostMapping(path = arrayOf("/"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun addFriend(@RequestBody json: String?): ResponseEntity<FriendslistEntity>? {

        val jackson = ObjectMapper()

        val newFriend = jackson.readValue(json, FriendslistEntity::class.java)

        val response = try {
            crud.save(newFriend)
        } catch (e: Exception) {
            return null
        }

        return ResponseEntity.ok(response)
    }

    @DeleteMapping(path = arrayOf("/{friendId}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun removeFriend(@PathVariable friendId: String?): ResponseEntity.BodyBuilder {

        val index = crud.findOne(friendId)

        val statusCode = if (crud.exists(index.toString())) 201 else 404

        return ResponseEntity.status(statusCode)
    }

}









