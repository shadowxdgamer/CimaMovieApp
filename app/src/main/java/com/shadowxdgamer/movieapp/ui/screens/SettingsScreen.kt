package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        ListItem(
            headlineContent = { Text("Support the Developer") },
            supportingContent = { Text("Buy me a coffee ☕") },
            modifier = Modifier.clickable {
                uriHandler.openUri("https://www.buymeacoffee.com/shadowxdgamer")
            }
        )
        ListItem(
            headlineContent = { Text("GitHub") },
            supportingContent = { Text("View the source code") },
            leadingContent = { Icon(Icons.Default.Code, contentDescription = null) },
            modifier = Modifier.clickable {
                uriHandler.openUri("https://github.com/shadowxdgamer")
            }
        )
        ListItem(
            headlineContent = { Text("LinkedIn") },
            supportingContent = { Text("Contact me") },
            leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.clickable {
                uriHandler.openUri("https://www.linkedin.com/in/yassin-chaabani")
            }
        )
    }
}