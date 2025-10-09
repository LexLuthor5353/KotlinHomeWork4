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