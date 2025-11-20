package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
fun ProfileHeader(
    name: String,
    isVerified: Boolean,
    rating: Double,
    reviewCount: Int,
    profileImageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaidProfileServiceBackground)
        ) {
            if (profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile picture of $name",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaidProfileServiceBackground)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name and Verification Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaidProfileNameText
            )

            if (isVerified) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified badge",
                    tint = MaidProfileVerificationBadge,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaidProfileStarIcon,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = String.format("%.1f", rating),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaidProfileRatingText
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "($reviewCount reviews)",
                fontSize = 16.sp,
                color = MaidProfileReviewCount
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaidProfileBackground)
                .padding(24.dp)
        ) {
            ProfileHeader(
                name = "Elena Rodriguez",
                isVerified = true,
                rating = 4.9,
                reviewCount = 125,
                profileImageUrl = ""
            )
        }
    }
}

