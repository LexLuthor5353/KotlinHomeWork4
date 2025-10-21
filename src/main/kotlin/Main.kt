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
    val url : String,
    val withPadding: Int
)

data class PhotoAttachment(val photo: Photo): Attachment {
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

data class VideoAttachment(val video: Video): Attachment {
    override val type = "video"
}

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val image: Array<ImagePreview>
)

data class AudioAttachment (val audio: Audio):Attachment{
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

data class FileAttachment (val file: File): Attachment {
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
    val id: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val postId: Int
)

interface Deletable {
    val id: Int
    val isDeleted: Boolean
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
): Deletable

class Methods<T: Deletable> {
    private val items = mutableListOf<T>()
    fun add(item: T): T {
        items += item
        return item
    }
    fun getAll(): List<T> = items.filter {!it.isDeleted}
    fun findeById(id: Int):T? = items.find {it.id == id}
    fun delete(id: Int): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) {return false}
        val item = items[index]
        if (item.isDeleted) {return false}
        //по идее тут нужно сделать копию и подифицировать у нее признак удление = тру
        return true
    }

    fun restore(id: Int): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if(index == -1) {return false}
        val item = items[index]
        if (!item.isDeleted) {return false}
        //по идее тут нужно сделать копию и подифицировать у нее признак удление = тру
        return true
    }
}

fun main() {
    val notsMethod = Methods<Notes>()
    val note1 = notsMethod.add(Notes(1, 1,false,"Тест Заметка", "ЧТо то тестовое", "21.10.2025", null,null))
    val note2 = notsMethod.add(Notes(2,2,false,"Hello notes","Hello my comrade notes","21.10.2025",null,5))

//    println(notsMethod.getAll())
    notsMethod.delete(1)
//    println(notsMethod.getAll())
//    notsMethod.restore(1)
    println(notsMethod.getAll())
}


class PostNotFoundException(message: String): RuntimeException(message)

class WallService {
    private val posts = mutableListOf<Post>()
    private var nextId = 1
//    private val comments = mutableListOf<Comment>()?: throw PostNotFoundException("Пост с id $postId  не найен ")
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
        val postExist = posts.any {it.id == postId}
        if (!postExist) {
            throw PostNotFoundException("Пост с id $postId  не найен")
        }
        val commentWithId = comment.copy(id = nextCommentId++, postId = postId)
        comments += commentWithId
        return commentWithId

    }

}