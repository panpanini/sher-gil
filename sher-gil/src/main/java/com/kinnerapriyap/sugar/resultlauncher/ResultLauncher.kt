package com.kinnerapriyap.sugar.resultlauncher

interface ResultLauncher {
    fun askPermission()
    fun openGallery(input: GetFromGalleryInput)
}