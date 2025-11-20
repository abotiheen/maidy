package com.example.maidy.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.ui.theme.HomeBookNowButton
import com.example.maidy.ui.theme.HomeScheduleButton
import com.example.maidy.ui.theme.HomeScheduleButtonText
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun ActionButtons(
    onBookNowClick: () -> Unit,
    onScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Book Now Button
        ActionButton(
            text = "Book Now",
            backgroundColor = HomeBookNowButton,
            textColor = Color.White,
            iconRes = R.drawable.book_maid, // TODO: Replace with lightning/bolt icon
            onClick = onBookNowClick,
            modifier = Modifier.weight(1f)
        )

        // Schedule Button
        ActionButton(
            text = "Schedule",
            backgroundColor = HomeScheduleButton,
            textColor = HomeScheduleButtonText,
            iconRes = R.drawable.calendar, // TODO: Replace with calendar icon
            onClick = onScheduleClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = backgroundColor.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionButtonsPreview() {
    MaidyTheme {
        ActionButtons(
            onBookNowClick = {},
            onScheduleClick = {}
        )
    }
}



