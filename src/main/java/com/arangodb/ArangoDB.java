/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb;

import com.arangodb.entity.ArangoDBEngine;
import com.arangodb.entity.ArangoDBVersion;
import com.arangodb.entity.LoadBalancingStrategy;
import com.arangodb.entity.LogEntity;
import com.arangodb.entity.LogEntriesEntity;
import com.arangodb.entity.LogLevelEntity;
import com.arangodb.entity.Permissions;
import com.arangodb.entity.ServerRole;
import com.arangodb.entity.UserEntity;
import com.arangodb.internal.ArangoContext;
import com.arangodb.internal.ArangoDBImpl;
import com.arangodb.internal.ArangoDefaults;
import com.arangodb.internal.InternalArangoDBBuilder;
import com.arangodb.internal.http.HttpCommunication;
import com.arangodb.internal.http.HttpConnectionFactory;
import com.arangodb.internal.net.ConnectionFactory;
import com.arangodb.internal.net.Host;
import com.arangodb.internal.net.HostHandle;
import com.arangodb.internal.net.HostHandler;
import com.arangodb.internal.net.HostResolver;
import com.arangodb.internal.util.ArangoDeserializerImpl;
import com.arangodb.internal.util.ArangoSerializationFactory;
import com.arangodb.internal.util.ArangoSerializerImpl;
import com.arangodb.internal.util.DefaultArangoSerialization;
import com.arangodb.internal.velocystream.VstCommunicationSync;
import com.arangodb.internal.velocystream.VstConnectionFactorySync;
import com.arangodb.model.DBCreateOptions;
import com.arangodb.model.LogOptions;
import com.arangodb.model.UserCreateOptions;
import com.arangodb.model.UserUpdateOptions;
import com.arangodb.util.ArangoCursorInitializer;
import com.arangodb.util.ArangoDeserializer;
import com.arangodb.util.ArangoSerialization;
import com.arangodb.util.ArangoSerializer;
import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackAnnotationFieldFilter;
import com.arangodb.velocypack.VPackAnnotationFieldNaming;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackInstanceCreator;
import com.arangodb.velocypack.VPackJsonDeserializer;
import com.arangodb.velocypack.VPackJsonSerializer;
import com.arangodb.velocypack.VPackModule;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackParserModule;
import com.arangodb.velocypack.VPackSerializer;
import com.arangodb.velocypack.ValueType;
import com.arangodb.velocystream.Request;
import com.arangodb.velocystream.Response;
import org.apache.http.client.HttpRequestRetryHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Properties;

/**
 * Central access point for applications to communicate with an ArangoDB server.
 *
 * <p>
 * Will be instantiated through {@link ArangoDB.Builder}
 * </p>
 *
 * <pre>
 * ArangoDB arango = new ArangoDB.Builder().build();
 * ArangoDB arango = new ArangoDB.Builder().host("127.0.0.1", 8529).build();
 * </pre>
 *
 * @author Mark Vollmary
 * @author Michele Rastelli
 */
@SuppressWarnings("UnusedReturnValue")
public interface ArangoDB extends ArangoSerializationAccessor {

    /**
     * Builder class to build an instance of {@link ArangoDB}.
     *
     * @author Mark Vollmary
     */
    class Builder extends InternalArangoDBBuilder {

        private static final String PROPERTY_KEY_PROTOCOL = "arangodb.protocol";

        protected Protocol protocol;

        public Builder() {
            super();
        }

        @Override
        protected void loadProperties(final Properties properties) {
            super.loadProperties(properties);
            protocol = loadProtocol(properties, protocol);
        }

        private static Protocol loadProtocol(final Properties properties, final Protocol currentValue) {
            return Protocol.valueOf(
                    getProperty(properties, PROPERTY_KEY_PROTOCOL, currentValue, ArangoDefaults.DEFAULT_NETWORK_PROTOCOL)
                            .toUpperCase());
        }

        public Builder useProtocol(final Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        @Override
        public Builder loadProperties(final InputStream in) throws ArangoDBException {
            super.loadProperties(in);
            return this;
        }

        /**
         * Adds a host to connect to. Multiple hosts can be added to provide fallbacks.
         *
         * @param host address of the host
         * @param port port of the host
         * @return {@link ArangoDB.Builder}
         */
        public Builder host(final String host, final int port) {
            setHost(host, port);
            return this;
        }

        /**
         * Sets the connection and request timeout in milliseconds.
         *
         * @param timeout timeout in milliseconds
         * @return {@link ArangoDB.Builder}
         */
        public Builder timeout(final Integer timeout) {
            setTimeout(timeout);
            return this;
        }

        /**
         * Sets the username to use for authentication.
         *
         * @param user the user in the database (default: {@code root})
         * @return {@link ArangoDB.Builder}
         */
        public Builder user(final String user) {
            setUser(user);
            return this;
        }

        /**
         * Sets the password for the user for authentication.
         *
         * @param password the password of the user in the database (default: {@code null})
         * @return {@link ArangoDB.Builder}
         */
        public Builder password(final String password) {
            setPassword(password);
            return this;
        }

        /**
         * If set to {@code true} SSL will be used when connecting to an ArangoDB server.
         *
         * @param useSsl whether or not use SSL (default: {@code false})
         * @return {@link ArangoDB.Builder}
         */
        public Builder useSsl(final Boolean useSsl) {
            setUseSsl(useSsl);
            return this;
        }

        /**
         * Sets the SSL context to be used when {@code true} is passed through {@link #useSsl(Boolean)}.
         *
         * @param sslContext SSL context to be used
         * @return {@link ArangoDB.Builder}
         */
        public Builder sslContext(final SSLContext sslContext) {
            setSslContext(sslContext);
            return this;
        }

        /**
         * Sets the {@link javax.net.ssl.HostnameVerifier} to be used when using ssl with http protocol.
         *
         * @param hostnameVerifier HostnameVerifier to be used
         * @return {@link ArangoDB.Builder}
         */
        public Builder hostnameVerifier(final HostnameVerifier hostnameVerifier) {
            setHostnameVerifier(hostnameVerifier);
            return this;
        }

        /**
         * Sets the {@link HttpRequestRetryHandler} to be used when using http protocol.
         *
         * @param httpRequestRetryHandler HttpRequestRetryHandler to be used
         * @return {@link ArangoDB.Builder}
         *
         * <br /><br />
         * NOTE:
         * Some ArangoDB HTTP endpoints do not honor RFC-2616 HTTP 1.1 specification in respect to
         * <a href="https://tools.ietf.org/html/rfc2616#section-9.1">9.1 Safe and Idempotent Methods</a>.
         * Please refer to <a href="https://www.arangodb.com/docs/stable/http/">HTTP API Documentation</a> for details.
         */
        public Builder httpRequestRetryHandler(final HttpRequestRetryHandler httpRequestRetryHandler) {
            setHttpRequestRetryHandler(httpRequestRetryHandler);
            return this;
        }

        /**
         * Sets the chunk size when {@link Protocol#VST} is used.
         *
         * @param chunksize size of a chunk in bytes
         * @return {@link ArangoDB.Builder}
         */
        public Builder chunksize(final Integer chunksize) {
            setChunksize(chunksize);
            return this;
        }

        /**
         * Sets the maximum number of connections the built in connection pool will open per host.
         *
         * <p>
         * Defaults:
         * </p>
         *
         * <pre>
         * {@link Protocol#VST} == 1
         * {@link Protocol#HTTP_JSON} == 20
         * {@link Protocol#HTTP_VPACK} == 20
         * </pre>
         *
         * @param maxConnections max number of connections
         * @return {@link ArangoDB.Builder}
         */
        public Builder maxConnections(final Integer maxConnections) {
            setMaxConnections(maxConnections);
            return this;
        }

        /**
         * Set the maximum time to life of a connection. After this time the connection will be closed automatically.
         *
         * @param connectionTtl the maximum time to life of a connection in milliseconds
         * @return {@link ArangoDB.Builder}
         */
        public Builder connectionTtl(final Long connectionTtl) {
            setConnectionTtl(connectionTtl);
            return this;
        }

        /**
         * Set the keep-alive interval for VST connections. If set, every VST connection will perform a no-op request every
         * {@code keepAliveInterval} seconds, to avoid to be closed due to inactivity by the server (or by the external
         * environment, eg. firewall, intermediate routers, operating system).
         *
         * @param keepAliveInterval interval in seconds
         * @return {@link ArangoDB.Builder}
         */
        public Builder keepAliveInterval(final Integer keepAliveInterval) {
            setKeepAliveInterval(keepAliveInterval);
            return this;
        }

        /**
         * Whether or not the driver should acquire a list of available coordinators in an ArangoDB cluster or a single
         * server with active failover.
         * In case of Active-Failover deployment set to {@code true} to enable automatic master discovery.
         *
         * <p>
         * The host list will be used for failover and load balancing.
         * </p>
         *
         * @param acquireHostList whether or not automatically acquire a list of available hosts (default: false)
         * @return {@link ArangoDB.Builder}
         */
        public Builder acquireHostList(final Boolean acquireHostList) {
            setAcquireHostList(acquireHostList);
            return this;
        }

        /**
         * Setting the Interval for acquireHostList
         *
         * @param acquireHostListInterval Interval in Seconds
         * @return {@link ArangoDB.Builder}
         */
        public Builder acquireHostListInterval(final Integer acquireHostListInterval) {
            setAcquireHostListInterval(acquireHostListInterval);
            return this;
        }

        /**
         * Sets the load balancing strategy to be used in an ArangoDB cluster setup.
         * In case of Active-Failover deployment set to {@link LoadBalancingStrategy#NONE} or not set at all, since that
         * would be the default.
         *
         * @param loadBalancingStrategy the load balancing strategy to be used (default: {@link LoadBalancingStrategy#NONE}
         * @return {@link ArangoDB.Builder}
         */
        public Builder loadBalancingStrategy(final LoadBalancingStrategy loadBalancingStrategy) {
            setLoadBalancingStrategy(loadBalancingStrategy);
            return this;
        }

        /**
         * Register a custom {@link VPackSerializer} for a specific type to be used within the internal serialization
         * process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param clazz      the type the serializer should be registered for
         * @param serializer serializer to register
         * @return {@link ArangoDB.Builder}
         */
        public <T> Builder registerSerializer(final Class<T> clazz, final VPackSerializer<T> serializer) {
            vpackBuilder.registerSerializer(clazz, serializer);
            return this;
        }

        /**
         * Register a special serializer for a member class which can only be identified by its enclosing class.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param clazz      the type of the enclosing class
         * @param serializer serializer to register
         * @return {@link ArangoDB.Builder}
         */
        public <T> Builder registerEnclosingSerializer(final Class<T> clazz, final VPackSerializer<T> serializer) {
            vpackBuilder.registerEnclosingSerializer(clazz, serializer);
            return this;
        }

        /**
         * Register a custom {@link VPackDeserializer} for a specific type to be used within the internal serialization
         * process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param clazz        the type the serializer should be registered for
         * @param deserializer
         * @return {@link ArangoDB.Builder}
         */
        public <T> Builder registerDeserializer(final Class<T> clazz, final VPackDeserializer<T> deserializer) {
            vpackBuilder.registerDeserializer(clazz, deserializer);
            return this;
        }

        /**
         * Register a custom {@link VPackInstanceCreator} for a specific type to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param clazz   the type the instance creator should be registered for
         * @param creator
         * @return {@link ArangoDB.Builder}
         */
        public <T> Builder registerInstanceCreator(final Class<T> clazz, final VPackInstanceCreator<T> creator) {
            vpackBuilder.registerInstanceCreator(clazz, creator);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonDeserializer} for a specific type to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param type         the type the serializer should be registered for
         * @param deserializer
         * @return {@link ArangoDB.Builder}
         */
        public Builder registerJsonDeserializer(final ValueType type, final VPackJsonDeserializer deserializer) {
            vpackParserBuilder.registerDeserializer(type, deserializer);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonDeserializer} for a specific type and attribute name to be used within the
         * internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param attribute
         * @param type         the type the serializer should be registered for
         * @param deserializer
         * @return {@link ArangoDB.Builder}
         */
        public Builder registerJsonDeserializer(
                final String attribute,
                final ValueType type,
                final VPackJsonDeserializer deserializer) {
            vpackParserBuilder.registerDeserializer(attribute, type, deserializer);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonSerializer} for a specific type to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param clazz      the type the serializer should be registered for
         * @param serializer
         * @return {@link ArangoDB.Builder}
         */
        public <T> Builder registerJsonSerializer(final Class<T> clazz, final VPackJsonSerializer<T> serializer) {
            vpackParserBuilder.registerSerializer(clazz, serializer);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonSerializer} for a specific type and attribute name to be used within the
         * internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param attribute
         * @param clazz      the type the serializer should be registered for
         * @param serializer
         * @return {@link ArangoDB.Builder}
         */
        public <T> Builder registerJsonSerializer(
                final String attribute,
                final Class<T> clazz,
                final VPackJsonSerializer<T> serializer) {
            vpackParserBuilder.registerSerializer(attribute, clazz, serializer);
            return this;
        }

        /**
         * Register a custom {@link VPackAnnotationFieldFilter} for a specific type to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param type        the type the serializer should be registered for
         * @param fieldFilter
         * @return {@link ArangoDB.Builder}
         */
        public <A extends Annotation> Builder annotationFieldFilter(
                final Class<A> type,
                final VPackAnnotationFieldFilter<A> fieldFilter) {
            vpackBuilder.annotationFieldFilter(type, fieldFilter);
            return this;
        }

        /**
         * Register a custom {@link VPackAnnotationFieldNaming} for a specific type to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param type        the type the serializer should be registered for
         * @param fieldNaming
         * @return {@link ArangoDB.Builder}
         */
        public <A extends Annotation> Builder annotationFieldNaming(
                final Class<A> type,
                final VPackAnnotationFieldNaming<A> fieldNaming) {
            vpackBuilder.annotationFieldNaming(type, fieldNaming);
            return this;
        }

        /**
         * Register a {@link VPackModule} to be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param module module to register
         * @return {@link ArangoDB.Builder}
         */
        public Builder registerModule(final VPackModule module) {
            vpackBuilder.registerModule(module);
            return this;
        }

        /**
         * Register a list of {@link VPackModule} to be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param modules modules to register
         * @return {@link ArangoDB.Builder}
         */
        public Builder registerModules(final VPackModule... modules) {
            vpackBuilder.registerModules(modules);
            return this;
        }

        /**
         * Register a {@link VPackParserModule} to be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param module module to register
         * @return {@link ArangoDB.Builder}
         */
        public Builder registerJsonModule(final VPackParserModule module) {
            vpackParserBuilder.registerModule(module);
            return this;
        }

        /**
         * Register a list of {@link VPackParserModule} to be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with {@link #serializer(ArangoSerialization)}
         * </p>
         *
         * @param modules modules to register
         * @return {@link ArangoDB.Builder}
         */
        public Builder registerJsonModules(final VPackParserModule... modules) {
            vpackParserBuilder.registerModules(modules);
            return this;
        }

        /**
         * Replace the built-in serializer with the given serializer.
         * <p>
         * <br />
         * <b>ATTENTION!:</b> Use at your own risk
         *
         * @param serializer custom serializer
         * @return {@link ArangoDB.Builder}
         * @deprecated use {@link #serializer(ArangoSerialization)} instead
         */
        @Deprecated
        public Builder setSerializer(final ArangoSerializer serializer) {
            serializer(serializer);
            return this;
        }

        /**
         * Replace the built-in deserializer with the given deserializer.
         * <p>
         * <br />
         * <b>ATTENTION!:</b> Use at your own risk
         *
         * @param deserializer custom deserializer
         * @return {@link ArangoDB.Builder}
         * @deprecated use {@link #serializer(ArangoSerialization)} instead
         */
        @Deprecated
        public Builder setDeserializer(final ArangoDeserializer deserializer) {
            deserializer(deserializer);
            return this;
        }

        /**
         * Replace the built-in serializer/deserializer with the given one.
         * <p>
         * <br />
         * <b>ATTENTION!:</b> Any registered custom serializer/deserializer or module will be ignored.
         *
         * @param serialization custom serializer/deserializer
         * @return {@link ArangoDB.Builder}
         */
        public Builder serializer(final ArangoSerialization serialization) {
            setSerializer(serialization);
            return this;
        }

        /**
         * Returns an instance of {@link ArangoDB}.
         *
         * @return {@link ArangoDB}
         */
        public synchronized ArangoDB build() {
            if (hosts.isEmpty()) {
                hosts.add(host);
            }
            final VPack vpacker = vpackBuilder.serializeNullValues(false).build();
            final VPack vpackerNull = vpackBuilder.serializeNullValues(true).build();
            final VPackParser vpackParser = vpackParserBuilder.build();
            final ArangoSerializer serializerTemp = serializer != null ? serializer
                    : new ArangoSerializerImpl(vpacker, vpackerNull, vpackParser);
            final ArangoDeserializer deserializerTemp = deserializer != null ? deserializer
                    : new ArangoDeserializerImpl(vpackerNull, vpackParser);
            final DefaultArangoSerialization internal = new DefaultArangoSerialization(serializerTemp,
                    deserializerTemp);
            final ArangoSerialization custom = customSerializer != null ? customSerializer : internal;
            final ArangoSerializationFactory util = new ArangoSerializationFactory(internal, custom);

            int protocolMaxConnections = protocol == Protocol.VST ?
                    ArangoDefaults.MAX_CONNECTIONS_VST_DEFAULT :
                    ArangoDefaults.MAX_CONNECTIONS_HTTP_DEFAULT;
            final int max = maxConnections != null ? Math.max(1, maxConnections) : protocolMaxConnections;

            final ConnectionFactory connectionFactory = (protocol == null || Protocol.VST == protocol)
                    ? new VstConnectionFactorySync(host, timeout, connectionTtl, keepAliveInterval, useSsl, sslContext)
                    : new HttpConnectionFactory(timeout, user, password, useSsl, sslContext, hostnameVerifier, custom,
                    protocol, connectionTtl, httpCookieSpec, httpRequestRetryHandler);

            final Collection<Host> hostList = createHostList(max, connectionFactory);
            final HostResolver hostResolver = createHostResolver(hostList, max, connectionFactory);
            final HostHandler hostHandler = createHostHandler(hostResolver);

            return new ArangoDBImpl(
                    new VstCommunicationSync.Builder(hostHandler).timeout(timeout).user(user).password(password)
                            .useSsl(useSsl).sslContext(sslContext).chunksize(chunksize).maxConnections(maxConnections)
                            .connectionTtl(connectionTtl),
                    new HttpCommunication.Builder(hostHandler), util, protocol, hostResolver, new ArangoContext());
        }

    }

    /**
     * Releases all connections to the server and clear the connection pool.
     *
     * @throws ArangoDBException
     */
    void shutdown() throws ArangoDBException;

    /**
     * Returns a {@code ArangoDatabase} instance for the {@code _system} database.
     *
     * @return database handler
     */
    ArangoDatabase db();

    /**
     * Returns a {@code ArangoDatabase} instance for the given database name.
     *
     * @param name Name of the database
     * @return database handler
     */
    ArangoDatabase db(String name);

    /**
     * Creates a new database with the given name.
     *
     * @param name Name of the database to create
     * @return true if the database was created successfully.
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/database-database-management.html#create-database">API
     * Documentation</a>
     */
    Boolean createDatabase(String name) throws ArangoDBException;

    /**
     * Creates a new database with the given name.
     *
     * @param options Creation options
     * @return true if the database was created successfully.
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/database-database-management.html#create-database">API
     * Documentation</a>
     * @since ArangoDB 3.6.0
     */
    Boolean createDatabase(DBCreateOptions options) throws ArangoDBException;

    /**
     * Retrieves a list of all existing databases
     *
     * @return a list of all existing databases
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/database-database-management.html#list-of-databases">API
     * Documentation</a>
     */
    Collection<String> getDatabases() throws ArangoDBException;

    /**
     * Retrieves a list of all databases the current user can access
     *
     * @return a list of all databases the current user can access
     * @throws ArangoDBException
     * @see <a href=
     * "https://www.arangodb.com/docs/stable/http/database-database-management.html#list-of-accessible-databases">API
     * Documentation</a>
     */
    Collection<String> getAccessibleDatabases() throws ArangoDBException;

    /**
     * List available database to the specified user
     *
     * @param user The name of the user for which you want to query the databases
     * @return list of database names which are available for the specified user
     * @throws ArangoDBException
     * @see <a href=
     * "https://www.arangodb.com/docs/stable/http/user-management.html#list-the-accessible-databases-for-a-user">API
     * Documentation</a>
     */
    Collection<String> getAccessibleDatabasesFor(String user) throws ArangoDBException;

    /**
     * Returns the server name and version number.
     *
     * @return the server version, number
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/miscellaneous-functions.html#return-server-version">API
     * Documentation</a>
     */
    ArangoDBVersion getVersion() throws ArangoDBException;

    /**
     * Returns the server storage engine.
     *
     * @return the storage engine name
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/miscellaneous-functions.html#return-server-database-engine-type">API
     * Documentation</a>
     */
    ArangoDBEngine getEngine() throws ArangoDBException;

    /**
     * Returns the server role.
     *
     * @return the server role
     * @throws ArangoDBException
     */
    ServerRole getRole() throws ArangoDBException;

    /**
     * Create a new user. This user will not have access to any database. You need permission to the _system database in
     * order to execute this call.
     *
     * @param user   The name of the user
     * @param passwd The user password
     * @return information about the user
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#create-user">API Documentation</a>
     */
    UserEntity createUser(String user, String passwd) throws ArangoDBException;

    /**
     * Create a new user. This user will not have access to any database. You need permission to the _system database in
     * order to execute this call.
     *
     * @param user    The name of the user
     * @param passwd  The user password
     * @param options Additional options, can be null
     * @return information about the user
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#create-user">API Documentation</a>
     */
    UserEntity createUser(String user, String passwd, UserCreateOptions options) throws ArangoDBException;

    /**
     * Removes an existing user, identified by user. You need access to the _system database.
     *
     * @param user The name of the user
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#remove-user">API Documentation</a>
     */
    void deleteUser(String user) throws ArangoDBException;

    /**
     * Fetches data about the specified user. You can fetch information about yourself or you need permission to the
     * _system database in order to execute this call.
     *
     * @param user The name of the user
     * @return information about the user
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#fetch-user">API Documentation</a>
     */
    UserEntity getUser(String user) throws ArangoDBException;

    /**
     * Fetches data about all users. You can only execute this call if you have access to the _system database.
     *
     * @return informations about all users
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#list-available-users">API
     * Documentation</a>
     */
    Collection<UserEntity> getUsers() throws ArangoDBException;

    /**
     * Partially updates the data of an existing user. The name of an existing user must be specified in user. You can
     * only change the password of your self. You need access to the _system database to change the active flag.
     *
     * @param user    The name of the user
     * @param options Properties of the user to be changed
     * @return information about the user
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#modify-user">API Documentation</a>
     */
    UserEntity updateUser(String user, UserUpdateOptions options) throws ArangoDBException;

    /**
     * Replaces the data of an existing user. The name of an existing user must be specified in user. You can only
     * change the password of your self. You need access to the _system database to change the active flag.
     *
     * @param user    The name of the user
     * @param options Additional properties of the user, can be null
     * @return information about the user
     * @throws ArangoDBException
     * @see <a href="https://www.arangodb.com/docs/stable/http/user-management.html#replace-user">API
     * Documentation</a>
     */
    UserEntity replaceUser(String user, UserUpdateOptions options) throws ArangoDBException;

    /**
     * Sets the default access level for databases for the user {@code user}. You need permission to the _system
     * database in order to execute this call.
     *
     * @param user        The name of the user
     * @param permissions The permissions the user grant
     * @throws ArangoDBException
     * @since ArangoDB 3.2.0
     */
    void grantDefaultDatabaseAccess(String user, Permissions permissions) throws ArangoDBException;

    /**
     * Sets the default access level for collections for the user {@code user}. You need permission to the _system
     * database in order to execute this call.
     *
     * @param user        The name of the user
     * @param permissions The permissions the user grant
     * @throws ArangoDBException
     * @since ArangoDB 3.2.0
     */
    void grantDefaultCollectionAccess(String user, Permissions permissions) throws ArangoDBException;

    /**
     * Generic Execute. Use this method to execute custom FOXX services.
     *
     * @param request VelocyStream request
     * @return VelocyStream response
     * @throws ArangoDBException
     */
    Response execute(Request request) throws ArangoDBException;

    /**
     * Generic Execute. Use this method to execute custom FOXX services.
     *
     * @param request    VelocyStream request
     * @param hostHandle Used to stick to a specific host when using {@link LoadBalancingStrategy#ROUND_ROBIN}
     * @return VelocyStream response
     * @throws ArangoDBException
     */
    Response execute(Request request, HostHandle hostHandle) throws ArangoDBException;

    /**
     * Returns fatal, error, warning or info log messages from the server's global log.
     *
     * @param options
     *         Additional options, can be null
     * @return the log messages
     *
     * @throws ArangoDBException
     * @see <a href= "https://www.arangodb.com/docs/stable/http/administration-and-monitoring.html#read-global-logs-from-the-server">API
     * Documentation</a>
     * @deprecated use {@link #getLogEntries(LogOptions)} instead
     */
    LogEntity getLogs(LogOptions options) throws ArangoDBException;

    /**
     * Returns the server logs
     *
     * @param options
     *         Additional options, can be null
     * @return the log messages
     *
     * @see <a href= "https://www.arangodb.com/docs/stable/http/administration-and-monitoring.html#read-global-logs-from-the-server">API
     * Documentation</a>
     * @since ArangoDB 3.8
     */
    LogEntriesEntity getLogEntries(LogOptions options) throws ArangoDBException;

    /**
     * Returns the server's current loglevel settings.
     *
     * @return the server's current loglevel settings
     * @throws ArangoDBException
     * @since ArangoDB 3.1.0
     */
    LogLevelEntity getLogLevel() throws ArangoDBException;

    /**
     * Modifies and returns the server's current loglevel settings.
     *
     * @param entity loglevel settings
     * @return the server's current loglevel settings
     * @throws ArangoDBException
     * @since ArangoDB 3.1.0
     */
    LogLevelEntity setLogLevel(LogLevelEntity entity) throws ArangoDBException;

    /**
     * <strong>Attention:</strong> Please do not use!
     *
     * @param cursorInitializer
     * @return ArangoDB
     */
    ArangoDB _setCursorInitializer(ArangoCursorInitializer cursorInitializer);

}
