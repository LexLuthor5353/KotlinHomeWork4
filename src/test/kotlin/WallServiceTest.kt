import org.junit.Assert.*
import org.junit.Test
import org.junit.Before

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
    fun addCommentToExistingPost() {
        val service = WallService()
        val post = service.add(Post(text = "Привет"))
        val comment = Comment(0, 1, "12.12.2025", "Комментарий", post.id, 1, false)

        val result = service.createComment(post.id, comment)

        assertEquals(1, result.id)
        assertEquals("Комментарий", result.text)
    }

    @Test(expected = PostNotFoundException::class)
    fun postNotFound() {
        val service = WallService()
        val comment = Comment(0, 1, "12.12.2025", "Комментарий", 1, 1, false)

        service.createComment(999, comment)
    }

    class MethodsTest {

        private lateinit var repo: Methods<Notes>

        @Before
        fun setup() {
            repo = Methods()
            repo.add(Notes(1, 100, false, "Заметка", "Текст", "22.10.2025", null, null))
        }

        @Test
        fun addStoreItem() {
            val result = repo.getAll()
            assertEquals(1, result.size)
            assertEquals("Заметка", result[0].title)
        }

        @Test
        fun deleteItemAsDeleted() {
            val success = repo.delete(1)
            assertTrue(success)
            assertTrue(repo.findeById(1)?.isDeleted == true)
        }

        @Test
        fun restoreDeletedItem() {
            repo.delete(1)
            val success = repo.restore(1)
            assertTrue(success)
            assertFalse(repo.findeById(1)?.isDeleted ?: true)
        }

        @Test(expected = AlreadyDeletedException::class)
        fun deleteIfAlreadyDeleted() {
            repo.delete(1)
            repo.delete(1)
        }

        @Test(expected = NotDeletedException::class)
        fun restoreIfNotDeleted() {
            repo.restore(1)
        }

        @Test(expected = ItemNotFoundException::class)
        fun deleteIfItemNotFound() {
            repo.delete(999)
        }

        @Test(expected = ItemNotFoundException::class)
        fun restoreIfItemNotFound() {
            repo.restore(999)
        }
    }
}