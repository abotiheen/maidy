package com.example.maidy.feature.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
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
import com.example.maidy.ui.theme.*

/**
 * Standard text field for authentication screens
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
fun AuthTextField(
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
            color = MaidyTextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaidyTextSecondary,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaidyBorderFocused,
                unfocusedBorderColor = MaidyBorderLight,
                focusedContainerColor = MaidyBackgroundWhite,
                unfocusedContainerColor = MaidyBackgroundWhite
            ),
            visualTransformation = if (isPassword && !passwordVisible) 
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onPasswordVisibilityToggle?.invoke() }) {
                        Icon(
                            painter = painterResource(R.drawable.visibility),
                            contentDescription = if (passwordVisible) 
                                "Hide password" else "Show password",
                            tint = MaidyTextSecondary
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
private fun AuthTextFieldEmptyPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            AuthTextField(
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
private fun AuthTextFieldFilledPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            AuthTextField(
                value = "John Doe",
                onValueChange = {},
                label = "Full Name",
                placeholder = "Enter your full name"
            )
        }
    }
}

@Preview(name = "Password Hidden", showBackground = true)
@Composable
private fun AuthTextFieldPasswordHiddenPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            AuthTextField(
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
private fun AuthTextFieldPasswordVisiblePreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            AuthTextField(
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


