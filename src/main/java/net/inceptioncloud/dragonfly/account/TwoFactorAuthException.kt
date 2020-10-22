package net.inceptioncloud.dragonfly.account

/**
 * Thrown when a login attempt fails due to a 2fa code missing
 */
class TwoFactorAuthException : Exception("This account requires a two factor auth code for login")