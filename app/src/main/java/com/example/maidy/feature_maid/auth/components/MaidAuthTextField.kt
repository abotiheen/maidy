package com.example.maidy.feature_maid.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.feature_maid.auth.*
import com.example.maidy.ui.theme.*

/**
 * Standard text field for maid authentication screens - Green themed with more rounded corners
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param label Label text displayed above the field
 * @param placeholder Placeholder text shown when field is empty
 * @param modifier Modifier to be applied to the field container
 * @param keyboardType Type of keyboard to show
 * @param isPassword Whether this is a password field (enables visibility toggle)
 * @param passwordVisible Whether password is currently visible (only for password fields)
 * @param onPasswordVisibilityToggle Callback when password visibility is toggled
 */
@Composable
fun MaidAuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaidAppTextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaidAppTextSecondary,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), // More rounded than customer app (12dp)
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaidAppBorderFocused,
                unfocusedBorderColor = MaidAppBorderLight,
                focusedContainerColor = MaidAppBackgroundWhite,
                unfocusedContainerColor = MaidAppBackgroundWhite
            ),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onPasswordVisibilityToggle?.invoke() }) {
                        Icon(
                            painter = if (passwordVisible) painterResource(R.drawable.visibilit_off) else painterResource(
                                R.drawable.visibility
                            ),
                            contentDescription = if (passwordVisible)
                                "Hide password" else "Show password",
                            tint = MaidAppTextSecondary
                        )
                    }
                }
            } else null,
            singleLine = true
        )
    }
}

@Preview(name = "Empty Field", showBackground = true)
@Composable
private fun MaidAuthTextFieldEmptyPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidAuthTextField(
                value = "",
                onValueChange = {},
                label = "Full Name",
                placeholder = "Enter your full name"
            )
        }
    }
}

@Preview(name = "Filled Field", showBackground = true)
@Composable
private fun MaidAuthTextFieldFilledPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidAuthTextField(
                value = "Jane Smith",
                onValueChange = {},
                label = "Full Name",
                placeholder = "Enter your full name"
            )
        }
    }
}

@Preview(name = "Password Hidden", showBackground = true)
@Composable
private fun MaidAuthTextFieldPasswordHiddenPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidAuthTextField(
                value = "password123",
                onValueChange = {},
                label = "Password",
                placeholder = "Enter your password",
                isPassword = true,
                passwordVisible = false,
                onPasswordVisibilityToggle = {}
            )
        }
    }
}

@Preview(name = "Password Visible", showBackground = true)
@Composable
private fun MaidAuthTextFieldPasswordVisiblePreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidAuthTextField(
                value = "password123",
                onValueChange = {},
                label = "Password",
                placeholder = "Enter your password",
                isPassword = true,
                passwordVisible = true,
                onPasswordVisibilityToggle = {}
            )
        }
    }
}
