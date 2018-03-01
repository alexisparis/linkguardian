package org.blackdog.linkguardian.service.template.impl;

import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.service.LinkResponse;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.TargetDeterminationError;
import org.blackdog.linkguardian.service.exception.LinkException;
import org.blackdog.linkguardian.service.template.LinkTargetProcessorTemplateMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * LinkTargetProcessorTemplateMethod able to deal with http response {@link ResponseEntity}
 */
public class HttpLinkTargetProcessorTemplateMethod extends LinkTargetProcessorTemplateMethod<ResponseEntity> {

    public HttpLinkTargetProcessorTemplateMethod(LinkService linkService) {
        super(linkService);
    }

    @Override
    protected ResponseEntity onLinkCreated(CallContext context, Link newLink) {
        if (context.getTarget().isClientError() || context.getTarget().isServerError()) {
            return new ResponseEntity<>(LinkResponse.of(newLink), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(LinkResponse.of(newLink), HttpStatus.OK);
        }
    }

    @Override
    protected ResponseEntity onLinkAlreadyExist(CallContext context, Link link) {
        return new ResponseEntity<>(LinkResponse.of((String)null), HttpStatus.ALREADY_REPORTED);
    }

    @Override
    protected ResponseEntity onUnknownHostException(CallContext context) {
        return new ResponseEntity<>(LinkResponse.of("unknownHost"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity onLinkException(CallContext context, LinkException e) {
        return new ResponseEntity<>(LinkResponse.of("defaultError"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity onTargetDeterminationError(CallContext context, TargetDeterminationError error) {

        switch (error)
        {
            case INVALID_CONNECTION_TYPE:
                return new ResponseEntity<>(LinkResponse.of("invalidConnectionType"), HttpStatus.INTERNAL_SERVER_ERROR);
            case EXCEPTION :
                return new ResponseEntity<>(LinkResponse.of("defaultError"), HttpStatus.INTERNAL_SERVER_ERROR);
            case INFINITE_LOOP :
                return new ResponseEntity<>(LinkResponse.of("redirectionLoop"), HttpStatus.INTERNAL_SERVER_ERROR);
            case TOO_MANY_LOOP :
                return new ResponseEntity<>(LinkResponse.of("tooMuchRedirections"), HttpStatus.INTERNAL_SERVER_ERROR);
            case UNKNOWN_HOST_EXCEPTION :
                return new ResponseEntity<>(LinkResponse.of("unknownHost"), HttpStatus.INTERNAL_SERVER_ERROR);
            case MALFORMED_URL :
                return new ResponseEntity<>(LinkResponse.of("invalidUrlWithCause"), HttpStatus.INTERNAL_SERVER_ERROR);
            case SSL_HANDSHAKE_ERROR:
                return new ResponseEntity<>(LinkResponse.of("sslHandshakeError"), HttpStatus.INTERNAL_SERVER_ERROR);
            default :
                return new ResponseEntity<>(LinkResponse.of("defaultError"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
