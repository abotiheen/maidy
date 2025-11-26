package com.example.maidy.feature_maid.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maidy.R
import com.example.maidy.feature_maid.auth.MaidAppLogoBackground
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Maid app logo with cleaning emoji - Green themed
 *
 * @param modifier Modifier to be applied to the logo container
 */
@Composable
fun MaidLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaidAppLogoBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.maidy_maid),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(name = "Default Logo")
@Composable
private fun MaidLogoPreview() {
    MaidyTheme {
        MaidLogo()
    }
}

@Preview(name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MaidLogoDarkPreview() {
    MaidyTheme {
        MaidLogo()
    }
}
