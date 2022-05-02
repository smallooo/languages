package com.alashow.datmusic.data

import com.alashow.datmusic.ui.library.R
import java.io.Serializable



object AlbumsDataProvider {

    val listOfSpotifyHomeLanes = listOf(
        "Continue listening",
        "Popular Playlists",
        "Top Charts",
        "Recommended for today",
        "Bollywood",
        "Acoustic only"
    )

    val album = Album(
        id = 0,
        artist = "Adele",
        song = "Someone like you",
        descriptions = "Album by Adele-2016",
        imageId = R.drawable.adele21
    )

    val albums = mutableListOf(
        Album(
            id = 1,
            artist = "Ed Sheeran",
            song = "Perfect",
            descriptions = "Album by Ed Sheeran-2016",
            imageId = R.drawable.adele21,
            genre = "Pop"
        ),
        Album(
            id = 2,
            artist = "Camelia Cabello",
            song = "Havana",
            descriptions = "Album by Camelia Cabello-2016",
            imageId = R.drawable.adele21,
            genre = "R&B"
        ),
        Album(
            id = 3,
            artist = "BlackPink",
            song = "Kill this love",
            descriptions = "Album by BlackPink-2016",
            imageId = R.drawable.adele21,
            genre = "K-pop"
        ),
        Album(
            id = 4,
            artist = "Ed Sheeran",
            song = "Photograph",
            descriptions = "Album by Ed Sheeran-2016",
            imageId = R.drawable.adele21,
            genre = "Acoustic"
        ),
        Album(
            id = 5,
            artist = "Lana del rey",
            song = "Born to die",
            descriptions = "Album by Lana del ray-2014",
            imageId = R.drawable.adele21,
            genre = "Jazz"
        ),
        Album(
            id = 6,
            artist = "Khalid",
            song = "Location",
            descriptions = "Album by Khalid-2019",
            imageId = R.drawable.adele21,
            genre = "RnB"
        ),
        Album(
            id = 7,
            artist = "Adele",
            song = "Hello",
            descriptions = "Album by Adele-2019",
            imageId = R.drawable.adele21,
            genre = "Pop"
        ),
        Album(
            id = 8,
            artist = "Sam Smith",
            song = "Stay With Me",
            descriptions = "Album by Ed Sheeran-2016",
            imageId = R.drawable.adele21,
            genre = "Pop"
        ),
        Album(
            id = 9,
            artist = "Billie Eilish",
            song = "Bad Guy",
            descriptions = "Album by Billie Eilish-2016",
            imageId = R.drawable.adele21,
            genre = "Pop"
        ),
        Album(
            id = 10,
            artist = "Dua Lipa",
            song = "Break My Heart",
            descriptions = "Album by Dua Lipa-2016",
            imageId = R.drawable.adele21,
            genre = "Music"
        ),
        Album(
            id = 11,
            artist = "Tones & I",
            song = "Dance Monkey",
            descriptions = "Album by Tones & I-2019",
            imageId = R.drawable.adele21,
            genre = "Party"
        ),
        Album(
            id = 12,
            artist = "Marshmello",
            song = "Happier",
            descriptions = "Album by Marshmello-2016",
            imageId = R.drawable.adele21,
            genre = "DJ"
        ),
        Album(
            id = 13,
            artist = "Eminem",
            song = "The Eminem Show",
            descriptions = "Album by Eminem-2019",
            imageId = R.drawable.adele21,
            genre = "Rap"
        ),
        Album(
            id = 14,
            artist = "The Weekend",
            song = "Starboy",
            descriptions = "Album by The Weekend-2016",
            imageId = R.drawable.adele21,
            genre = "Mood"
        ),
        Album(
            id = 15,
            artist = "Katy Perry",
            song = "Smile",
            descriptions = "Album by Katy Perry-2016",
            imageId = R.drawable.adele21,
            genre = "Chill"
        ),
        Album(
            id = 16,
            artist = "Shawn Mendes",
            song = "Senorita",
            descriptions = "Album by Shawn Mendes-2016",
            imageId = R.drawable.adele21,
            genre = "Latin"
        ),
        Album(
            id = 17,
            artist = "Selena Gomez",
            song = "Wolves",
            descriptions = "Album by Selena Gomez-2016",
            imageId = R.drawable.adele21,
            genre = "Rock"
        ),
        Album(
            id = 18,
            artist = "Adele",
            song = "Someone Like You",
            descriptions = "Album by Adele 21-2016",
            imageId = R.drawable.adele21,
            genre = "Solo"
        ),
        Album(
            id = 19,
            artist = "Imagine Dragon",
            song = "Believer",
            descriptions = "Album by Imagine Dragon-2017",
            imageId = R.drawable.adele21,
            genre = "Pop"
        ),
        Album(
            id = 20,
            artist = "James Arthur",
            song = "Impossible",
            descriptions = "Album by James Arthur-2016",
            imageId = R.drawable.adele21,
            genre = "Pop"
        ),
    )
}
