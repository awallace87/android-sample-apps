package work.wander.videoclip.framework.camerax.legacy

import android.hardware.camera2.CameraCharacteristics

data class CameraDeviceId(
    val camera2DeviceIds: List<String>,
    val camera2DeviceCharacteristics: List<CameraCharacteristics>,
) {
}


