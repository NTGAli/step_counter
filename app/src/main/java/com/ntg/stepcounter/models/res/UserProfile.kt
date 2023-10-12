package com.ntg.stepcounter.models.res

data class UserProfile(
    val totalDays: Int? = null,
    val steps: Int? = null,
    val fullName: String? = null,
    val gradeId: Int? = null,
    val isVerified: Boolean? = false,
    val isLock: Boolean? = false,
    val fosName: String? = null,
    val totalClaps: Int? = 0,
    val socials: List<SocialRes>? = null,


)
