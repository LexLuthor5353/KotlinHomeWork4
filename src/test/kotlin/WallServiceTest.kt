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

    class ChatServiceTest {

        private lateinit var service: ChatService

        @Before
        fun setup() {
            service = ChatService()
        }

        @Test
        fun `create chat and send message`() {
            service.sendMessage(toUserId = 1, fromUserId = 2, text = "Hello")
            val chats = service.getChats()
            assertEquals(1, chats.size)
            assertEquals("Hello", chats[0].message[0].text)
        }

        @Test
        fun `count unread chats`() {
            service.sendMessage(1, 2, "Прив")
            service.sendMessage(2, 1, "Yo")
            assertEquals(2, service.getChatsCount())
        }

        @Test
        fun `return last messages or placeholder`() {
            service.sendMessage(1, 2, "Первыйн")
            service.deleteChat(1)
            val messages = service.getLastMessage()
            assertTrue(messages.contains("нет сообщений"))
        }

        @Test
        fun `mark messages`() {
            service.sendMessage(1, 2, "Один")
            service.sendMessage(1, 2, "Два")
            val messages = service.getMessages(1, 2)
            assertTrue(messages.all { it.isRead })
        }

        @Test
        fun `delete message`() {
            service.sendMessage(1, 2, "Удалено")
            val messageId = service.getMessages(1, 1)[0].id
            service.deleteMessage(1, messageId)
            val messages = service.getMessages(1, 1)
            assertTrue(messages.isEmpty())
        }

        @Test
        fun `throw when getting messages`() {
            try {
                service.getMessages(42, 1)
                fail("Expected IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                assertEquals("Чат не найден", e.message)
            }
        }

        @Test
        fun `delete chat`() {
            service.sendMessage(1, 2, "Bye")
            service.deleteChat(1)
            assertTrue(service.getChats().isEmpty())
        }
    }


}