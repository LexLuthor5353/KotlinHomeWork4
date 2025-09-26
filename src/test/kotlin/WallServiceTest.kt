import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WallServiceTest {
    @Test
    fun add() {
        val service = WallService()
        val post = Post(text = "Hello user!")
        val addedPost = service.add(post)

        assertNotEquals(0, addedPost.id)
    }

    @Test
    fun update() {
        val service = WallService()
        val original = service.add(Post(text = "Orig"))
        val updated = original.copy(text = "Updat")

        val result = service.update(updated)

        assertTrue(result)

    }
    @Test
    fun updateReturnFalse() {
        val service = WallService()
        val post = Post(id = 999, text = "Post someone")
        val result = service.update(post)

        assertFalse(result)
    }

//    @Test
//    fun addFailure() {
//        val post = Post(text = "doFail")
//        val addPost = WallService.add(post)
//        assertEquals(0, addedPost.id, "Жолжен провалиться по идее")
//
//    }



}