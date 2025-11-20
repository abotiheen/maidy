package com.example.maidy.feature.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Primary action button for authentication screens
 * 
 * @param text Button label text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier to be applied to the button
 * @param enabled Whether the button is enabled
 * @param isLoading Whether to show loading indicator
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaidyButtonPrimary,
            disabledContainerColor = MaidyButtonDisabled
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaidyTextLight,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaidyTextLight
            )
        }
    }
}

@Preview(name = "Normal Button", showBackground = true)
@Composable
private fun PrimaryButtonNormalPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            PrimaryButton(
                text = "Login",
                onClick = {}
            )
        }
    }
}

@Preview(name = "Loading Button", showBackground = true)
@Composable
private fun PrimaryButtonLoadingPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            PrimaryButton(
                text = "Login",
                onClick = {},
                isLoading = true
            )
        }
    }
}

@Preview(name = "Disabled Button", showBackground = true)
@Composable
private fun PrimaryButtonDisabledPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            PrimaryButton(
                text = "Login",
                onClick = {},
                enabled = false
            )
        }
    }
}

@Preview(name = "Multiple Button States", showBackground = true)
@Composable
private fun PrimaryButtonAllStatesPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryButton(text = "Login", onClick = {})
            PrimaryButton(text = "Create Account", onClick = {})
            PrimaryButton(text = "Confirm", onClick = {}, isLoading = true)
            PrimaryButton(text = "Submit", onClick = {}, enabled = false)
        }
    }
}




