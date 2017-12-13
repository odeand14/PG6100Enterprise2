package no.westerdals.game

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Repository
interface GameRepository : CrudRepository<GameEntity, Long>, GameRepositoryCustom {

    fun findOneByGameId(id: Long): GameEntity?

}

@Transactional
interface GameRepositoryCustom {


    fun createGame(player1Id: Long, player2Id: Long): Long

    fun addMove(gameId: Long, move: MovesEntity)
}


@Repository
@Transactional
open class GameRepositoryImpl : GameRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager


    override fun createGame(player1Id: Long, player2Id: Long): Long {
        val entity = GameEntity( player1Id, player2Id)
        em.persist(entity)
        return entity.gameId!!
    }

    override fun addMove(gameId:Long, move: MovesEntity) {
        val gameEntity = em.find(GameEntity::class.java, gameId)!!

        (gameEntity.gameMoves!!).add(move)
        em.persist(gameEntity)
    }
}