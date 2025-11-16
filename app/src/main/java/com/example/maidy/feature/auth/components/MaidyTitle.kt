package com.example.maidy.feature.auth.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.MaidyTextPrimary
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Maidy app title text
 * 
 * @param modifier Modifier to be applied to the text
 */
@Composable
fun MaidyTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Maidy",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MaidyTextPrimary,
        modifier = modifier
    )
}

@Preview(name = "Default Title")
@Composable
private fun MaidyTitlePreview() {
    MaidyTheme {
        MaidyTitle()
    }
}

@Preview(name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MaidyTitleDarkPreview() {
    MaidyTheme {
        MaidyTitle()
    }
}

