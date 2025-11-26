package com.example.maidy.feature_maid.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.MaidAppGreen
import com.example.maidy.feature_maid.auth.MaidAppTextSecondary
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Terms and conditions acceptance text with clickable link - Green themed
 *
 * @param modifier Modifier to be applied to the text container
 * @param onTermsClick Callback when terms link is clicked (optional)
 */
@Composable
fun MaidTermsAndConditionsText(
    modifier: Modifier = Modifier,
    onTermsClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "By continuing, you agree to our ",
            fontSize = 12.sp,
            color = MaidAppTextSecondary
        )
        Text(
            text = "Terms & Conditions",
            fontSize = 12.sp,
            color = MaidAppGreen,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onTermsClick?.invoke() }
        )
    }
}

@Preview(name = "Default Terms Text", showBackground = true)
@Composable
private fun MaidTermsAndConditionsTextPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidTermsAndConditionsText()
        }
    }
}

@Preview(name = "Terms Text with Callback", showBackground = true)
@Composable
private fun MaidTermsAndConditionsTextWithCallbackPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidTermsAndConditionsText(
                onTermsClick = { /* Would open terms screen */ }
            )
        }
    }
}
