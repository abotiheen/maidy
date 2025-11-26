package com.example.maidy.feature.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.core.model.Maid
import com.example.maidy.ui.theme.*

@Composable
fun SearchMaidCard(
    maid: Maid,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BookingStatusMaidCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(BookingStatusMapPlaceholder)
                    .border(
                        width = 2.dp,
                        color = BookingStatusMaidCardBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (maid.profileImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = maid.profileImageUrl,
                        contentDescription = "Maid Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Maid Profile",
                        tint = BookingStatusServiceLabel,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Maid Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name and Verification
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = maid.fullName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BookingStatusMaidName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (maid.verified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = MaidProfileVerificationBadge,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Specialty Tag
                if (maid.specialtyTag.isNotBlank()) {
                    Text(
                        text = maid.specialtyTag,
                        fontSize = 13.sp,
                        color = BookingStatusMaidInfo,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Rating and Reviews
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = BookingStatusStarRating,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", maid.averageRating),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = BookingStatusMaidName
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${maid.reviewCount})",
                        fontSize = 14.sp,
                        color = BookingStatusMaidInfo
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${String.format("%.0f", maid.hourlyRate)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaidyBlue
                )
                Text(
                    text = "per hour",
                    fontSize = 12.sp,
                    color = BookingStatusMaidInfo
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchMaidCardPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaidyBackgroundWhite)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchMaidCard(
                maid = Maid(
                    id = "1",
                    fullName = "Elena Rodriguez",
                    profileImageUrl = "",
                    verified = true,
                    averageRating = 4.9,
                    reviewCount = 125,
                    specialtyTag = "Gold",
                    hourlyRate = 25.0,
                    available = true
                ),
                onClick = {}
            )

            SearchMaidCard(
                maid = Maid(
                    id = "2",
                    fullName = "Maria Garcia",
                    profileImageUrl = "",
                    verified = false,
                    averageRating = 4.5,
                    reviewCount = 67,
                    specialtyTag = "Silver",
                    hourlyRate = 22.0,
                    available = true
                ),
                onClick = {}
            )

            SearchMaidCard(
                maid = Maid(
                    id = "3",
                    fullName = "Sarah Ahmed",
                    profileImageUrl = "",
                    verified = false,
                    averageRating = 4.2,
                    reviewCount = 45,
                    specialtyTag = "Bronze",
                    hourlyRate = 18.0,
                    available = true
                ),
                onClick = {}
            )
        }
    }
}
