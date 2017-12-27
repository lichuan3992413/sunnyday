package sunnyday.gateway.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

/**
 * 密码加密解密
 * @Author yzy
 * @Date 2015-12-9下午4:54:32
 * @Description 
 */
public class PassWordUtil {
	private static Logger log = CommonLogFactory.getLog(PassWordUtil.class);
    //密钥算法 
    public static final String KEY_ALGORITHM = "DESede";

    //加密/解密算法/工作模式/填充方式 
    public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

    //key
    public static final String KEY_STRING = "HwKu/Wj+f14sAXoLhQJuuh8Crv1o/n9e";

    //密钥 
    private static SecretKey secretKey;

    /*
     * KEY_STRING暂时是固定的，如果非固定需要改正
     */
    static {
        DESedeKeySpec dks = null;
        SecretKeyFactory keyFactory = null;
        try {
            //解码key
            byte[] key = Base64.decodeBase64(KEY_STRING);
            //  实例化Des密钥         
            dks = new DESedeKeySpec(key);
            //  实例化密钥工厂        
            keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            //  生成密钥       
            secretKey = keyFactory.generateSecret(dks);
        } catch (InvalidKeyException e) {
        	log.error("",e);
        } catch (NoSuchAlgorithmException e) {
        	log.error("",e);
        } catch (InvalidKeySpecException e) {
        	log.error("",e);
        }
    }

    /**
     * 
     * encrypt(加密数据)  
     * @param   name      
     * @param  @return    设定文件      
     * @return String    DOM对象      
     * @Exception 异常对象      
     * @since  CodingExample　Ver(编码范例查看) 1.1
     */
    public static String encrypt(String data) throws Exception {

        // 实例化         
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置为加密模式         
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // 获取加密后数据 
        byte[] temp = cipher.doFinal(data.getBytes());

        return Base64.encodeBase64String(temp);
    }

    /**
     * 
     * decrypt(解密数据)  
     * @param   name      
     * @param  @return    设定文件      
     * @return String    DOM对象      
     * @Exception 异常对象      
     * @since  CodingExample　Ver(编码范例查看) 1.1
     */
    public static String decrypt(String data) throws Exception {
        //  实例化        
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //  初始化，设置为解密模式         
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 获取解密后数据      
        byte[] temp = cipher.doFinal(Base64.decodeBase64(data));
        return new String(temp);
    }
    
    public static void main(String[] args) throws Exception {

        String str = "F3xB2HDL9dS6bpBtWq1zo0u8CMzIzsEsEcRe4rgnrZFCUuCcUTSNKvylXvtXo9IlzZkHgrylB3/9Cg1zy79dHxY94WTfBf1q";
        System.out.println("加密前：" + str);
        //  加密数据         
       // System.out.println("加密后：" + PassWordUtil.encrypt(str));
        //  解密数据         
        System.out.println("解密后：" + PassWordUtil.decrypt(str));

    }
}
