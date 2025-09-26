import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WallServiceTest {
    @Test
    fun add() {
        val post = Post(text = "Hello user!")
        val addedPost = WallService.add(post)

        assertNotEquals(0, addedPost.id)
    }

    @Test
    fun update() {
        val original = WallService.add(Post(text = "Orig"))
        val updated = original.copy(text = "Updat")

        val result = WallService.update(updated)

        assertTrue(result)

    }
    @Test
    fun updateReturnFalse() {
        val post = Post(id = 999, text = "Post someone")

        val result = WallService.update(post)

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