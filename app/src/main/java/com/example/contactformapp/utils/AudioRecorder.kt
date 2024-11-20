package com.example.contactformapp.utils

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioRecorder(private val filePath: String) {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val bufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    companion object {
        private const val SAMPLE_RATE = 44100 // Standard sample rate for high-quality audio
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        Thread { writeAudioDataToFile() }.start()
    }

    fun stopRecording(): String {
        isRecording = false
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null

        convertPcmToWav() // Convert the recorded PCM file to WAV
        return filePath
    }

    private fun writeAudioDataToFile() {
        val pcmFile = File(filePath.replace(".wav", ".pcm"))
        FileOutputStream(pcmFile).use { outputStream ->
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (bytesRead > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    private fun convertPcmToWav() {
        val pcmFile = File(filePath.replace(".wav", ".pcm"))
        val wavFile = File(filePath)

        FileOutputStream(wavFile).use { outputStream ->
            writeWavHeader(outputStream, pcmFile.length().toInt(), SAMPLE_RATE, 1, 16)
            pcmFile.inputStream().use { it.copyTo(outputStream) }
        }

        pcmFile.delete() // Clean up the raw PCM file after conversion
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        pcmDataLength: Int,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val totalDataLength = pcmDataLength + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8

        out.write("RIFF".toByteArray()) // ChunkID
        out.write(intToByteArray(totalDataLength)) // ChunkSize
        out.write("WAVE".toByteArray()) // Format
        out.write("fmt ".toByteArray()) // Sub chunk1ID
        out.write(intToByteArray(16)) // Sub chunk1Size
        out.write(shortToByteArray(1)) // AudioFormat (1 for PCM)
        out.write(shortToByteArray(channels)) // NumChannels
        out.write(intToByteArray(sampleRate)) // SampleRate
        out.write(intToByteArray(byteRate)) // ByteRate
        out.write(shortToByteArray(channels * bitsPerSample / 8)) // BlockAlign
        out.write(shortToByteArray(bitsPerSample)) // BitsPerSample
        out.write("data".toByteArray()) // Sub chunk2ID
        out.write(intToByteArray(pcmDataLength)) // Sub chunk2Size
    }

    private fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()
    }

    private fun shortToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value.toShort()).array()
    }
}
