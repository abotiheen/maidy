package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maid_details.CustomerReview
import com.example.maidy.ui.theme.*

@Composable
fun CustomerReviewsSection(
    reviews: List<CustomerReview>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Customer Reviews",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaidProfileSectionTitle
        )

        Spacer(modifier = Modifier.height(16.dp))

        reviews.forEach { review ->
            ReviewItem(
                reviewerName = review.reviewerName,
                reviewerImageUrl = review.reviewerImageUrl,
                date = review.date,
                rating = review.rating,
                comment = review.comment
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerReviewsSectionPreview() {
    MaidyTheme {
        Box(modifier = Modifier.background(MaidProfileContentBackground)) {
            CustomerReviewsSection(
                reviews = listOf(
                    CustomerReview(
                        id = "1",
                        reviewerName = "Mark Johnson",
                        reviewerImageUrl = "",
                        date = "June 15, 2024",
                        rating = 5f,
                        comment = "Elena was fantastic! Our house has never looked this clean. She was professional, punctual, and incredibly thorough. Highly recommend!"
                    ),
                    CustomerReview(
                        id = "2",
                        reviewerName = "Sarah Lee",
                        reviewerImageUrl = "",
                        date = "June 12, 2024",
                        rating = 5f,
                        comment = "Absolutely amazing service. Elena paid attention to every little detail. I'm so happy with the result and will definitely be booking her again."
                    )
                )
            )
        }
    }
}

