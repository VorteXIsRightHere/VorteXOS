package com.tubitak.themeengine.modes

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors
import kotlin.math.absoluteValue

/**
 * Kameradan yüz hatlarını okuyarak duygu tespiti yapar.
 *
 * ML Kit Face Detection kullanır ve şu olasılıkları analiz eder:
 *  - smilingProbability
 *  - leftEyeOpenProbability
 *  - rightEyeOpenProbability
 *  - headEulerAngleY / X / Z
 *
 * Şu an için basit heuristic kullanılmaktadır. Daha yüksek doğruluk için
 * ileride TensorFlow Lite tabanlı FER (Facial Expression Recognition) modeli
 * entegre edilebilir.
 */
class FaceEmotionDetector(private val context: Context) {

    interface EmotionCallback {
        fun onEmotionDetected(emotion: EmotionTheme.Emotion)
        fun onError(error: String)
    }

    private val executor = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var lastDetectionTime = 0L
    private val emotionSettings = EmotionSettings(context)

    private val detector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .build()
        FaceDetection.getClient(options)
    }

    fun startDetection(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        callback: EmotionCallback
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(lifecycleOwner, previewView, callback)
            } catch (e: Exception) {
                callback.onError("Kamera başlatılamadı: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        callback: EmotionCallback
    ) {
        val provider = cameraProvider ?: return

        val preview = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executor) { imageProxy ->
                    processImage(imageProxy, callback)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            callback.onError("Kamera kullanım hatası: ${e.message}")
        }
    }

    private fun processImage(imageProxy: ImageProxy, callback: EmotionCallback) {
        if (!emotionSettings.isEnabled) {
            imageProxy.close()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastDetectionTime < emotionSettings.detectionIntervalMs) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val emotion = detectEmotion(faces[0])
                    lastDetectionTime = now
                    callback.onEmotionDetected(emotion)
                }
            }
            .addOnFailureListener { e ->
                callback.onError("Yüz analizi hatası: ${e.message}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    /**
     * ML Kit yüz olasılıklarına göre duygu tahmini yapar.
     * Hassasiyet ayarı ile eşikler değiştirilir.
     */
    private fun detectEmotion(face: Face): EmotionTheme.Emotion {
        val smile = face.smilingProbability ?: 0f
        val leftEye = face.leftEyeOpenProbability ?: 1f
        val rightEye = face.rightEyeOpenProbability ?: 1f
        val sensitivity = emotionSettings.sensitivity

        // Eşikler hassasiyete göre ayarlanır.
        // Yüksek hassasiyet = daha düşük eşik, daha kolay duygu değişimi.
        val smileThreshold = 0.75f - (sensitivity * 0.25f)
        val eyeOpenThreshold = 0.4f - (sensitivity * 0.15f)
        val angerHeadAngle = 20f - (sensitivity * 10f)

        return when {
            smile > smileThreshold -> EmotionTheme.Emotion.HAPPY
            face.headEulerAngleY.absoluteValue > angerHeadAngle ||
                face.headEulerAngleX.absoluteValue > angerHeadAngle -> EmotionTheme.Emotion.ANGRY
            leftEye < eyeOpenThreshold && rightEye < eyeOpenThreshold -> EmotionTheme.Emotion.SAD
            else -> EmotionTheme.Emotion.CALM
        }
    }

    fun stopDetection() {
        try {
            cameraProvider?.unbindAll()
        } catch (e: Exception) {
            // ignore
        }
        executor.shutdown()
    }
}
