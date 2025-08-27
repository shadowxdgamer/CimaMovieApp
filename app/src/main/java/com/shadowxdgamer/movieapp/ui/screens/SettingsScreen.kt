package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    // We need the UriHandler again to open web links
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Maybe add in some future update
//        item { SectionHeader("EMBED SETTINGS") }
//        item {
//            SettingsItem(
//                icon = Icons.Default.Movie,
//                title = "Movie",
//                onClick = { /* Non-functional for now */ }
//            )
//        }
//        item {
//            SettingsItem(
//                icon = Icons.Default.Tv,
//                title = "TV",
//                onClick = { /* Non-functional for now */ }
//            )
//        }
//
//        item { Spacer(Modifier.height(16.dp)) }
//
//        item { SectionHeader("RESOURCES") }
//        item {
//            SettingsItem(
//                icon = Icons.Default.Download,
//                title = "Downloads",
//                onClick = { /* Non-functional for now */ }
//            )
//        }
//
//        item { Spacer(Modifier.height(16.dp)) }

        item { SectionHeader("CONTACT") }
        item {
            SettingsItem(
                icon = Icons.Default.Code, // GitHub Icon
                title = "GitHub",
                onClick = {
                    uriHandler.openUri("https://github.com/shadowxdgamer")
                }
            )
        }
        item {
            SettingsItem(
                icon = Icons.Default.Person, // LinkedIn Icon
                title = "LinkedIn",
                onClick = {
                    uriHandler.openUri("https://www.linkedin.com/in/shadowxdgamer")
                }
            )
        }
        item {
            SettingsItem(
                icon = Icons.Default.Support,
                title = "Support",
                onClick = {
                    navController.navigate("support")
                }
            )
        }
    }
}

// Helper for the gray section titles (no changes)
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// Helper for the clickable list items (no changes)
@Composable
private fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}