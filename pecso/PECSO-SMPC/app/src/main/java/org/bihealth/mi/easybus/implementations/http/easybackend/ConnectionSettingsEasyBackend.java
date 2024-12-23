package org.bihealth.mi.easybus.implementations.http.easybackend;

import org.bihealth.mi.easybus.ConnectionSettings;
import org.bihealth.mi.easybus.PasswordStore;
import org.bihealth.mi.easybus.PerformanceListener;
import org.bihealth.mi.easybus.implementations.PasswordProvider;
import org.bihealth.mi.easybus.implementations.http.HTTPAuthentication;
import org.bihealth.mi.easysmpc.resources.Resources;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Settings for Easybackend connections
 *
 * @author Felix Wirth
 */
public class ConnectionSettingsEasyBackend extends ConnectionSettings {

    /**
     * SVUID
     */
    private static final long serialVersionUID = -944743683309534747L;
    /**
     * URL validator
     */
    // TODO Remove http
    private final static UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS);
    /**
     * Auth server URL
     */
    private URL authServer;
    /**
     * Easybackend server URL
     */
    private URL apiServer;
    /**
     * Keycloak realm
     */
    private String realm = Resources.AUTH_REALM_DEFAULT;
    /**
     * Keycloak clien id
     */
    private String clientId = Resources.AUTH_CLIENTID_DEFAULT;
    /**
     * Keycloak client secret
     */
    private String clientSecret;
    /**
     * Proxy
     */
    private URI proxy = null;
    /**
     * Send timeout
     */
    private int sendTimeout;
    /**
     * Maximal message size
     */
    private int maxMessageSize;
    /**
     * Check interval
     */
    private int checkInterval;
    /**
     * Password provider
     */
    private final PasswordProvider provider;
    /**
     * Performance listener
     */
    private transient PerformanceListener listener = null;
    /**
     * Email
     */
    private final String email;

    /**
     * Creates a new instance
     *
     * @param provider
     */
    public ConnectionSettingsEasyBackend(String email, PasswordProvider provider) {

        // Store
        this.email = email;
        this.provider = provider;
    }

    /**
     * Check if url is valid
     *
     * @param url to check
     */
    public static void checkURL(String url) {
        if (!URL_VALIDATOR.isValid(url)) {
            throw new IllegalStateException("URL is not valid!");
        }
    }

    /**
     * Check
     *
     * @param object
     */
    public static void checkNonNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
    }

    @Override
    public String getIdentifier() {
        return this.email;
    }

    /**
     * @return the sendTimeout
     */
    @Override
    public int getSendTimeout() {
        return sendTimeout > 0 ? sendTimeout : Resources.TIMEOUT_SEND_EMAILS_DEFAULT;
    }

    /**
     * @param sendTimeout the sendTimeout to set
     */
    public ConnectionSettingsEasyBackend setSendTimeout(int sendTimeout) {
        this.sendTimeout = sendTimeout;
        return this;
    }

    /**
     * @return the maxMessageSize
     */
    @Override
    public int getMaxMessageSize() {
        return maxMessageSize > 0 ? maxMessageSize : Resources.EMAIL_MAX_MESSAGE_SIZE_DEFAULT;
    }

    /**
     * @param maxMessageSize the maxMessageSize to set
     * @return
     */
    public ConnectionSettingsEasyBackend setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
        return this;
    }

    /**
     * @return the checkInterval
     */
    @Override
    public int getCheckInterval() {
        return checkInterval > 0 ? checkInterval : Resources.INTERVAL_CHECK_EASYBACKEND_DEFAULT;
    }

    /**
     * @param checkInterval the checkInterval to set
     */
    public ConnectionSettingsEasyBackend setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
        return this;
    }

    @Override
    public ExchangeMode getExchangeMode() {
        return ExchangeMode.EASYBACKEND;
    }

    /**
     * @return the authServer
     */
    public URL getAuthServer() {
        return authServer != null ? authServer : apiServer;
    }

    /**
     * @param authServer the authServer to set
     */
    public ConnectionSettingsEasyBackend setAuthServer(URL authServer) {
        this.authServer = authServer;
        return this;
    }

    /**
     * @return the apiServer
     */
    public URL getAPIServer() {
        return apiServer;
    }

    /**
     * @param apiServer the apiServer to set
     */
    public ConnectionSettingsEasyBackend setAPIServer(URL apiServer) {
        checkURL(apiServer.toString());

        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(apiServer.toString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL not understood");
        }

        // Set defaults
        if (uriBuilder.getPort() == -1 || uriBuilder.getPort() == 0) {
            uriBuilder.setPort(443);
        }

        // Set
        try {
            this.apiServer = uriBuilder.build().toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("URL not understood");
        }

        // Return
        return this;
    }

    /**
     * @return the realm
     */
    public String getRealm() {
        return realm != null ? realm : Resources.AUTH_REALM_DEFAULT;
    }

    /**
     * @param realm the realm to set
     */
    public ConnectionSettingsEasyBackend setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId != null ? clientId : Resources.AUTH_CLIENTID_DEFAULT;
    }

    /**
     * @param clientId the clientId to set
     */
    public ConnectionSettingsEasyBackend setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Return config parameter
     *
     * @return the IMAP password
     */
    public String getPassword() {
        return getPassword(true);
    }

    /**
     * Return the password
     *
     * @param usePasswordProvider
     * @return
     */
    public String getPassword(boolean usePasswordProvider) {
        // Potentially ask for password
        if ((this.getPasswordStore() == null || this.getPasswordStore().getFirstPassword() == null) && this.provider != null && usePasswordProvider) {
            // Get passwords
            PasswordStore store = this.provider.getPassword();

            // Check
            if (store == null) {
                return null;
            }

            // Store
            this.setPasswordStore(store);

            // Check connection settings
            if (!this.isValid(false)) {
                setPasswordStore(null);
            }
        }

        // Return password
        return getPasswordStore() == null ? null : getPasswordStore().getFirstPassword();
    }

    /**
     * @return the proxy
     */
    public URI getProxy() {
        return proxy;
    }

    /**
     * @param proxy the proxy to set
     */
    public ConnectionSettingsEasyBackend setProxy(URI proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret the clientSecret to set
     */
    public ConnectionSettingsEasyBackend setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * @return the listener
     */
    public PerformanceListener getListener() {
        return listener;
    }

    /**
     * @param listener the listener to set
     */
    public ConnectionSettingsEasyBackend setListener(PerformanceListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public boolean isValid(boolean usePasswordProvider) {

        if ((this.getPasswordStore() == null || this.getPasswordStore().getFirstPassword() == null) && !usePasswordProvider) {
            return false;
        }

        try {
            // Add check to API server
            new HTTPAuthentication(this).authenticate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
