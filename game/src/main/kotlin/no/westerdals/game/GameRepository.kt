package no.westerdals.game

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Repository
interface GameRepository : CrudRepository<GameEntity, Long>, GameRepositoryCustom {

}

@Transactional
interface GameRepositoryCustom {


    fun createGame(gameId:Long, player1Id: Long, player2Id: Long): Long
}


@Repository
@Transactional
open class GameRepositoryImpl : GameRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager


    override fun createGame(gameId:Long, player1Id: Long, player2Id: Long): Long {
        val entity = GameEntity(null, player1Id, player2Id, null);
        em.persist(entity)
        return entity.gameId!!
    }
}