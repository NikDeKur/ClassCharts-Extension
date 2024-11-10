package dev.nikdekur.classcharts.ext.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import dev.nikdekur.classcharts.pupil.PupilData

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    data: PupilData
) {
    Card(
        shape = RoundedCornerShape(12.dp),
    ) {

        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = rememberAsyncImagePainter(data.avatarUrl),
                contentDescription = "avatar",
            )

            Column {
                Text(data.name)
                Text(data.id.toString())
            }
        }
    }
}


@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Text("Logout")
    }
}