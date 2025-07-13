package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(videoUrl: String,onServerClick: (String) -> Unit) {
    val context = LocalContext.current
    var servers by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedServerUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(videoUrl) {
        withContext(Dispatchers.IO) {
            try {
                val playUrl = videoUrl.replace("watch", "play")
                val doc = Jsoup.connect(playUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get()

                val buttons = doc.select("li[id^=server_]")
                val serverList = buttons.mapNotNull {
                    val name = it.selectFirst("strong")?.text() ?: return@mapNotNull null
                    val rawEmbed = it.attr("data-embed")
                    val srcRegex = Regex("src='([^']+)'")
                    val match = srcRegex.find(rawEmbed)
                    val link = match?.groupValues?.get(1) ?: return@mapNotNull null
                    name to link
                }

                servers = serverList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Choose a Server") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                servers.isEmpty() -> Text("No servers found.", Modifier.align(Alignment.Center))
                else -> LazyColumn {
                    items(servers) { (name, link) ->
                        ListItem(
                            headlineContent = { Text(name) },
                            modifier = Modifier.clickable {
                                onServerClick(link)
                            }
                        )
                    }
                }
            }
        }
    }
}
