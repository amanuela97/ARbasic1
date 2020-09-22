package fi.metropolia.kari.arbasic1

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fragment: ArFragment
    private var sampleRenderable: Renderable? = null
    private var bottleRenderable: ModelRenderable? = null
    private var pikachuRenderable: ModelRenderable? = null
    private var charmanderRenderable: ModelRenderable? = null
    private var txtViewRenderable: ViewRenderable? = null
    private var arrayV = ArrayList<View>()
    private val GLTF_ASSET = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/WaterBottle/glTF/WaterBottle.gltf"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        val bottle = bottle_iv
        val pikachu = pikachu_iv
        val charmander = charmander_iv
        val hello = hello_tv
        arrayV = arrayListOf(bottle, pikachu, charmander, hello)
        arrayV.forEach { it.setOnClickListener(this)}
        setUpModel()


        fragment.setOnTapArPlaneListener { hitResult: HitResult?, plane: Plane?, motionEvent: MotionEvent? ->
                    if (sampleRenderable == null) {
                        return@setOnTapArPlaneListener
                        }
                    val anchor = hitResult!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(fragment.arSceneView.scene)
                    val viewNode = TransformableNode(fragment.transformationSystem)
                    viewNode.setParent(anchorNode)
                    viewNode.renderable = sampleRenderable
                    viewNode.select()
                    }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpModel() {
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
        renderableFuture.thenAccept { bottleRenderable = it }
        renderableFuture.exceptionally { throwable -> Toast.makeText(
            applicationContext,
            "${throwable.printStackTrace()}",
            Toast.LENGTH_SHORT
        ).show()
            null
        } //let user know what went wrong

        val modelPikachu = Uri.parse("model.gltf")
        val renderableFuture2 = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this,
                    modelPikachu,
                    RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.2f) // Scale the original model to 20%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("model.gltf")
            .build()
        renderableFuture2.thenAccept { pikachuRenderable = it }
        renderableFuture2.exceptionally { throwable -> Toast.makeText(
            applicationContext,
            "${throwable.printStackTrace()}",
            Toast.LENGTH_SHORT
        ).show()
            null
        }

        val modelCharmander = Uri.parse("model2.gltf")
        val renderableFuture3 = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this,
                    modelCharmander,
                    RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.2f) // Scale the original model to 20%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("model2.gltf")
            .build()
        renderableFuture3.thenAccept { charmanderRenderable = it }
        renderableFuture3.exceptionally { throwable -> Toast.makeText(
            applicationContext,
            "${throwable.printStackTrace()}",
            Toast.LENGTH_SHORT
        ).show()
            null
        }

        ViewRenderable.builder()
            .setView(this, R.layout.hello)
            .build().thenAccept { txtViewRenderable = it }


    }


       private fun addObject() {
            val frame = fragment.arSceneView.arFrame
            val pt = getScreenCenter()
            val hits: List<HitResult>
            if (frame != null && sampleRenderable != null) {
                hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
                for (hit in hits) {
                    val trackable = hit.trackable
                    if (trackable is Plane) {
                        val anchor = hit!!.createAnchor()
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)
                        val mNode = TransformableNode(fragment.transformationSystem)
                        mNode.setParent(anchorNode)
                        mNode.renderable = sampleRenderable
                        mNode.select()
                        break
                    } } } }

        private fun getScreenCenter(): android.graphics.Point {
            val vw = findViewById<View>(android.R.id.content).rootView
            return android.graphics.Point(vw.width / 2, vw.height / 2)
        }


        override fun onClick(p0: View?) {
            when(p0?.id){
                bottle_iv.id -> sampleRenderable = bottleRenderable
                pikachu_iv.id -> sampleRenderable = pikachuRenderable
                charmander_iv.id  -> sampleRenderable = charmanderRenderable
                hello_tv.id -> sampleRenderable = txtViewRenderable
            }
            addObject()
            Log.i("DBG", "${p0}")
        }

    }
