package com.example.walletapp.helper
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyChain
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.walletapp.R
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/*USAGE:
private const val PASSWORD_KEY = "password"
    private val passwordStorage: PasswordStorageHelper = PasswordStorageHelper(context)

    fun savePassword(password: String) {
        passwordStorage.setData(PASSWORD_KEY, password.toByteArray())
    }

    fun getPassword() : String {
        return String((passwordStorage.getData(PASSWORD_KEY) ?: ByteArray(0)))
    }

    fun removePassword() {
        passwordStorage.remove(PASSWORD_KEY)
    }*/
class PasswordStorageHelper(context: Context) {
    private val tag = "PasswordStorageHelper"

    private val PREFS_NAME = "SecureData"

    private var passwordStorage: PasswordStorageInterface?

    init {
        passwordStorage = PasswordStorageHelperSDK18();
        var isInitialized: Boolean? = false;

        try {
            isInitialized = passwordStorage?.init(context);
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message!!, Toast.LENGTH_LONG).show()
            Toast.makeText(context, "BAD NEWS!! Crypto SDK needed.", Toast.LENGTH_LONG).show()
          //  Log.e(tag, "PasswordStorage initialisation error:" + ex.message, ex);
        }

      /*  if (isInitialized != true && passwordStorage is PasswordStorageHelperSDK18) { // ну а шо ещё остаётся
            passwordStorage = PasswordStorageHelperSDK16();
            passwordStorage?.init(context);
        }*/
    }


    fun setData(key: String?, data: ByteArray?) {
        passwordStorage?.setData(key!!, data ?: ByteArray(0))
    }

    fun getData(key: String?): ByteArray? {
        return passwordStorage?.getData(key ?: "")
    }

    fun remove(key: String?) {
        passwordStorage?.remove(key ?: "")
    }

    private interface PasswordStorageInterface {
        fun init(context: Context?): Boolean
        fun setData(key: String?, data: ByteArray?)
        fun getData(key: String?): ByteArray?
        fun remove(key: String?)
    }

    // Шифрования в телефоне нет! Ну это старые телефоны:
  /*  private inner class PasswordStorageHelperSDK16 : PasswordStorageInterface {
        private var preferences: SharedPreferences? = null

        override fun init(context: Context?): Boolean {
            preferences = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return true
        }

        override fun setData(key: String?, data: ByteArray?) {
            if (data == null) return
            val editor = preferences?.edit()
            editor?.putString(key, Base64.encodeToString(data, Base64.DEFAULT))
            editor?.apply()
        }

        override fun getData(key: String?): ByteArray? {
            val res = preferences?.getString(key, null) ?: return null
            return Base64.decode(res, Base64.DEFAULT)
        }

        override fun remove(key: String?) {
            val editor = preferences?.edit()
            editor?.remove(key)
            editor?.apply()
        }
    }
*/
    // Шифрование есть в телефоне:
    private inner class PasswordStorageHelperSDK18 : PasswordStorageInterface {

        private val KEY_ALGORITHM_RSA: String = "RSA";

        private val KEYSTORE_PROVIDER_ANDROID_KEYSTORE: String = "AndroidKeyStore";
        private val RSA_ECB_PKCS1_PADDING: String = "RSA/ECB/PKCS1Padding";

        private var preferences: SharedPreferences? = null;
        private var alias: String? = null;

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun init(context: Context?): Boolean {
            preferences = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            alias = context?.getString(R.string.app_package);

            val ks: KeyStore?

            try {
                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                //Use null to load Keystore with default parameters.
                ks?.load(null);

                // Check if Private and Public already keys exists. If so we don't need to generate them again
                val privateKey: Key? = ks?.getKey(alias, null);
                if (privateKey != null && ks.getCertificate(alias) != null) {
                    val publicKey: PublicKey? = ks.getCertificate(alias).publicKey;
                    if (publicKey != null) {
                        // All keys are available.
                        return true;
                    }
                }
            } catch (ex: Exception) {
                return false;
            }

            // Create a start and end time, for the validity range of the key pair that's about to be
            // generated.
            val start = GregorianCalendar();
            val end = GregorianCalendar();
            end.add(Calendar.YEAR, 10);

            // Specify the parameters object which will be passed to KeyPairGenerator
            val spec: AlgorithmParameterSpec?
           /* if (Build.VERSION.SDK_INT < 23) {
                spec = context?.let {
                    android.security.KeyPairGeneratorSpec.Builder(it)
                        // Alias - is a key for your KeyPair, to obtain it from Keystore in future.
                        .setAlias(alias ?: "")
                        // The subject used for the self-signed certificate of the generated pair
                        .setSubject(X500Principal("CN=$alias"))
                        // The serial number used for the self-signed certificate of the generated pair.
                        .setSerialNumber(BigInteger.valueOf(1337))
                        // Date range of validity for the generated pair.
                        .setStartDate(start.time).setEndDate(end.time)
                        .build()};} else */
                spec = KeyGenParameterSpec.Builder(alias ?: "", KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build();


            // Initialize a KeyPair generator using the the intended algorithm (RSA
            // and the KeyStore. use AndroidKeyStore.
            val kpGenerator: KeyPairGenerator
            try {
                kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                kpGenerator.initialize(spec);
                // Generate private/public keys
                kpGenerator.generateKeyPair();
            } catch (e: Exception) {
                when (e) {
                    is NoSuchAlgorithmException, is InvalidAlgorithmParameterException, is NoSuchProviderException -> {
                        try {
                            ks?.deleteEntry(alias)
                        } catch (e1: Exception) {
                            // Just ignore any errors here
                        }
                    }
                }

            }

            // Check if device support Hardware-backed keystore
            try {
                var isHardwareBackedKeystoreSupported: Boolean? = null

                isHardwareBackedKeystoreSupported = if (Build.VERSION.SDK_INT < 23) {
                    KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA)
                } else {
                    val privateKey: Key = ks.getKey(alias, null)
                    //KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA)
                    val keyFactory: KeyFactory = KeyFactory.getInstance(privateKey.algorithm, "AndroidKeyStore")
                    val keyInfo: KeyInfo = keyFactory.getKeySpec(privateKey, KeyInfo::class.java)
                    keyInfo.isInsideSecureHardware
                }
                //Log.d(tag, "Hardware-Backed Keystore Supported: $isHardwareBackedKeystoreSupported");
            } catch (e: Exception) {
                //KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | InvalidKeySpecException | NoSuchProviderException e
            }
            return true;
        }

        override fun setData(key: String?, data: ByteArray?) {
            var ks: KeyStore? = null;
            try {
                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

                ks.load(null);
                if (ks.getCertificate(alias) == null) return;

                val publicKey: PublicKey? = ks.getCertificate(alias).publicKey;

                if (publicKey == null) {
                    //Log.d(tag, "Error: Public key was not found in Keystore");
                    return;
                }

                val value: String = encrypt(publicKey, data);

                val editor: SharedPreferences.Editor? = preferences?.edit();
                editor?.putString(key, value);
                editor?.apply();
            } catch (e: Exception) {
                when (e) {
                    is NoSuchAlgorithmException, is InvalidKeyException, is NoSuchPaddingException,
                    is IllegalBlockSizeException, is BadPaddingException, is NoSuchProviderException,
                    is InvalidKeySpecException, is KeyStoreException, is CertificateException, is IOException -> {

                        try {
                            ks?.deleteEntry(alias)
                        } catch (e1: Exception) {
                            // Just ignore any errors here
                        }
                    }
                }
            }
        }


        override fun getData(key: String?): ByteArray? {
            var ks: KeyStore? = null;
            try {
                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                ks.load(null);
                val privateKey: Key = ks.getKey(alias, null);
                return decrypt(privateKey, preferences?.getString(key, null));
            } catch (e: Exception) {
                //KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                // | UnrecoverableEntryException | InvalidKeyException | NoSuchPaddingException
                // | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
                try {
                    ks?.deleteEntry(alias); // СверхЗащита - удаляем ключи если что-то пошло не так.
                } catch (e1: Exception) {
                    // Just ignore any errors here
                }
            }
            return null;
        }


        override fun remove(key: String?) {
            val editor: SharedPreferences.Editor? = preferences?.edit();
            editor?.remove(key);
            editor?.apply();
        }

        private fun encrypt(encryptionKey: PublicKey, data: ByteArray?): String {
            val cipher: Cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            val encrypted: ByteArray = cipher.doFinal(data);
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        }

        private fun decrypt(decryptionKey: Key, encryptedData: String?): ByteArray? {
            if (encryptedData == null) return null;
            val encryptedBuffer: ByteArray = Base64.decode(encryptedData, Base64.DEFAULT);
            val cipher: Cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
            return cipher.doFinal(encryptedBuffer);
        }

    }
}