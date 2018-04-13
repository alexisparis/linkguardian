package org.blackdog.linkguardian.service.template;

import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.LinkTarget;
import org.blackdog.linkguardian.service.TargetDeterminationError;
import org.blackdog.linkguardian.service.exception.LinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class LinkTargetProcessorTemplateMethod<R> {

    private final Logger log = LoggerFactory.getLogger(LinkTargetProcessorTemplateMethod.class);

    private LinkService linkService;

    public LinkTargetProcessorTemplateMethod(LinkService linkService) {
        this.linkService = linkService;
    }

    public R process(CallContext context) {

        Assert.notNull(context, "a valid " + CallContext.class + " is needed");
        Assert.notNull(context.getUser(), "a valid " + User.class + " is needed in the context");
        Assert.notNull(context.getTarget(), "a valid " + LinkTarget.class + " is needed in the context");

        if (context.getTarget() == null) {
            return onLinkException(context, new LinkException("target null", null));
        } else {
            if (context.getTarget().getError() == null) {
                try {
                    // check that this url does not already exist
                    log.info("context url : " + context.getUrl());
                    log.info("context target url : " + context.getTarget().getUrl());
                    log.info("context target string url : " + context.getTarget().getStringUrl());
                    log.info("checking if a link already exist with " + context.getTarget().getStringUrl() + "...");
                    Link linksByUserAndUrl =
                        this.linkService.findLinksByUserAndUrl(context.getUser().getLogin(), context.getTarget().getStringUrl());
                    log.info(" => link for url " + context.getTarget() + " ? " + linksByUserAndUrl);
                    if (linksByUserAndUrl != null) {

                        log.debug("url " + context.getTarget() + " already exists");
                        return onLinkAlreadyExist(context, linksByUserAndUrl);
                    } else {

                        Link newLink = this.linkService.createLink(context.getUser(), context.getTarget(), context.getTags());

                        context.setCreatedLink(newLink);

                        return onLinkCreated(context, newLink);
                    }
                } catch (LinkException e) {
                    log.error("error while trying to save new link with url : " + context.getUrl(), e);

                    this.linkService.createToxicLink(context.getUser(), context.getUrl(), e.getMessage());

                    if (e.getCause() != null) {
                            /*if ( e.getCause() instanceof MalformedURLException )
                            {
                                msg = this.error(this.message(code: "service.link.addUrl.invalidUrlWithCause", args: [this.formatUrl(params.url), e.getCause().getMessage()]))
                            }
                            else */
                        // seems to be not possible anymore
//                        if (e.getCause() instanceof UnknownHostException) {
//                            return onUnknownHostException(context);
//                        }
                    }

                    return onLinkException(context, e);
                }
            } else {
                this.linkService.createToxicLink(context.getUser(), context.getUrl(), context.getTarget().getError().name());

                return onTargetDeterminationError(context, context.getTarget().getError());
            }
        }
    }

    protected R onLinkCreated(CallContext context, Link newLink) {
        return null;
    }

    protected R onLinkAlreadyExist(CallContext context, Link link) {
        return null;
    }

    protected R onUnknownHostException(CallContext context) {
        return null;
    }

    protected R onLinkException(CallContext context, LinkException e) {
        return null;
    }

    protected R onTargetDeterminationError(CallContext context, TargetDeterminationError error) {
        return null;
    }

    public static class CallContext {

        private LinkTarget target;

        private User user;

        private String url;

        private Iterable<String> tags;

        private Link createdLink;

        public static CallContext newInstance(LinkTarget target, User user, String url, Iterable<String> tags) {
            return new CallContext(target, user, url, tags);
        }

        private CallContext(LinkTarget target, User user, String url, Iterable<String> tags) {
            this.target = target;
            this.user = user;
            this.url = url;
            this.tags = tags;
        }

        public Link getCreatedLink() {
            return createdLink;
        }

        public void setCreatedLink(Link createdLink) {
            this.createdLink = createdLink;
        }

        public LinkTarget getTarget() {
            return target;
        }

        public User getUser() {
            return user;
        }

        public String getUrl() {
            return url;
        }

        public Iterable<String> getTags() {
            return tags;
        }
    }
}
