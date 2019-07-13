package com.example.kotlinmessenger.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//user object that's stored in firebase database under /users/
@Parcelize //extra library in build.gradle(Module: app)
class User(val uid : String, val username : String, val profileImageUrl : String): Parcelable {
    constructor() : this ("", "", "") //empty constructor in Kotlin
}//User class