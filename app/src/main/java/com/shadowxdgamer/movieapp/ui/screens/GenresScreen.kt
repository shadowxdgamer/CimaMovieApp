//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun GenresScreen(
//    genres: List<TmdbGenre>, // define model below
//    onGenreClick: (TmdbGenre) -> Unit
//) {
//    LazyColumn(
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(genres, key = { it.id }) { genre ->
//            Text(
//                text = genre.name,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onGenreClick(genre) }
//                    .padding(vertical = 8.dp)
//            )
//        }
//    }
//}
