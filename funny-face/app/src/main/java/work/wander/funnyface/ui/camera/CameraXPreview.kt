package work.wander.funnyface.ui.camera

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraXPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier
    )
}