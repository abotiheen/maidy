package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.R
import com.example.maidy.ui.theme.*

/**
 * Header component displaying maid information
 * Shows maid profile image, name, rating, reviews count, and price
 */
@Composable
fun MaidInfoHeader(
    maidName: String,
    rating: Double,
    reviewsCount: Int,
    pricePerHour: String,
    isVerified: Boolean,
    profileImageUrl: String = "",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BookingDetailsTopBarBackground, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image with Verification Badge
        Box {
            if (profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Maid profile picture",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(ProfileImageBackground),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder when no image
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(ProfileImageBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default profile picture",
                        tint = ProfileImagePlaceholderIcon,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
        
        // Maid Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Name
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = maidName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BookingDetailsMaidName
                )

                // Verification Badge
                if (isVerified) {
                    Icon(
                        painter = painterResource(R.drawable.verified),
                        contentDescription = "Verified badge",
                        tint = BookingDetailsVerificationBadge,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Rating and Reviews
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BookingDetailsStarIcon,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = rating.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BookingDetailsMaidRating
                )
                Text(
                    text = "($reviewsCount Reviews)",
                    fontSize = 14.sp,
                    color = BookingDetailsReviewCount
                )
            }
            
            // Price
            Text(
                text = pricePerHour,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BookingDetailsDateIcon
            )
        }
    }
}

@Preview(name = "Verified Maid", showBackground = true)
@Composable
private fun MaidInfoHeaderVerifiedPreview() {
    MaidyTheme {
        MaidInfoHeader(
            maidName = "Maria Garcia",
            rating = 4.8,
            reviewsCount = 120,
            pricePerHour = "$15/hour",
            isVerified = true,
            profileImageUrl = ""
        )
    }
}

@Preview(name = "Non-Verified Maid", showBackground = true)
@Composable
private fun MaidInfoHeaderNotVerifiedPreview() {
    MaidyTheme {
        MaidInfoHeader(
            maidName = "Sarah Johnson",
            rating = 4.5,
            reviewsCount = 85,
            pricePerHour = "$12/hour",
            isVerified = false,
            profileImageUrl = ""
        )
    }
}

@Preview(name = "With Profile Image URL", showBackground = true)
@Composable
private fun MaidInfoHeaderWithImagePreview() {
    MaidyTheme {
        MaidInfoHeader(
            maidName = "Emma Watson",
            rating = 4.9,
            reviewsCount = 200,
            pricePerHour = "$18/hour",
            isVerified = true,
            profileImageUrl = "https://example.com/profile.jpg"
        )
    }
}

