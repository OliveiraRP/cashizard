package com.houseofrafa.cashizard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform