package com.mbds.bpst.parcoursnfc.fragments

import android.content.Context
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Parcelable

interface ActionNFC {
    fun onNFC(tag: Tag, ndef: Ndef, rawMsgs:Array<Parcelable>?, context: Context)
}