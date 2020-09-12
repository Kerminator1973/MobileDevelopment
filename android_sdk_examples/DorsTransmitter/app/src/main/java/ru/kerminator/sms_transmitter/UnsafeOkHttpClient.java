// Код заимствован с сайта: https://futurestud.io/tutorials/retrofit-2-how-to-trust-unsafe-ssl-certificates-self-signed-expired
// Код базируется на решении: https://stackoverflow.com/questions/25509296/trusting-all-certificates-with-okhttp/25992879#25992879

package ru.kerminator.sms_transmitter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class UnsafeOkHttpClient {
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) //throws CertificateException
                        {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) //throws CertificateException
                        {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager. В качестве cipher suit указываем TLS
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // Добавляем в транспортный компонент HTTP(S), объект-перехватчик
            // (interceptor), который будет добавлять дополнительную информацию о
            // запросах и ответах в логи приложения. Поиск по: "D/OkHttp"
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            // Указываем желаемый уровень логирования
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Добавляем logging, как последний interceptor
            builder.addInterceptor(logging);

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}