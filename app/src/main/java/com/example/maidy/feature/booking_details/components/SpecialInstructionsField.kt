package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Component for entering special instructions or notes for the booking
 * Multi-line text field with placeholder
 */
@Composable
fun SpecialInstructionsField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Title
        Text(
            text = "Special Instructions or Notes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BookingDetailsSectionTitle
        )
        
        // Text Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    color = BookingDetailsNotesBackground,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = BookingDetailsNotesBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = BookingDetailsNotesText
                ),
                cursorBrush = SolidColor(BookingDetailsDateIcon),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = "e.g. key is under the mat, please focus on the kitchen area...",
                            fontSize = 14.sp,
                            color = BookingDetailsNotesHint
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Preview(name = "Empty Instructions Field", showBackground = true)
@Composable
private fun SpecialInstructionsFieldEmptyPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            SpecialInstructionsField(
                value = "",
                onValueChange = {}
            )
        }
    }
}

@Preview(name = "Filled Instructions Field", showBackground = true)
@Composable
private fun SpecialInstructionsFieldFilledPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            SpecialInstructionsField(
                value = "Please focus on the kitchen and bathroom. The key is under the mat.",
                onValueChange = {}
            )
        }
    }
}

