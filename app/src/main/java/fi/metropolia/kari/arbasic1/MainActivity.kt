package fi.metropolia.kari.arbasic1

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private var testRenderable: ModelRenderable? = null
    private val GLTF_ASSET = "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { addObject() }

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        /*val renderableFuture = ViewRenderable.builder()
            .setView(this, R.layout.rendtext)
            .build()
        renderableFuture.thenAccept {it -> testRenderable = it }*/

        val modelUri = Uri.parse(GLTF_ASSET)
        val renderableFuture = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this,
                    modelUri,
                    RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.2f) // Scale the original model to 20%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId(GLTF_ASSET)
            .build()
        renderableFuture.thenAccept { testRenderable = it }
        renderableFuture.exceptionally { throwable -> Toast.makeText(
            applicationContext,
            "${throwable.printStackTrace()}",
            Toast.LENGTH_SHORT
        ).show()
            null
         } //let user know what went wrong


        fragment.setOnTapArPlaneListener { hitResult: HitResult?, plane: Plane?, motionEvent: MotionEvent? ->
                    if (testRenderable == null) {
                        return@setOnTapArPlaneListener
                        }
                    val anchor = hitResult!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(fragment.arSceneView.scene)
                    val viewNode = TransformableNode(fragment.transformationSystem)
                    viewNode.setParent(anchorNode)
                    viewNode.renderable = testRenderable
                    viewNode.select()
                    viewNode.setOnTapListener{ hitTestRes: HitTestResult?, motionEv: MotionEvent? ->
                                button.visibility = View.INVISIBLE
                            }

                    }
        }


        private fun addObject() {
            val frame = fragment.arSceneView.arFrame
            val pt = getScreenCenter()
            val hits: List<HitResult>
            if (frame != null && testRenderable != null) {
                hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
                for (hit in hits) {
                    val trackable = hit.trackable
                    if (trackable is Plane) {
                        val anchor = hit!!.createAnchor()
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)
                        val mNode = TransformableNode(fragment.transformationSystem)
                        mNode.setParent(anchorNode)
                        mNode.renderable = testRenderable
                        mNode.select()
                        break
                    } } } }

         private fun getScreenCenter(): android.graphics.Point {
            val vw = findViewById<View>(android.R.id.content).rootView
            return android.graphics.Point(vw.width / 2, vw.height / 2)
        }

    }
