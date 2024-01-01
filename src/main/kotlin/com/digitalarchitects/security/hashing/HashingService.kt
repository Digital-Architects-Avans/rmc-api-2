package com.digitalarchitects.security.hashing

interface HashingService {
    fun generateSaltedHash(value: String, saltLength: Int = 32): SaltedHash
    fun verifySaltedHash(value: String, saltedHash: SaltedHash): Boolean
}