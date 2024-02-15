package insaneio.insane

const val INSANE_ASSEMBLY_NAME: String = "InsaneIO.Insane"
const val INSANE_CRYPTOGRAPHY_NAMESPACE: String = "InsaneIO.Insane.Cryptography"

const val BASE64_NO_LINE_BREAKS_LENGTH: UInt = 0U
const val BASE64_MIME_LINE_BREAKS_LENGTH: UInt = 76U
const val BASE64_PEM_LINE_BREAKS_LENGTH: UInt = 64U

const val URL_ENCODED_PLUS_SIGN_STRING: String = "%2B"
const val URL_ENCODED_SLASH_STRING: String = "%2F"
const val URL_ENCODED_EQUAL_SIGN_STRING = "%3D"
const val PLUS_SIGN_STRING: String = "+"
const val MINUS_SIGN_STRING: String = "-"
const val SLASH_STRING: String = "/"
const val UNDERSCORE_STRING: String = "_"
const val EQUAL_SIGN_STRING: String = "="
const val LINE_FEED_STRING: String = "\n"
const val CARRIAGE_RETURN_STRING: String = "\r"
const val NEWLINE_WINDOWS_STRING: String = "\r\n"
const val EMPTY_STRING: String = ""

const val MD5_ALGORITHM_NAME_STRING:String = "MD5"
const val SHA1_ALGORITHM_NAME_STRING:String = "SHA-1"
const val SHA256_ALGORITHM_NAME_STRING:String = "SHA-256"
const val SHA384_ALGORITHM_NAME_STRING:String = "SHA-384"
const val SHA512_ALGORITHM_NAME_STRING:String = "SHA-512"

const val HMAC_MD5_ALGORITHM_NAME_STRING:String = "HmacMD5"
const val HMAC_SHA1_ALGORITHM_NAME_STRING:String = "HmacSHA1"
const val HMAC_SHA256_ALGORITHM_NAME_STRING:String = "HmacSHA256"
const val HMAC_SHA384_ALGORITHM_NAME_STRING:String = "HmacSHA384"
const val HMAC_SHA512_ALGORITHM_NAME_STRING:String = "HmacSHA512"

const val SCRYPT_ITERATIONS_FOR_INTERACTIVE_LOGIN: UInt = 2048U;
const val SCRYPT_ITERATIONS_FOR_ENCRYPTION: UInt = 1048576U;
const val SCRYPT_ITERATIONS: UInt = 16384U;
const val SCRYPT_BLOCK_SIZE: UInt = 8U;
const val SCRYPT_PARALLELISM: UInt = 1U;
const val SCRYPT_DERIVED_KEY_LENGTH: UInt = 64U;
const val SCRYPT_SALT_SIZE: UInt = 16U;

const val ARGON2_DERIVED_KEY_LENGTH: UInt = 64U
const val ARGON2_SALT_SIZE: UInt = 16U
const val ARGON2_ITERATIONS: UInt = 2U
const val ARGON2_MEMORY_SIZE_IN_KIB: UInt = 16384U
const val ARGON2_DEGREE_OF_PARALLELISM: UInt = 4U

const val AES_ALGORITHM_STRING:String = "AES"
const val AES_CBC_MODE_STRING:String = "CBC"
const val AES_NO_PADDING_STRING:String = "NoPadding"
const val AES_PKCS7_PADDING_STRING:String = "PKCS5Padding"
const val AES_CBC_INSTANCE_STRING:String = "${AES_ALGORITHM_STRING}/${AES_CBC_MODE_STRING}"
const val AES_MAX_IV_LENGTH:UInt = 16U
const val AES_MAX_KEY_LENGTH:UInt = 32U
const val AES_BLOCK_SIZE_LENGTH: UInt = 16U
const val AES_CBC_NO_PADDING_INSTANCE_STRING:String = "${AES_CBC_INSTANCE_STRING}/${AES_NO_PADDING_STRING}"
const val AES_CBC_PKCS7_PADDING_INSTANCE_STRING:String = "${AES_CBC_INSTANCE_STRING}/${AES_PKCS7_PADDING_STRING}"

const val RSA_ALGORITHM_STRING:String = "RSA"
const val RSA_PEM_KEY_INITIAL_TEXT_HEADER = "-----BEGIN "
const val RSA_PEM_PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----"
const val RSA_PEM_PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----"
const val RSA_PEM_PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----"
const val RSA_PEM_PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----"

const val RSA_PEM_RSA_PRIVATE_KEY_HEADER = "-----BEGIN RSA PRIVATE KEY-----"
const val RSA_PEM_RSA_PRIVATE_KEY_FOOTER = "-----END RSA PRIVATE KEY-----"
const val RSA_PEM_RSA_PUBLIC_KEY_HEADER = "-----BEGIN RSA PUBLIC KEY-----"
const val RSA_PEM_RSA_PUBLIC_KEY_FOOTER = "-----END RSA PUBLIC KEY-----"

const val RSA_PEM_PUBLIC_KEY_REGEX_PATTERN = """^(?:(-----BEGIN PUBLIC KEY-----)(?:\r|\n|\r\n)((?:(?:(?:[A-Za-z0-9+\/]{4}){16}(?:\r|\n|\r\n))+)(?:(?:[A-Za-z0-9+\/]{4}){0,15})(?:(?:[A-Za-z0-9+\/]{4}|[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=)))(?:\r|\n|\r\n)(-----END PUBLIC KEY-----))$"""
const val RSA_PEM_PRIVATE_KEY_REGEX_PATTERN = """^(?:(-----BEGIN PRIVATE KEY-----)(?:\r|\n|\r\n)((?:(?:(?:[A-Za-z0-9+\/]{4}){16}(?:\r|\n|\r\n))+)(?:(?:[A-Za-z0-9+\/]{4}){0,15})(?:(?:[A-Za-z0-9+\/]{4}|[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=)))(?:\r|\n|\r\n)(-----END PRIVATE KEY-----))$"""
const val RSA_PEM_RSA_PUBLIC_KEY_REGEX_PATTERN = """^(?:(-----BEGIN RSA PUBLIC KEY-----)(?:\r|\n|\r\n)((?:(?:(?:[A-Za-z0-9+\/]{4}){16}(?:\r|\n|\r\n))+)(?:(?:[A-Za-z0-9+\/]{4}){0,15})(?:(?:[A-Za-z0-9+\/]{4}|[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=)))(?:\r|\n|\r\n)(-----END RSA PUBLIC KEY-----))$"""
const val RSA_PEM_RSA_PRIVATE_KEY_REGEX_PATTERN = """^(?:(-----BEGIN RSA PRIVATE KEY-----)(?:\r|\n|\r\n)((?:(?:(?:[A-Za-z0-9+\/]{4}){16}(?:\r|\n|\r\n))+)(?:(?:[A-Za-z0-9+\/]{4}){0,15})(?:(?:[A-Za-z0-9+\/]{4}|[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=)))(?:\r|\n|\r\n)(-----END RSA PRIVATE KEY-----))$"""

const val RSA_XML_PUBLIC_KEY_REGEX_PATTERN = """^(<RSAKeyValue>\s*(?:\s*<Modulus>[a-zA-Z0-9\+\/]+={0,2}<\/Modulus>()|\s*<Exponent>[a-zA-Z0-9\+\/]+={0,2}<\/Exponent>()){2}\s*<\/\s*RSAKeyValue>\s*\2\3)$"""
const val RSA_XML_PRIVATE_KEY_REGEX_PATTERN = """^(\s*<RSAKeyValue>\s*(?:\s*<Modulus>[a-zA-Z0-9\+\/]+={0,2}<\/Modulus>()|\s*<Exponent>[a-zA-Z0-9\+\/]+={0,2}<\/Exponent>()|\s*<P>[a-zA-Z0-9\+\/]+={0,2}<\/P>()|\s*<Q>[a-zA-Z0-9\+\/]+={0,2}<\/Q>()|\s*<DP>[a-zA-Z0-9\+\/]+={0,2}<\/DP>()|\s*<DQ>[a-zA-Z0-9\+\/]+={0,2}<\/DQ>()|\s*<InverseQ>[a-zA-Z0-9\+\/]+={0,2}<\/InverseQ>()|\s*<D>[a-zA-Z0-9\+\/]+={0,2}<\/D>()){8}\s*<\/\s*RSAKeyValue>\s*\2\3\4\5\6\7\8\9)$"""

const val BASE64_VALUE_REGEX_PATTERN = """^(?:(?:[A-Za-z0-9+\/]{4})*)(?:[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=)?$"""

const val RSA_XML_KEY_MAIN_NODE = "RSAKeyValue"
const val RSA_XML_KEY_P_NODE = "P"
const val RSA_XML_KEY_Q_NODE = "Q"
const val RSA_XML_KEY_DP_NODE = "DP"
const val RSA_XML_KEY_DQ_NODE = "DQ"
const val RSA_XML_KEY_INVERSE_Q_NODE = "InverseQ"
const val RSA_XML_KEY_D_NODE = "D"
const val RSA_XML_KEY_MODULUS_NODE = "Modulus"
const val RSA_XML_KEY_EXPONENT_NODE = "Exponent"
const val RSA_XML_KEY_MAIN_TAG = "<$RSA_XML_KEY_MAIN_NODE>"
const val RSA_XML_PUBLIC_KEY_FORMAT = """
    <$RSA_XML_KEY_MAIN_NODE>
        <$RSA_XML_KEY_MODULUS_NODE>%s</$RSA_XML_KEY_MODULUS_NODE>
        <$RSA_XML_KEY_EXPONENT_NODE>%s</$RSA_XML_KEY_EXPONENT_NODE>
    </$RSA_XML_KEY_MAIN_NODE>
"""
const val RSA_XML_PRIVATE_KEY_FORMAT = """
    <$RSA_XML_KEY_MAIN_NODE>
        <$RSA_XML_KEY_MODULUS_NODE>%s</$RSA_XML_KEY_MODULUS_NODE>
        <$RSA_XML_KEY_EXPONENT_NODE>%s</$RSA_XML_KEY_EXPONENT_NODE>
        <$RSA_XML_KEY_P_NODE>%s</$RSA_XML_KEY_P_NODE>
        <$RSA_XML_KEY_Q_NODE>%s</$RSA_XML_KEY_Q_NODE>
        <$RSA_XML_KEY_DP_NODE>%s</$RSA_XML_KEY_DP_NODE>
        <$RSA_XML_KEY_DQ_NODE>%s</$RSA_XML_KEY_DQ_NODE>
        <$RSA_XML_KEY_INVERSE_Q_NODE>%s</$RSA_XML_KEY_INVERSE_Q_NODE>
        <$RSA_XML_KEY_D_NODE>%s</$RSA_XML_KEY_D_NODE>
    </$RSA_XML_KEY_MAIN_NODE>
"""
const val RSA_PADDING_PKCS1 = "PKCS1Padding"
const val RSA_PADDING_OAEP_SHA1 = "OAEPWithSHA-1AndMGF1Padding"
const val RSA_PADDING_OAEP_SHA256 = "OAEPWithSHA-256AndMGF1Padding"
const val RSA_PADDING_OAEP_SHA384 = "OAEPWithSHA-384AndMGF1Padding"
const val RSA_PADDING_OAEP_SHA512 = "OAEPWithSHA-512AndMGF1Padding"
