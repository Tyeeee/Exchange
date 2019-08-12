package com.hynet.heebit.components.utils

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.hynet.heebit.components.constant.Constant
import com.iflytek.cloud.*

class TTSUtil : SynthesizerListener {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BundleUtil()
        }

    }

    private var context: Context? = null
    private var speechSynthesizer: SpeechSynthesizer? = null

    fun initializeSpeechSynthesizer(context: Context) {
        this.context = context
        SpeechUtility.createUtility(context, SpeechConstant.APPID + Constant.TTS.APPID)
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(context, null)
        setSpeechSynthesizerParameter()
    }

    @Synchronized
    fun startPlaying(context: Context, content: String) {
        if (speechSynthesizer == null) {
            initializeSpeechSynthesizer(context)
        }
        speechSynthesizer?.startSpeaking(content, this)
    }

    @Synchronized
    fun stopPlaying() {
        speechSynthesizer?.stopSpeaking()
    }

    @Synchronized
    fun destroy() {
        speechSynthesizer?.stopSpeaking()
        speechSynthesizer?.destroy()
    }

    private fun setSpeechSynthesizerParameter() {
        speechSynthesizer?.setParameter(SpeechConstant.VOICE_NAME, Constant.TTS.TTS_ROLE)
        speechSynthesizer?.setParameter(SpeechConstant.SPEED, Constant.TTS.TTS_SPEED)//语速
        speechSynthesizer?.setParameter(SpeechConstant.VOLUME, Constant.TTS.TTS_VOLUME)//音量
        speechSynthesizer?.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
    }

    override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {
        LogUtil.instance.print("onBufferProgress")
    }

    override fun onSpeakBegin() {
        LogUtil.instance.print("onSpeakBegin")
    }

    override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {
        LogUtil.instance.print("onSpeakProgress")
    }

    override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
        LogUtil.instance.print("onEvent")
    }

    override fun onSpeakPaused() {
        LogUtil.instance.print("onSpeakPaused")
    }

    override fun onSpeakResumed() {
        LogUtil.instance.print("onSpeakResumed")
    }

    override fun onCompleted(speechError: SpeechError?) {
        LogUtil.instance.print("onCompleted")
        LogUtil.instance.print(speechError?.getErrorCode())
        LogUtil.instance.print(speechError?.getErrorDescription())
        LogUtil.instance.print(speechError?.getLocalizedMessage())
        LogUtil.instance.print(speechError?.toString())
        LogUtil.instance.print(speechError?.getErrorCode())
        ToastUtil.instance.showToast(context!!, speechError?.getErrorDescription()!!, Toast.LENGTH_SHORT)
    }


}