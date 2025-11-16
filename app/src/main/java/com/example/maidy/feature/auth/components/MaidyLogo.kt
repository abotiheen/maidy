package com.example.maidy.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.MaidyLogoBackground
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Maidy app logo with cleaning emoji
 * 
 * @param modifier Modifier to be applied to the logo container
 */
@Composable
fun MaidyLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaidyLogoBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🧹",
            fontSize = 40.sp
        )
    }
}

@Preview(name = "Default Logo")
@Composable
private fun MaidyLogoPreview() {
    MaidyTheme {
        MaidyLogo()
    }
}

@Preview(name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MaidyLogoDarkPreview() {
    MaidyTheme {
        MaidyLogo()
    }
}

