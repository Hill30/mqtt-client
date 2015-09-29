package com.hill30.android.mqttClient;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.content.Context;

public class SslUtility {

    private static TrustManager noCACheckManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    private static SslUtility		mInstance = null;
    private Context					mContext = null;
    private HashMap<Integer, SSLSocketFactory> mSocketFactoryMap = new HashMap<Integer, SSLSocketFactory>();

    public SslUtility(Context context) {
        mContext = context;
    }

    public static SslUtility getInstance( ) {
        if ( null == mInstance ) {
            throw new RuntimeException("first call must be to SslUtility.newInstance(Context) ");
        }
        return mInstance;
    }

    public static SslUtility newInstance( Context context ) {
        if ( null == mInstance ) {
            mInstance = new SslUtility( context );
        }
        return mInstance;
    }


    public SSLSocketFactory getSocketFactory(int certificateId, String certificatePassword ) {

        SSLSocketFactory result = mSocketFactoryMap.get(certificateId);

        if ( ( null == result) && ( null != mContext ) ) {

            try {
                KeyStore keystoreTrust = KeyStore.getInstance("BKS");

                keystoreTrust.load(mContext.getResources().openRawResource(certificateId), certificatePassword.toCharArray());

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                trustManagerFactory.init(keystoreTrust);

                SSLContext sslContext = SSLContext.getInstance("TLS");

                sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

                result = sslContext.getSocketFactory();

                mSocketFactoryMap.put( certificateId, result);	// cache for reuse
            }
            catch ( Exception ex ) {
                // log exception
            }
        }

        return result;
    }

    public SSLSocketFactory getSocketFactoryForSelfSignedCertificate( ) {

        SSLSocketFactory result = mSocketFactoryMap.get(-1);  	// check to see if already created

        if ( ( null == result) && ( null != mContext ) ) {					// not cached so need to load server certificate

            try {

                SSLContext sslContext = SSLContext.getInstance("TLS");

                sslContext.init(null, new TrustManager[]{noCACheckManager}, new SecureRandom());

                result = sslContext.getSocketFactory();

                mSocketFactoryMap.put( -1, result);	// cache for reuse
            }
            catch ( Exception ex ) {
                // log exception
            }
        }

        return result;
    }

}