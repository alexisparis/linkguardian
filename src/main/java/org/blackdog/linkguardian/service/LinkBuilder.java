package org.blackdog.linkguardian.service;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import net.htmlparser.jericho.*;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.repository.TagRepository;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.service.exception.DomainTooLongException;
import org.blackdog.linkguardian.service.exception.LinkException;
import org.blackdog.linkguardian.service.exception.TagTooLongException;
import org.blackdog.linkguardian.service.exception.UrlTooLongException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by alexisparis on 16/03/16.
 */
@Service
public class LinkBuilder {

    static final int URL_MAX_LENGTH = 2400;

    static final int TITLE_MAX_LENGTH = 255;

    static final int DESCRIPTION_MAX_LENGTH = 255;

    // do not change
    static final int DOMAIN_MAX_LENGTH = 255;

    static final int LABEL_MAX_LENGTH = 255;

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkBuilder.class);

    @Autowired
    private TagRepository tagRepository;

    public LinkBuilder() {
        MicrosoftTagTypes.register();
        //MicrosoftConditionalCommentTagTypes.register();
        PHPTagTypes.register();
        PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        MasonTagTypes.register();
    }

    public Link clone(Link link) {
        Link result = null;

        if ( link != null ) {
            result = new Link();

            result.setTitle(link.getTitle());
            result.setDescription(link.getDescription());
            result.setDomain(link.getDomain());
            result.setUrl(link.getUrl());
            result.setRead(link.isRead());
            result.setNote(link.getNote());
            result.setCreationDate(link.getCreationDate());
            result.setLocked(link.isLocked());

            if ( link.getTags() != null ) {
                result.getTags().addAll(link.getTags());
            }
        }

        return result;
    }

    public void complete(Link link, LinkTarget target) throws LinkException {

        LOGGER.info("completing link with url : " + link.getUrl() + " and target : " + target);

        LOGGER.info("is HTML ? " + target.isHtml());
        LOGGER.info("is client error ? " + target.isClientError());
        LOGGER.info("is server error ? " + target.isServerError());
        //java.net.URL url = new java.net.URL(link.url)
        if ( target.isHtml() && ! target.isClientError() && ! target.isServerError() )
        {
            LOGGER.info("complete html");
            try {
                this.completeHtml(link, target);
            } catch(IOException ex) {
                throw new LinkException("html parsing error", link, ex);
            }
        }
        else
        {
            LOGGER.info("complete default");
            this.completeDefault(link, target);
        }

        if ( link.getTitle() == null )
        {
            link.setTitle("");
        }
        if ( link.getDescription() == null )
        {
            link.setDescription("");
        }

        if ( "goo.gl".equals(link.getDomain()) ) {
            try {
                target.setUrl(new URL(target.getStringUrl()));
            } catch(MalformedURLException ex) {
                throw new LinkException("malformed url", link, ex);
            }
        }

        LOGGER.info("target string url : " + target.getStringUrl());
        LOGGER.info("target url : " + target.getUrl());

        link.setDomain(target.getUrl().getHost());
        link.setOriginalUrl(target.getStringUrl());
        LOGGER.info("setting domain to " + link.getDomain());

        // shorten url with google service
        String urlResource = null;
//        String urlResource = urlShortener.shorten(target.getStringUrl());
//        LOGGER.info("url shortening for : " + target.getStringUrl() + " returns " + urlResource);

        link.setUrl(urlResource);

        if (urlResource == null) {
            link.setUrl(target.getStringUrl());
        }

        if ( link.getTitle() != null && link.getTitle().length() > TITLE_MAX_LENGTH )
        {
            link.setTitle(link.getTitle().substring(0, TITLE_MAX_LENGTH));
        }
        if ( link.getDescription() != null && link.getDescription().length() > DESCRIPTION_MAX_LENGTH )
        {
            link.setDescription(link.getDescription().substring(0, DESCRIPTION_MAX_LENGTH));
        }

        // check validity of link
        if ( link.getDomain() != null && link.getDomain().length() > DOMAIN_MAX_LENGTH ) {
            throw new DomainTooLongException(link.getDomain(), link);
        }
        if ( link.getUrl() != null && link.getUrl().length() > URL_MAX_LENGTH ) {
            throw new UrlTooLongException(link.getUrl(), link);
        }
        if ( link.getOriginalUrl() != null && link.getOriginalUrl().length() > URL_MAX_LENGTH ) {
            throw new UrlTooLongException(link.getOriginalUrl(), link);
        }
    }

    public void addTags(Link link, String... tags) throws TagTooLongException {

        if ( tags != null && tags.length > 0 ) {
            for(int i = 0; i < tags.length; i++) {
                String s = tags[i];
                Tag byLabel = tagRepository.findByLabel(s);
                if (byLabel == null) {

                    LOGGER.info("creating new tag");
                    if (s.length() > LABEL_MAX_LENGTH) {
                        throw new TagTooLongException(s, link);
                    }

                    byLabel = new Tag();
                    byLabel.setLabel(s);

                    tagRepository.save(byLabel);
                }

                link.getTags().add(byLabel);
            }
        }
    }

    /* ##############################
       COMMON
       ############################## */

    public String extractFilenameFrom(URL url)
    {
        String result = null;

        if ( url != null )
        {
            result = url.getFile();

            int lastSlashIndex = result.lastIndexOf('/');
            if ( lastSlashIndex > -1 )
            {
                result = result.substring(lastSlashIndex + 1);

                result = result.replace('-', ' ').replace('_', ' ');
            }

            int lastPointIndex = result.lastIndexOf('.');
            if ( lastPointIndex > -1 )
            {
                result = result.substring(0, lastPointIndex);
            }
        }

        return result;
    }


    private void completeDefault(Link link, LinkTarget target)
    {
        link.setTitle(this.extractFilenameFrom(target.getUrl()));
    }

    private void completeHtml(Link link, LinkTarget target) throws IOException
    {
        LOGGER.info("complete HTML with " + link);
        // make an http request to get the header empty the web site
        Source source = new Source(target.getConnection());

        // Call fullSequentialParse manually as most empty the source will be parsed.
        source.fullSequentialParse();

        link.setTitle(getTitle(source));
        LOGGER.info("setting title to " + link.getTitle());

        String encoding = source.getEncoding();

        String desc = getMetaValue(source,"description");
        if (desc != null) {
            desc = new String(encode(desc, encoding));
        }
        link.setDescription(desc);
        LOGGER.info("setting description to " + link.getDescription());
    }

    private static byte[] encode(CharSequence cs, String charset) {
        ByteBuffer bb = Charset.forName(charset).encode(CharBuffer.wrap(cs));
        byte[] result = new byte[bb.remaining()];
        bb.get(result);
        return result;
    }

    private String getTitle(Source source) {
        Element titleElement=source.getFirstElement(HTMLElementName.TITLE);

        String encoding = source.getEncoding();

        if (titleElement==null) return null;
        // TITLE element never contains other tags so just decode it collapsing whitespace:

        CharSequence content = titleElement.getContent();
        if ( content != null && encoding != null ) {
            content = new String(encode(content, encoding));
        }

        return CharacterReference.decodeCollapseWhiteSpace(content);
    }

    private String getMetaValue(Source source, String key) {
        for (int pos=0; pos<source.length();) {
            StartTag startTag=source.getNextStartTag(pos,"name",key,false);
            if (startTag==null) return null;
            if (startTag.getName()==HTMLElementName.META)
                return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
            pos=startTag.getEnd();
        }
        return null;
    }
}
