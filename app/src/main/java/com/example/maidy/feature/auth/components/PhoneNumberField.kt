package com.example.maidy.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Phone number input field with Iraq country code (+964)
 * 
 * @param phoneNumber Current phone number value
 * @param onPhoneNumberChange Callback when phone number changes
 * @param modifier Modifier to be applied to the field container
 */
@Composable
fun PhoneNumberField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Phone Number",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaidyTextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TextFieldDefaults.MinHeight)
                .border(
                    width = 1.dp,
                    color = MaidyBorderLight,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(MaidyBackgroundWhite, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+964",
                fontSize = 14.sp,
                color = MaidyTextPrimary,
                modifier = Modifier.padding(end = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp),
                thickness = DividerDefaults.Thickness, color = MaidyBorderLight
            )
            
            BasicTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaidyTextPrimary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (phoneNumber.isEmpty()) {
                            Text(
                                text = "770 123 4567",
                                color = MaidyTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Preview(name = "Empty Phone Field", showBackground = true)
@Composable
private fun PhoneNumberFieldEmptyPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            PhoneNumberField(
                phoneNumber = "",
                onPhoneNumberChange = {}
            )
        }
    }
}

@Preview(name = "Filled Phone Field", showBackground = true)
@Composable
private fun PhoneNumberFieldFilledPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            PhoneNumberField(
                phoneNumber = "770 123 4567",
                onPhoneNumberChange = {}
            )
        }
    }
}

@Preview(name = "Partial Phone Field", showBackground = true)
@Composable
private fun PhoneNumberFieldPartialPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            PhoneNumberField(
                phoneNumber = "770 1",
                onPhoneNumberChange = {}
            )
        }
    }
}

