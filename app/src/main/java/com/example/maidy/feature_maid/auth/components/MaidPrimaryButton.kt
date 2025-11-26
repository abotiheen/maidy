package com.example.maidy.feature_maid.auth.components

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
import com.example.maidy.feature_maid.auth.*
import com.example.maidy.ui.theme.*

/**
 * Primary action button for maid authentication screens - Green themed with more rounded corners
 *
 * @param text Button label text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier to be applied to the button
 * @param enabled Whether the button is enabled
 * @param isLoading Whether to show loading indicator
 */
@Composable
fun MaidPrimaryButton(
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
        shape = RoundedCornerShape(16.dp), // More rounded than customer app (12dp)
        colors = ButtonDefaults.buttonColors(
            containerColor = MaidAppButtonPrimary,
            disabledContainerColor = MaidAppButtonDisabled
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaidAppTextLight,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaidAppTextLight
            )
        }
    }
}

@Preview(name = "Normal Button", showBackground = true)
@Composable
private fun MaidPrimaryButtonNormalPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidPrimaryButton(
                text = "Login",
                onClick = {}
            )
        }
    }
}

@Preview(name = "Loading Button", showBackground = true)
@Composable
private fun MaidPrimaryButtonLoadingPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidPrimaryButton(
                text = "Login",
                onClick = {},
                isLoading = true
            )
        }
    }
}

@Preview(name = "Disabled Button", showBackground = true)
@Composable
private fun MaidPrimaryButtonDisabledPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidPrimaryButton(
                text = "Login",
                onClick = {},
                enabled = false
            )
        }
    }
}

@Preview(name = "Multiple Button States", showBackground = true)
@Composable
private fun MaidPrimaryButtonAllStatesPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MaidPrimaryButton(text = "Login", onClick = {})
            MaidPrimaryButton(text = "Create Account", onClick = {})
            MaidPrimaryButton(text = "Confirm", onClick = {}, isLoading = true)
            MaidPrimaryButton(text = "Submit", onClick = {}, enabled = false)
        }
    }
}
