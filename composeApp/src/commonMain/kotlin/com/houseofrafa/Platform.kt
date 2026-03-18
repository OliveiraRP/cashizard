package com.houseofrafa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform