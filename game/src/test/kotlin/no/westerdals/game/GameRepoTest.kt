package no.westerdals.game

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NEVER)
class GameRepoTest {

    @Autowired
    private lateinit var crud: GameRepository


    @Before
    fun cleanDatabase(){
        crud.deleteAll();
    }

    @Test
    fun testInitialization(){
        assertNotNull(crud);
    }

    @Test
    fun testCreate(){
        assertEquals(0, crud.count());

        val id= crud.createGame("foo", "bar");

        assertEquals(1, crud.count())
        assertEquals(id, crud.findOne(id).gameId)
    }
}