// This will create an encrypted value that can only be decrypted on the instance it was created on
for (int i = 0; i < 10; i++) {
  println hudson.util.Secret.fromString(java.util.UUID.randomUUID().toString()).getEncryptedValue()
}
