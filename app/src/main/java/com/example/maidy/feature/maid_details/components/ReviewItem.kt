package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.ui.theme.*

@Composable
fun ReviewItem(
    reviewerName: String,
    reviewerImageUrl: String,
    date: String,
    rating: Float,
    comment: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaidProfileServiceBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reviewerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaidProfileReviewerName
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = date,
                    fontSize = 13.sp,
                    color = MaidProfileReviewDate
                )
            }

            // Star Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating.toInt()) MaidProfileReviewStar else MaidProfileReviewDate,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = comment,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaidProfileReviewText
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewItemPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .background(MaidProfileContentBackground)
                .padding(16.dp)
        ) {
            ReviewItem(
                reviewerName = "Mark Johnson",
                reviewerImageUrl = "",
                date = "June 15, 2024",
                rating = 5f,
                comment = "Elena was fantastic! Our house has never looked this clean. She was professional, punctual, and incredibly thorough. Highly recommend!"
            )
        }
    }
}



