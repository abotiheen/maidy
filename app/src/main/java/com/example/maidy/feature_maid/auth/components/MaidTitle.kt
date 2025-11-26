package com.example.maidy.feature_maid.auth.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Maid app title text - Green themed
 *
 * @param modifier Modifier to be applied to the text
 */
@Composable
fun MaidTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Maidy for Maids",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MaidAppTextPrimary,
        modifier = modifier
    )
}

@Preview(name = "Default Title")
@Composable
private fun MaidTitlePreview() {
    MaidyTheme {
        MaidTitle()
    }
}

@Preview(name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MaidTitleDarkPreview() {
    MaidyTheme {
        MaidTitle()
    }
}
