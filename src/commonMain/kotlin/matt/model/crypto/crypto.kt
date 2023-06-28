package matt.model.crypto


interface EncryptionCipher<P : Any, C : Any> {
    fun encrypt(plaintext: P): C
}

interface DecryptionCipher<P : Any, C : Any> {
    fun decrypt(cipher: C): P
}

interface SymmetricCipher<P : Any, C : Any> : EncryptionCipher<P, C>, DecryptionCipher<P, C>