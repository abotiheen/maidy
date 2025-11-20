package com.example.maidy.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Profile info field component for displaying user information
 * Currently read-only, can be made editable in the future
 */
@Composable
fun ProfileInfoField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Label
        Text(
            text = label,
            fontSize = 14.sp,
            color = ProfileFieldLabel,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Value Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ProfileFieldBackground,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                color = ProfileFieldValue,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileInfoFieldPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileScreenBackground)
            .padding(vertical = 16.dp)
    ) {
        ProfileInfoField(
            label = "Full Name",
            value = "Amelia Kristensen"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileInfoField(
            label = "Phone Number",
            value = "+1 123-456-7890"
        )
    }
}



