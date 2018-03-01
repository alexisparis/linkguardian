package org.blackdog.linkguardian.service;

/**
 * Created by alexisparis on 16/03/16.
 */
public enum TargetDeterminationError {

    INFINITE_LOOP,
    TOO_MANY_LOOP,
    INVALID_CONNECTION_TYPE,
    UNKNOWN_HOST_EXCEPTION,
    MALFORMED_URL,
    EXCEPTION,
    SSL_HANDSHAKE_ERROR;
}
