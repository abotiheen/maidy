package com.example.maidy.feature.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.MaidyBlue
import com.example.maidy.ui.theme.MaidyTextSecondary
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Terms and conditions acceptance text with clickable link
 * 
 * @param modifier Modifier to be applied to the text container
 * @param onTermsClick Callback when terms link is clicked (optional)
 */
@Composable
fun TermsAndConditionsText(
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
            color = MaidyTextSecondary
        )
        Text(
            text = "Terms & Conditions",
            fontSize = 12.sp,
            color = MaidyBlue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onTermsClick?.invoke() }
        )
    }
}

@Preview(name = "Default Terms Text", showBackground = true)
@Composable
private fun TermsAndConditionsTextPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            TermsAndConditionsText()
        }
    }
}

@Preview(name = "Terms Text with Callback", showBackground = true)
@Composable
private fun TermsAndConditionsTextWithCallbackPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            TermsAndConditionsText(
                onTermsClick = { /* Would open terms screen */ }
            )
        }
    }
}


