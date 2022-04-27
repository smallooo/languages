package tm.alashow.datmusic.domain.entities

import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import tm.alashow.datmusic.domain.CoverImageSize
import tm.alashow.datmusic.domain.UNKNOWN_Creator
import tm.alashow.datmusic.domain.UNTITLED_SONG
import tm.alashow.domain.models.BasePaginatedEntity

@Parcelize
@Serializable
@Entity(tableName = "videos")
data class Video(
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: AudioId = "",

    @SerialName("key")
    @ColumnInfo(name = "key")
    val searchKey: String = "",

    @SerialName("source_id")
    @ColumnInfo(name = "source_id")
    val sourceId: String = "",

    @SerialName("creator")
    @ColumnInfo(name = "creator")
    val creator: String = UNKNOWN_Creator,

    @SerialName("title")
    @ColumnInfo(name = "title")
    val title: String = UNTITLED_SONG,

    @SerialName("duration")
    @ColumnInfo(name = "duration")
    val duration: Int = 0,

    @SerialName("date")
    @ColumnInfo(name = "date")
    val date: Long = 0L,

    @SerialName("cover_url")
    @ColumnInfo(name = "cover_url")
    val coverUrl: String? = null,

    @SerialName("cover_url_medium")
    @ColumnInfo(name = "cover_url_medium")
    val coverUrlMedium: String? = null,

    @SerialName("cover_url_small")
    @ColumnInfo(name = "cover_url_small")
    val coverUrlSmall: String? = null,

    @SerialName("cover")
    @ColumnInfo(name = "cover")
    val coverAlternate: String = "",

    @SerialName("download")
    @ColumnInfo(name = "download")
    val downloadUrl: String? = null,

    @SerialName("stream")
    @ColumnInfo(name = "stream")
    val streamUrl: String? = null,

    @Transient
    @ColumnInfo(name = "params")
    override var params: String = defaultParams,

    @Transient
    @ColumnInfo(name = "page")
    override var page: Int = defaultPage,

    @PrimaryKey
    val primaryKey: String = "",

    @Transient
    @ColumnInfo(name = "search_index")
    val searchIndex: Int = 0,
) : BasePaginatedEntity(), Parcelable {

    override fun getIdentifier() = id

    @Ignore
    @Transient
    @IgnoredOnParcel
    var videoDownloadItem: VideoDownloadItem? = null

    fun coverUri(size: CoverImageSize = CoverImageSize.LARGE, allowAlternate: Boolean = true): Uri = (
        when (size) {
            CoverImageSize.LARGE -> coverUrl
            CoverImageSize.MEDIUM -> coverUrlMedium
            CoverImageSize.SMALL -> coverUrlSmall
        } ?: coverUrl ?: (if (allowAlternate) coverAlternate.toUri().buildUpon().appendPath(size.type).toString() else "")
        ).toUri()

    fun durationMillis() = (duration * 1000).toLong()

    fun fileDisplayName() = "$creator - $title"

}
