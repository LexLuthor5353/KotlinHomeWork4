data class Post(
    val original: Post? = null,
    val id: Int = 0,
    val ownerId: Int = 0,
    val fromId: Int = 0,
    val date: Int = 0,
    val text: String,
    val friendsOnly: Boolean = false,
    val isPinned: Boolean = false,
    val markedAsAds: Boolean = false,
    val postType: String = "post",
    val isFavorite: Boolean = false,
    val comments: Comments = Comments(),
    val likes: Likes = Likes(),
    val attachments: List<Attachment>? = null
)

data class Comments(
    val count: Int = 0,
    val canPost: Boolean = false,
    val groupsCanPost: Boolean = false,
    val canClose: Boolean = false,
    val canOpen: Boolean = false
)

data class Likes(
    val count: Int = 0,
    val userLikes: Boolean = false,
    val canLike: Boolean = false,
    val canPublish: Boolean = false
)

interface Attachment {
    val type: String
}

data class ImagePreview(
    val height: Int,
    val width: Int,
    val url: String,
    val withPadding: Int
)

data class PhotoAttachment(val photo: Photo) : Attachment {
    override val type = "photo"
}

data class Photo(
    val id: Int,
    val ownerId: Int,
    val album_id: Int,
    val text: String,
    val date: Int,
    val photo130: String,
    val photo604: String,
    val photo807: String
)

data class VideoAttachment(val video: Video) : Attachment {
    override val type = "video"
}

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val image: Array<ImagePreview>
)

data class AudioAttachment(val audio: Audio) : Attachment {
    override val type = "audio"
}

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val duration: Int,
    val url: String,
    val genreid: Int,
    val date: Int
)

data class FileAttachment(val file: File) : Attachment {
    override val type = "file"
}

data class File(
    val id: Int,
    val ownerid: Int,
    val title: String,
    val size: Int,
    val ext: String,
    val url: String,
    val date: Int,
    val type: Int
)

data class Comment(
    override val id: Int, //ид коммента
    val fromId: Int, //ид автора комметария
    val date: String, //Дата комметрия(Так же переделан на строку)
    val text: String, //Текс комментария
    val postId: Int, //ИД поста
    val noteId: Int?, //   ИД заметки
    override val isDeleted: Boolean// пометка на удаление
) : Deletable {
    override fun toggleDeletable(): Deletable {
        return this.copy(isDeleted = !isDeleted)
    }
}

interface Deletable {
    val id: Int
    val isDeleted: Boolean
    fun toggleDeletable(): Deletable
}

data class Notes(
    override val id: Int, // ид заметки
    val ownerId: Int, // ИД владельца заметки
    override val isDeleted: Boolean, //Помечена на удалени (По дефолту фолс)
    val title: String, //наименование заметки
    val text: String, // текст заметки
    val addDate: String, //Дата добавления заметки. Переставил на строку что бы нормально вывести дату в консоль, а не сполошным числом
    val editDate: Int?, // Дата реадактирования. Надо понять как выполнить проверку на редактирование и не отображать это поле если не было редактирования
    val countComment: Int? // Количество комментов к заметке. Тоже самое что и дата редактирования. Временно нулабл
) : Deletable {
    override fun toggleDeletable(): Deletable {
        return this.copy(isDeleted = !isDeleted)
    }
}

class Methods<T : Deletable> {
    private val items = mutableListOf<T>()
    fun add(item: T): T {
        items += item
        return item
    }

    fun getAll(): List<T> = items.filter { !it.isDeleted }
    fun findeById(id: Int): T? = items.find { it.id == id }
    fun delete(id: Int): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) throw ItemNotFoundException("Объект с id $id не найден")
        val item = items[index]
        if (item.isDeleted) throw AlreadyDeletedException("Объект с id $id уже удалён")
        items[index] = item.toggleDeletable() as T
        return true
    }

    fun restore(id: Int): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) throw ItemNotFoundException("Объект с id $id не найден")
        val item = items[index]
        if (!item.isDeleted) throw NotDeletedException("Объект с id $id не был удалён")
        items[index] = item.toggleDeletable() as T
        return true
    }
}

class PostNotFoundException(message: String) : RuntimeException(message)

class WallService {
    private val posts = mutableListOf<Post>()
    private var nextId = 1
    private val comments = mutableListOf<Comment>()
    private var nextCommentId = 1

    fun add(post: Post): Post {
        val postWithId = post.copy(id = nextId++)
        posts += postWithId
        return postWithId
    }

    fun update(post: Post): Boolean {
        for ((index, existingPost) in posts.withIndex()) {
            if (existingPost.id == post.id) {
                posts[index] = post.copy(id = existingPost.id)
                return true
            }
        }
        return false
    }
    fun createComment(postId: Int, comment: Comment): Comment {
        val postExist = posts.any { it.id == postId }
        if (!postExist) {
            throw PostNotFoundException("Пост с id $postId  не найен")
        }
        val commentWithId = comment.copy(id = nextCommentId++, postId = postId)
        comments += commentWithId
        return commentWithId
    }
}

class NoteService(
    private val notes: Methods<Notes> = Methods(),
    private val comments: Methods<Comment> = Methods()
) {
    private var nextCommentId = 1

    fun addNote(note: Notes): Notes = notes.add(note)
    fun getNotes(): List<Notes> = notes.getAll()
    fun addCommentToNote(noteId: Int, comment: Comment): Comment {
        val note = notes.findeById(noteId)
        if (note == null || note.isDeleted) {
            throw IllegalArgumentException("Заметка с id $noteId не найдена или удалена")
        }
        val commentWithId = comment.copy(id = nextCommentId++, noteId = noteId)
        return comments.add(commentWithId)
    }

    fun getCommentsForNote(noteId: Int): List<Comment> = comments.getAll().filter { it.noteId == noteId }
}

class AlreadyDeletedException(message: String) : RuntimeException(message)
class NotDeletedException(message: String) : RuntimeException(message)
class ItemNotFoundException(message: String) : RuntimeException(message)

fun main() {
    val notesRepository = Methods<Notes>()
    val notesService = NoteService(notesRepository)
    val note1 = notesRepository.add(Notes(1, 1, false, "Тест Заметка", "ЧТо то тестовое", "21.10.2025", null, null))
    val note2 = notesRepository.add(Notes(2, 2, false, "Hello notes", "Hello my comrade notes", "21.10.2025", null, 5))
    val comment = notesService.addCommentToNote(1, comment = Comment(5, 15, "22.10.2025", "Тестовый коммент", 1, 1, false))

    try {
        notesRepository.delete(1)
        notesRepository.delete(1) //проверка на повторное удаление

    } catch (e: AlreadyDeletedException) {
        println("Ошибка: ${e.message}")
    }

    try {
        notesRepository.restore(5)// восстановление не существующего объекта
    } catch (e: ItemNotFoundException) {
        println("Ошибка: ${e.message}")
    }
    try {
        notesRepository.restore(1)
        notesRepository.restore(1)// повторное восстановление одного и тогоже
    } catch (e: NotDeletedException) {
        println("Ошибка: ${e.message}")
    }


    println(notesRepository.getAll())
//    notesRepository.delete(1)
//    println(notesRepository.getAll())
//    notesRepository.restore(1)
    println(notesService.getCommentsForNote(1))
}