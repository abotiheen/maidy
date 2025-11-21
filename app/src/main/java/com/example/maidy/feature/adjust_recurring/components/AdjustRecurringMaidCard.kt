package com.example.maidy.feature.adjust_recurring.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.ui.theme.*

@Composable
fun AdjustRecurringMaidCard(
    maidName: String,
    rating: Float,
    reviewsCount: Int,
    profileImageUrl: String,
    showRateButton: Boolean,
    onRateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AdjustRecurringCardBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Your Assigned Maid",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AdjustRecurringSectionTitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Maid Profile Image
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(2.dp, AdjustRecurringMaidBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Maid profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = maidName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdjustRecurringMaidName
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AdjustRecurringRatingStarColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$rating ($reviewsCount reviews)",
                            fontSize = 14.sp,
                            color = AdjustRecurringRatingText
                        )
                    }
                }
            }

            if (showRateButton) {
                TextButton(
                    onClick = onRateClick,
                    modifier = Modifier
                        .background(
                            color = AdjustRecurringRateButtonBg,
                            shape = CircleShape
                        )
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Rate",
                        fontSize = 14.sp,
                        color = AdjustRecurringRateButton,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(name = "With Rate Button", showBackground = true)
@Composable
fun AdjustRecurringMaidCardPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
                .padding(16.dp)
        ) {
            AdjustRecurringMaidCard(
                maidName = "Maria Rodriguez",
                rating = 4.9f,
                reviewsCount = 120,
                profileImageUrl = "",
                showRateButton = true,
                onRateClick = {}
            )
        }
    }
}

@Preview(name = "Without Rate Button", showBackground = true)
@Composable
fun AdjustRecurringMaidCardNoRatePreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
                .padding(16.dp)
        ) {
            AdjustRecurringMaidCard(
                maidName = "Sarah Johnson",
                rating = 4.7f,
                reviewsCount = 85,
                profileImageUrl = "",
                showRateButton = false,
                onRateClick = {}
            )
        }
    }
}
