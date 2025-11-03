data class Message(
    val id: Int,
    val senderId: Int,
    var text: String,
    var isRead: Boolean = false
)

data class Chat(
    val userId: Int,
    val message: MutableList<Message> = mutableListOf()
)

class ChatService {
    private val chats = mutableMapOf<Int, Chat>()
    private var messageIdCounter = 0

    fun getChatsCount(): Int =
        chats.values.asSequence()
            .filter { chat -> chat.message.any { !it.isRead } }
            .count()

    fun getChats(): List<Chat> = chats.values.toList()

    fun getLastMessage(): List<String> =
        chats.values.asSequence()
            .map { chat -> chat.message.lastOrNull()?.text ?: "Нет Сообщения" }
            .toList()

    fun getMessages(userId: Int, count: Int): List<Message> {
        val chat = chats[userId] ?: throw IllegalArgumentException("Чат не найден")
        val messages = chat.message
        val start = (messages.size - count).coerceAtLeast(0)

        return messages.asSequence()
            .drop(start)
            .onEach { it.isRead = true }
            .toList()
    }

    fun sendMessage(toUserId: Int, fromUserId: Int, text: String) {
        val chat = chats.getOrPut(toUserId) { Chat(toUserId) }
        chat.message += Message(++messageIdCounter, fromUserId, text)
    }

    fun deleteMessage(userId: Int, messageId: Int) {
        val chat = chats[userId] ?: throw IllegalArgumentException("Чат не найден")
        chat.message.removeIf { it.id == messageId }
    }

    fun deleteChat(userId: Int) {
        chats.remove(userId)
    }
}

fun List<Message>.unreadCount(): Int =
    asSequence().count { !it.isRead }

fun List<Chat>.withUnread(): List<Chat> =
    asSequence().filter { it.message.unreadCount() > 0 }.toList()