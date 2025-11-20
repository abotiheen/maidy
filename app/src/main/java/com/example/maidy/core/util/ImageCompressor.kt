package com.example.maidy.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

/**
 * Utility class for compressing images before upload
 * Reduces file size while maintaining good quality
 */
class ImageCompressor(private val context: Context) {
    
    companion object {
        private const val MAX_WIDTH = 1024f
        private const val MAX_HEIGHT = 1024f
        private const val QUALITY = 80 // JPEG quality (0-100)
    }
    
    /**
     * Compress an image from URI
     * @param uri Source image URI
     * @return URI of compressed image, or null if compression fails
     */
    suspend fun compressImage(uri: Uri): Uri? {
        return try {
            println("🗜️ ImageCompressor: Starting compression for: $uri")
            
            // Open input stream
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open image")
            
            // Decode original dimensions without loading full bitmap
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
            val originalWidth = options.outWidth
            val originalHeight = options.outHeight
            println("🗜️ ImageCompressor: Original size: ${originalWidth}x${originalHeight}")
            
            // Calculate sample size
            val sampleSize = calculateSampleSize(originalWidth, originalHeight)
            println("🗜️ ImageCompressor: Sample size: $sampleSize")
            
            // Decode with sample size
            val inputStream2 = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open image")
            
            val decodedOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            var bitmap = BitmapFactory.decodeStream(inputStream2, null, decodedOptions)
                ?: throw Exception("Cannot decode image")
            inputStream2.close()
            
            println("🗜️ ImageCompressor: Decoded size: ${bitmap.width}x${bitmap.height}")
            
            // Handle rotation (EXIF orientation)
            bitmap = rotateImageIfRequired(bitmap, uri)
            
            // Scale to target size if still too large
            bitmap = scaleBitmap(bitmap, MAX_WIDTH, MAX_HEIGHT)
            println("🗜️ ImageCompressor: Final size: ${bitmap.width}x${bitmap.height}")
            
            // Save compressed image to temp file
            val compressedFile = createTempFile(context)
            FileOutputStream(compressedFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
                out.flush()
            }
            
            // Clean up bitmap
            bitmap.recycle()
            
            val originalSize = getFileSize(uri)
            val compressedSize = compressedFile.length()
            val compressionRatio = ((originalSize - compressedSize) * 100 / originalSize)
            
            println("✅ ImageCompressor: Compression complete!")
            println("📊 Original: ${formatFileSize(originalSize)}")
            println("📊 Compressed: ${formatFileSize(compressedSize)}")
            println("📊 Saved: $compressionRatio%")
            
            Uri.fromFile(compressedFile)
            
        } catch (e: Exception) {
            println("❌ ImageCompressor: Compression failed - ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Calculate sample size for initial decode
     */
    private fun calculateSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        
        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            
            while ((halfWidth / sampleSize) >= MAX_WIDTH &&
                   (halfHeight / sampleSize) >= MAX_HEIGHT) {
                sampleSize *= 2
            }
        }
        
        return sampleSize
    }
    
    /**
     * Scale bitmap to target dimensions while maintaining aspect ratio
     */
    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Float, maxHeight: Float): Bitmap {
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        
        // If already smaller than target, return as is
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }
        
        // Calculate scale factor
        val scale = min(maxWidth / width, maxHeight / height)
        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()
        
        println("🗜️ ImageCompressor: Scaling from ${width.toInt()}x${height.toInt()} to ${scaledWidth}x${scaledHeight}")
        
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
        
        // Recycle original if it's different from scaled
        if (scaledBitmap != bitmap) {
            bitmap.recycle()
        }
        
        return scaledBitmap
    }
    
    /**
     * Rotate image based on EXIF orientation
     */
    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            inputStream.close()
            
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val rotation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> return bitmap
            }
            
            println("🗜️ ImageCompressor: Rotating image ${rotation}°")
            
            val matrix = Matrix()
            matrix.postRotate(rotation)
            
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            
            bitmap.recycle()
            return rotatedBitmap
            
        } catch (e: Exception) {
            println("⚠️ ImageCompressor: Could not read EXIF orientation - ${e.message}")
            return bitmap
        }
    }
    
    /**
     * Create a temporary file for compressed image
     */
    private fun createTempFile(context: Context): File {
        val timestamp = System.currentTimeMillis()
        return File(context.cacheDir, "compressed_${timestamp}.jpg")
    }
    
    /**
     * Get file size from URI
     */
    private fun getFileSize(uri: Uri): Long {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.available().toLong()
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Format file size for display
     */
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}


