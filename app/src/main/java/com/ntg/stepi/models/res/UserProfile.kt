package com.ntg.stepi.models.res

data class UserProfile(
    val totalDays: Int? = null,
    val steps: Int? = null,
    val state: String? = null,
    val claps: Int? = null,
    val fullName: String? = null,
    val gradeId: Int? = null,
    val isVerified: Boolean? = false,
    val isBlocked: Boolean? = false,
    val isLock: Boolean? = false,
    val fosId: Int? = null,
    val fosName: String? = null,
    val timeSign: String? = null,
    val totalClaps: Int? = 0,
    val isClap: Boolean? = false,
    val socials: List<SocialRes>? = null,
    val stepsList: List<StepRes>? = null,
    val achievement: Achievement? = null,
    val messages: String? = null,


)
