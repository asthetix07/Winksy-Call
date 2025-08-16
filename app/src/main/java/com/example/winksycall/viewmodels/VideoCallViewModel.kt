package com.example.winksycall.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.webrtc.*


class VideoCallViewModel(
    private val app: Application,
    private val isInitiator: Boolean,
    private val roomId: String
) : AndroidViewModel(app) {

    private val eglBase = EglBase.create()
    private val firebase = FirebaseDatabase.getInstance()
    private val localUid = FirebaseAuth.getInstance().uid ?: ""
    private lateinit var peerConnection: PeerConnection
    private lateinit var localVideoSource: VideoSource
    private lateinit var localAudioSource: AudioSource
    private lateinit var localVideoTrack: VideoTrack
    lateinit var localAudioTrack: AudioTrack
    private lateinit var localSurfaceTextureHelper: SurfaceTextureHelper
    lateinit var localVideoCapturer: VideoCapturer

    private val peerConnectionFactory: PeerConnectionFactory

    @SuppressLint("StaticFieldLeak")
    val localVideoView: SurfaceViewRenderer = SurfaceViewRenderer(app)
    @SuppressLint("StaticFieldLeak")
    val remoteVideoView: SurfaceViewRenderer = SurfaceViewRenderer(app)

    init {
        // Init WebRTC
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(app)
                .createInitializationOptions()
        )

        val options = PeerConnectionFactory.Options()
        val encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext, true, true
        )
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()

        setupVideoViews()
        startLocalMedia()
        createPeerConnection()
        setupSignaling()

        if (isInitiator) {
            createOffer()
        }
    }

    private fun setupVideoViews() {
        localVideoView.init(eglBase.eglBaseContext, null)
        localVideoView.setMirror(true)
        remoteVideoView.init(eglBase.eglBaseContext, null)
        remoteVideoView.setMirror(false)
    }

    private fun startLocalMedia() {
        // Create and initialize video capturer
        localVideoCapturer = createCameraCapturer()
        localSurfaceTextureHelper = SurfaceTextureHelper.create(
            "CaptureThread",
            eglBase.eglBaseContext
        )

        localVideoSource = peerConnectionFactory.createVideoSource(false)
        localVideoCapturer.initialize(
            localSurfaceTextureHelper,
            app,
            localVideoSource.capturerObserver
        )
        localVideoCapturer.startCapture(720, 1280, 30)

        // Create local video track and bind to view
        localVideoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", localVideoSource)
        localVideoTrack.addSink(localVideoView)

        // Create audio constraints for better quality
        val audioConstraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        }

        // Create audio source & track
        localAudioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", localAudioSource)

        // Enable both tracks initially
        localVideoTrack.setEnabled(true)
        localAudioTrack.setEnabled(true)
    }

    private fun createPeerConnection() {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )

        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, object : PeerConnection.Observer {
            override fun onIceCandidate(candidate: IceCandidate) {
                sendIceCandidate(candidate)
            }

            override fun onAddStream(stream: MediaStream) {
                if (stream.audioTracks.isNotEmpty()) {
                    val remoteAudioTrack = stream.audioTracks[0]
                    remoteAudioTrack.setEnabled(true)
                }

                val remoteTrack = stream.videoTracks.firstOrNull()
                remoteTrack?.addSink(remoteVideoView)
            }

            override fun onTrack(transceiver: RtpTransceiver) {
                transceiver.receiver.track()?.let { track ->
                    if (track is VideoTrack) {
                        track.addSink(remoteVideoView)
                    }
                }
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                Log.d("WebRTC", "ICE state: $state")
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState) {
                Log.d("WebRTC", "Peer connection state: $newState")
            }

            // Unused callbacks
            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
            override fun onIceConnectionReceivingChange(p0: Boolean) {}
            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
            override fun onRemoveStream(p0: MediaStream?) {}
            override fun onDataChannel(p0: DataChannel?) {}

            override fun onRenegotiationNeeded() {}
            override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
        })!!

        val stream = peerConnectionFactory.createLocalMediaStream("ARDAMS")
        stream.addTrack(localVideoTrack)
        stream.addTrack(localAudioTrack)
        peerConnection.addStream(stream)
    }

    private fun createOffer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }

        peerConnection.createOffer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(p0: SessionDescription?) {
                peerConnection.setLocalDescription(SdpObserverAdapter(), p0)
                sendSessionDescription("offer", p0)
            }
        }, constraints)
    }

    private fun createAnswer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection.createAnswer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(p0: SessionDescription?) {
                peerConnection.setLocalDescription(SdpObserverAdapter(), p0)
                sendSessionDescription("answer", p0)
            }
        }, constraints)
    }

    private fun sendSessionDescription(type: String, sdp: SessionDescription?) {
        val map = mapOf(
            "type" to type,
            "sdp" to sdp?.description
        )
        firebase.getReference("calls/$roomId/sdp").setValue(map)
    }

    private fun sendIceCandidate(candidate: IceCandidate) {
        val map = mapOf(
            "sdpMid" to candidate.sdpMid,
            "sdpMLineIndex" to candidate.sdpMLineIndex,
            "candidate" to candidate.sdp
        )
        firebase.getReference("calls/$roomId/candidates/$localUid").push().setValue(map)
    }

    private fun setupSignaling() {
        // SDP
        firebase.getReference("calls/$roomId/sdp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.value as? Map<*, *> ?: return
                    val type = data["type"] as? String ?: return
                    val sdp = data["sdp"] as? String ?: return
                    val sessionDescription = SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(type), sdp
                    )

                    peerConnection.setRemoteDescription(SdpObserverAdapter(), sessionDescription)
                    if (type == "offer" && !isInitiator) { createAnswer()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        // ICE
        firebase.getReference("calls/$roomId/candidates")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.children.forEach {
                        val candidateData = it.value as? Map<*, *> ?: return@forEach
                        val sdpMid = candidateData["sdpMid"] as? String ?: return@forEach
                        val sdpMLineIndex = (candidateData["sdpMLineIndex"] as? Long)?.toInt() ?: return@forEach
                        val candidate = candidateData["candidate"] as? String ?: return@forEach

                        val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, candidate)
                        peerConnection.addIceCandidate(iceCandidate)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
    }

    private fun createCameraCapturer(): VideoCapturer {
        val enumerator = Camera2Enumerator(app)
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        throw IllegalStateException("No front-facing camera found")
    }

    open class SdpObserverAdapter : SdpObserver {
        override fun onCreateSuccess(p0: SessionDescription?) {}
        override fun onSetSuccess() {}
        override fun onCreateFailure(p0: String?) {}
        override fun onSetFailure(p0: String?) {}
    }

    fun endCall() {
        try {
            // 1. Stop local tracks
            localVideoTrack.setEnabled(false)
            localAudioTrack.setEnabled(false)
            localVideoTrack.dispose()
            localAudioTrack.dispose()

            // 2. Stop video capturer
            try {
                localVideoCapturer.stopCapture()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            localVideoCapturer.dispose()

            // 3. Release sources
            localVideoSource.dispose()
            localAudioSource.dispose()

            // 4. Release video renderers (must be before SurfaceTextureHelper)
            localVideoView.release()
            remoteVideoView.release()

            // 5. Release SurfaceTextureHelper (post on its handler to avoid dead thread issue)
            localSurfaceTextureHelper.handler.post {
                localSurfaceTextureHelper.dispose()
            }

            // 6. Close PeerConnection
            peerConnection.close()
            peerConnection.dispose()

            // 7. Release EGL
            eglBase.release()

            // 8. Remove signaling data for this room
            val db = FirebaseDatabase.getInstance().reference
            db.child("calls").child(roomId).removeValue()
            db.child("offers").child(localUid).removeValue()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        endCall()
    }

}
