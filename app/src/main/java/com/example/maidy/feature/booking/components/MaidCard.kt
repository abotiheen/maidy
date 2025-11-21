package com.example.maidy.feature.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.feature.booking.MaidInfo
import com.example.maidy.ui.theme.*

@Composable
fun MaidCard(
    maidInfo: MaidInfo,
    statusMessage: String,
    currentStatus: BookingStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BookingStatusMaidCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BookingStatusMapPlaceholder)
                        .border(
                            width = 2.dp,
                            color = BookingStatusMaidCardBorder,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (maidInfo.profileImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = maidInfo.profileImageUrl,
                            contentDescription = "Maid Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Maid Profile",
                            tint = BookingStatusServiceLabel,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Maid Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = maidInfo.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BookingStatusMaidName
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = BookingStatusStarRating,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", maidInfo.rating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = BookingStatusMaidName
                        )
                    }
                }
                
                // Arriving in (only show for ON_THE_WAY status)
                if (currentStatus == BookingStatus.ON_THE_WAY) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Arriving in",
                            fontSize = 12.sp,
                            color = BookingStatusMaidInfo
                        )
                        Text(
                            text = "${maidInfo.arrivingInMinutes} mins",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BookingStatusOnWayIcon
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            Text(
                text = maidInfo.description,
                fontSize = 14.sp,
                color = BookingStatusMaidInfo,
                lineHeight = 20.sp,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status Message
            Text(
                text = statusMessage,
                fontSize = 14.sp,
                color = BookingStatusMaidInfo,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaidCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BookingStatusBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("On the Way Status", fontWeight = FontWeight.Bold)
        MaidCard(
            maidInfo = MaidInfo(
                name = "Jane Doe",
                rating = 4.9f,
                arrivingInMinutes = 15
            ),
            statusMessage = "Your maid is on the way.",
            currentStatus = BookingStatus.ON_THE_WAY
        )
        
        Text("In Progress Status", fontWeight = FontWeight.Bold)
        MaidCard(
            maidInfo = MaidInfo(
                name = "Jane Doe",
                rating = 4.9f,
                arrivingInMinutes = 15
            ),
            statusMessage = "Service in progress.",
            currentStatus = BookingStatus.IN_PROGRESS
        )
        
        Text("Completed Status", fontWeight = FontWeight.Bold)
        MaidCard(
            maidInfo = MaidInfo(
                name = "Jane Doe",
                rating = 4.9f,
                arrivingInMinutes = 15
            ),
            statusMessage = "Service completed successfully.",
            currentStatus = BookingStatus.COMPLETED
        )
    }
}
