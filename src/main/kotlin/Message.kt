data class Message(
    val id: Int, // ид сообщения
    val senderId: Int, // ид отправителя
    var text: String, //текст сообщения
    var isRead: Boolean = false //Прочитано или нет
)

data class Chat(
    val userId: Int,
    val message: MutableList<Message> = mutableListOf()
)

class ChatService {
    private val chats = mutableMapOf<Int, Chat>()
    private var messageIdCounter = 0

    fun getChatsCount(): Int = chats.values.count { chats -> chats.message.any { !it.isRead } }
    fun getChats(): List<Chat> = chats.values.toList()
    fun getLastMessage(): List<String> = chats.values.map { chat -> chat.message.lastOrNull()?.text ?: "Нет Сообщения" }
    fun getMessages(userId: Int, count: Int): List<Message> {
        val chat = chats[userId] ?: throw IllegalArgumentException("Чат не найден")
        val messages = chat.message.takeLast(count)
        messages.forEach { it.isRead = true }
        return messages
    }
    fun sendMessage(toUserId: Int, fromUserId: Int, text: String) {
        val chat = chats.getOrPut(toUserId) {Chat(toUserId)}
        chat.message += Message(++messageIdCounter, fromUserId,text)
    }
    fun deleteMessage(userId: Int, messageId: Int) {
        val chat = chats[userId] ?: throw IllegalArgumentException("Чат не найден")
        chat.message.removeIf { it.id == messageId }
    }
    fun deleteChat(userId: Int) {
        chats.remove(userId)
    }
}

fun List<Message>.unreadCount(): Int = count {!it.isRead}
fun List<Chat>.withUnread(): List<Chat> = filter { it.message.unreadCount() > 0 }