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
        val post = Post(id = 1, text = "Post someone")
        val result = service.update(post)

        assertFalse(result)
    }

    @Test
    fun `should add comment to existing post`() {
        val service = WallService()
        val post = service.add(Post(text = "Привет"))
        val comment = Comment(id = 0, fromId = 1, date = 12_12_2025, text = "Комментарий",1)

        val result = service.createComment(post.id, comment)

        assertEquals(1, result.id)
        assertEquals("Комментарий", result.text)
    }

    @Test
    fun `should throw PostNotFoundException when post not found`() {
        val service = WallService()
        val comment = Comment(id = 0, fromId = 1, date = 12_12_2025, text = "Комментарий",1)

        assertThrows(PostNotFoundException::class.java) {
            service.createComment(1, comment)
        }
    }

//    @Test
//    fun addFailure() {
//        val post = Post(text = "doFail")
//        val addPost = WallService.add(post)
//        assertEquals(0, addedPost.id, "Жолжен провалиться по идее")
//
//    }



}