package org.vinYtFetch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import okhttp3.OkHttpClient
import okhttp3.Request
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jdk.jshell.Snippet
import okio.IOException
import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaylistSnippet(
    val title: String,
    val resourceId: ResourceId
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResourceId(
    val videoId: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaylistItem(
    val snippet: PlaylistSnippet
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaylistResponse(
    val items: List<PlaylistItem>
)

fun fetchPlaylistTitles(apiKey : String, playlistId: String): List<Pair<String,String>> {
   val client = OkHttpClient()
    val url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=$playlistId&key=$apiKey"

    val request = Request.Builder().url(url).build()

    client.newCall(request).execute().use {
        response ->
        if(!response.isSuccessful) throw IOException("Unexpected code $response")
        val body = response.body?.string() ?: throw IOException("Empty response body")
        val mapper = jacksonObjectMapper()
        val playlistResponse: PlaylistResponse = mapper.readValue(body)

        return playlistResponse.items.map {
            it.snippet.title to  "https://www.youtube.com/watch?v=${it.snippet.resourceId.videoId}"
    }
}
}
fun extractPlaylistId(url : String) : String? {
    val regex = """(?:https?:\/\/)?(?:www\.)?youtube\.com\/playlist\?list=([a-zA-Z0-9_-]+)""".toRegex()
    val match = regex.find(url) ?: return null
    return match.groups?.get(1)?.value
}

fun GUI(titles: List<Pair<String, String>>) {
    val frame = JFrame("Youtube Playlist TItles")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(400, 850)

    val panel = JPanel()
    panel.layout = FlowLayout(FlowLayout.CENTER)


    val HeaderLabel = JLabel("Daftar Judul Video : ")
    HeaderLabel.font = HeaderLabel.font.deriveFont(Font.BOLD)
    panel.add(HeaderLabel)

    val listModel = DefaultListModel<String>()
    titles.forEach { listModel.addElement(it.first) }
    val list = JList(listModel)
    list.selectionMode = ListSelectionModel.SINGLE_SELECTION
    list.layoutOrientation = JList.HORIZONTAL_WRAP
    list.visibleRowCount = -1

    list.addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            val index = list.locationToIndex(e.point)
            if(index != -1 && e.button == MouseEvent.BUTTON1) {
                val url = titles[index].second
                try {
                    Desktop.getDesktop().browse(URI(url))
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    })

    val listScrollPane = JScrollPane(list)
    panel.add(listScrollPane)
    frame.contentPane.add(panel)
    frame.isResizable = false
    frame.isVisible = true

}

fun main() {
    val apiKey = "AIzaSyBorr36ZtWWzNGPXUwD1YCgJ_FbfI6Uk-c"
//    val playlistId = "PL-CtdCApEFH-aC-35fw5qrr6DZ-qMzmRr"
   val playlistInput = JOptionPane.showInputDialog("Masukkan Playlist ID atau URL Youtube")

    val playlistId = playlistInput?.let { extractPlaylistId(it) }

    if(playlistId.isNullOrBlank()) {
        JOptionPane.showMessageDialog(null, "Playlist ID is required")
        return
    }

    val titles = fetchPlaylistTitles(apiKey, playlistId)
//    println("Judul video dalam Playlist : ")
//    titles.forEach {    println(it) }

    SwingUtilities.invokeLater {
       GUI(titles)
    }
}

