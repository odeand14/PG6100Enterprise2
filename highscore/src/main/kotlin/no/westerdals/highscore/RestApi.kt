package no.westerdals.highscore

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Api(value = "/highscores", description = "Handling of creating, editing, deletion and retrieving of highscores")
@RestController
@Validated
class RestApi(
        private val highscoreCrud: HighscoreEntityRepository
) {
    @ApiOperation("Get the number of highscores")
    @GetMapping(path = arrayOf("/highscoresCount"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getCount(): ResponseEntity<Long> {

        return ResponseEntity.ok(highscoreCrud.count())
    }

    @ApiOperation("Get all highscores in the database")
    @GetMapping(path = arrayOf("/highscores"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getAll(): ResponseEntity<List<HighscoreEntity>> {

        return ResponseEntity.ok(highscoreCrud.findAll().toList())
    }

    @ApiOperation("Get highscore by ID")
    @GetMapping(path = arrayOf("/highscores/{id}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getById(@ApiParam("The highscore id")
            @PathVariable id: Long)
            : ResponseEntity<HighscoreEntity> {

        val entity = highscoreCrud.findOneById(id)
                ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(entity)
    }


    @ApiOperation("Puts values in given highscore, if nonexistent creates a new highscore")
    @PutMapping(path = arrayOf("/highscores/{id}"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 204 or 201, message = "The newly altered or created highscore")
    fun replace(@ApiParam("The highscore that will replace the old one. Cannot change its id though.")
            @PathVariable id: Long,
            @RequestBody dto: HighscoreEntity)
            : ResponseEntity<HighscoreEntity> {

        if (id != dto.id) {
            return ResponseEntity.status(409).build()
        }

        val alreadyExists = highscoreCrud.exists(id)
        var code = if(alreadyExists) 204 else 201

        try {
            return ResponseEntity.status(code).body(highscoreCrud.save(dto))
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }

    @ApiOperation("Deletes a highscore based on ID")
    @DeleteMapping(path = arrayOf("/highscores/{id}"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 200, message = "The id of deleted highscore")
    fun delete(@ApiParam("The highscore id")
            @PathVariable id: Long)
            : ResponseEntity<Long> {


        var code = 200

        try {
            return ResponseEntity.status(code).body(highscoreCrud.deleteHighscore(id))
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }

    @ApiOperation("Posts a new highscore")
    @PostMapping(path = arrayOf("/highscores"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 201, message = "The id of created highscore")
    fun post(
            @ApiParam("Name of the two players, plus their respective scores. Should not specify id")
            @RequestBody body: String)
            : ResponseEntity<Long> {

        if (body != "") {

            val jackson = ObjectMapper()

            val rootNode = jackson.readValue(body, JsonNode::class.java)

            val user1 = rootNode.get("user1").asText()
            val user2 = rootNode.get("user2").asText()
            val score1 = rootNode.get("score1").asInt()
            val score2 = rootNode.get("score2").asInt()
            val score = HighscoreEntity(null, score1, score2, user1, user2)
            var code = 201

            try {
                return ResponseEntity.status(code).body(highscoreCrud.save(score).id)
            } catch (e: Exception) {
                code = 400
            }
            return ResponseEntity.status(code).build()
        }

        return ResponseEntity.status(409).build()
    }
}
