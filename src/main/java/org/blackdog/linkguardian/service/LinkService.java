package org.blackdog.linkguardian.service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.net.ssl.SSLHandshakeException;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.repository.search.LinkSearchRepository;
import org.blackdog.linkguardian.service.exception.LinkException;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Link.
 */
@Service
@Transactional
public class LinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkService.class);

    @Autowired
    private ToxicLinkRepository toxicLinkRepository;

    @Autowired
    private LinkBuilder linkBuilder;

    @Autowired
    private TagsNormalizer tagsNormalizer;

    private final LinkRepository linkRepository;

    private final LinkSearchRepository linkSearchRepository;

    public LinkService(LinkRepository linkRepository, LinkSearchRepository linkSearchRepository) {
        this.linkRepository = linkRepository;
        this.linkSearchRepository = linkSearchRepository;
    }

    /**
     * Save a link.
     *
     * @param link the entity to save
     * @return the persisted entity
     */
    public Link save(Link link) {
        LOGGER.debug("Request to save Link : {}", link);
        Link result = linkRepository.save(link);
        linkSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the links.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Link> findAll(Pageable pageable) {
        LOGGER.debug("Request to get all Links");
        return linkRepository.findAll(pageable);
    }

    /**
     * Get one link by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Link findOne(Long id) {
        LOGGER.debug("Request to get Link : {}", id);
        return linkRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the link by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        LOGGER.debug("Request to delete Link : {}", id);
        linkRepository.delete(id);
        linkSearchRepository.delete(id);
    }

    /**
     * Search for the link corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Link> search(String query, Pageable pageable) {
        LOGGER.debug("Request to search for a page of Links for query {}", query);
        Page<Link> result = linkSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

    /**
     * return the link associated with the given user with the given url if exists
     * @param login the login of a user
     * @param url an url
     * @return a Link or null if not found
     */
    public Link findLinksByUserAndUrl(String login, String url) {
        return this.linkRepository.findLinksByUserAndUrl(login, url);
    }

    /**
     * create a toxic link entry
     * @param user a {@link User}
     * @param url the url
     * @param error an error message of code
     */
    public void createToxicLink(User user, String url, String error) {

        // log toxic link
        ToxicLink toxicLink = new ToxicLink();
        toxicLink.setEmail(user.getEmail());
        toxicLink.setCreationDate(ZonedDateTime.now());
        toxicLink.setUrl(url);
        toxicLink.setError(error);
        toxicLinkRepository.save(toxicLink);
    }

    public Long getLinkCreatedByUserCount(User user) {
        return this.linkRepository.countByLogin(user.getLogin());
    }

    public Long getToxicLinkCreatedByUserCount(User user) {
        return this.toxicLinkRepository.countByMail(user.getEmail());
    }

    /**
     * create a new link for the given user, target and tag
     * @param user a User
     * @param target the target to the link
     * @param tags the tags
     * @return a new Link
     * @throws LinkException
     */
    public Link createLink(User user, LinkTarget target, Iterable<String> tags) throws
        LinkException {

        LOGGER.debug("creating detached link... with url : " + target.getStringUrl());
        Link newLink = new Link();
        //url: target.stringUrl, fusionedTags: " " + _tag + " ", creationDate: new Date(), person: connectedPerson)
        newLink.setOriginalUrl(target.getStringUrl());
        newLink.setCreationDate(ZonedDateTime.now());
        newLink.setUser(user);
        newLink.setRead(Boolean.FALSE);

        LOGGER.debug("detached link created");

        this.linkBuilder.complete(newLink, target);
        LOGGER.debug("link completed");

        if(tags != null) {
            Set<String> setOfTags = new HashSet<>();
            for(String current : tags) {
                LOGGER.debug("considering tag '" + current + "'");
                String normalized = this.tagsNormalizer.normalize(current);
                setOfTags.add(normalized);
                LOGGER.debug("tag '" + normalized + "' added");
            }
            // this.linkBuilder.extractTags(tag, false);
            this.linkBuilder.addTags(newLink, setOfTags.toArray(new String[setOfTags.size()]));
            LOGGER.debug("tags added to the link");
        }

        LOGGER.debug("saving new link...");
        this.linkRepository.saveAndFlush(newLink);
        LOGGER.debug("new link saved");

        return newLink;
    }

    /**
     * determine the real target of an url
     * @param newurl an url
     * @return a LinkTarget
     */
    public LinkTarget determineTarget(String newurl)
    {
        String url = newurl;

        // if params.url does not contains ://, then add http://
        if ( url != null && url.indexOf("://") == -1 )
        {
            LOGGER.info("adding http:// to url without protocol : " + url);
            url = "http://" + url;
        }
        LinkTarget target = new LinkTarget();

        // manage redirect url
        Set<String> urls = new HashSet<>();
        int redirectLimitCount = 10;
        int redirectCount = 0;
        String currentUrl = url;

        boolean stopLoop = false;

        try
        {
            if (url == null) {
                throw new UnknownHostException("invalid null url");
            }

            while(!stopLoop)
            {
                if ( urls.add(currentUrl) )
                {
                    target.setStringUrl(currentUrl);
                    target.setUrl(new URL(currentUrl));
                    target.setConnection(target.getUrl().openConnection());
                    target.getConnection().
                        setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

                    target.setContentType(null);
                    target.setResponseCode(0);

                    if ( target.getConnection() instanceof HttpURLConnection)
                    {
                        HttpURLConnection _httpConnection = (HttpURLConnection) target.getConnection();
                        _httpConnection.setConnectTimeout(5000);
                        _httpConnection.setReadTimeout(5000);
                        _httpConnection.setInstanceFollowRedirects(true);

                        target.setResponseCode(_httpConnection.getResponseCode());
                        LOGGER.debug("getting http code " + target.getResponseCode() + " for url " + target.getStringUrl());
                        LOGGER.debug("connection url : " + target.getConnection().getURL());

                        target.setContentType(_httpConnection.getHeaderField("content-type"));
                        LOGGER.debug("content type : " + target.getContentType());

                        if ( LOGGER.isDebugEnabled() )
                        {   _httpConnection.getHeaderFields().entrySet().stream().forEach(
                            new Consumer<Map.Entry<String, List<String>>>() {
                                @Override
                                public void accept(Map.Entry<String, List<String>> it) {
                                    LOGGER.debug("   " + it.getKey() + " ==> " + it.getValue());
                                }
                            });
                        }

                        if( target.isRedirection() )
                        {
                            LOGGER.debug("it's a redirection");
                            currentUrl = null;

                            String location = target.getHeaderLocation();
                            if ( location != null ){
                                currentUrl = location;
                                LOGGER.debug("setting current url to " + location);
                            }
                        }
                        else if ( target.isClientError() || target.isServerError() )
                        {
                            // the host seems to exist because no UnknwonHostException thrown
                            // but for some reason, access to this url from openshift server provoke errors
                            // we won't be able to parse the real page but we will try to extract a title from the url
                            stopLoop = true;
                        }
                        else
                        {
                            target.setStringUrl(_httpConnection.getURL().toString()); // could be different from currentUrl !!!!
                            stopLoop = true;
                        }
                    }
                    else
                    {
                        LOGGER.error("don't know what to do with a connection empty type : " + target.getConnection().getClass());
                        target.setError(TargetDeterminationError.INVALID_CONNECTION_TYPE);
                        stopLoop = true;
                    }
                }
                else
                {
                    LOGGER.debug("url " + currentUrl + " already visited ==> redirection loop");
                    target.setError(TargetDeterminationError.INFINITE_LOOP);
                    stopLoop = true;
                }

                redirectCount++;

                if ( ! stopLoop && redirectCount >= redirectLimitCount )
                {
                    target.setError(TargetDeterminationError.TOO_MANY_LOOP);
                    stopLoop = true;
                }
            }
        }
        catch(SSLHandshakeException e) {
            target.setError(TargetDeterminationError.SSL_HANDSHAKE_ERROR);
            target.setException(e);
            LOGGER.error("SSL handshake error", e);
        }
        catch(UnknownHostException e)
        {
            target.setError(TargetDeterminationError.UNKNOWN_HOST_EXCEPTION);
            target.setException(e);
            LOGGER.error("unknown host exception", e);
        }
        catch(MalformedURLException e)
        {
            target.setError(TargetDeterminationError.MALFORMED_URL);
            target.setException(e);
            LOGGER.error("malformed url : " + url, e);
        }
        catch(Exception e)
        {
            target.setError(TargetDeterminationError.EXCEPTION);
            target.setException(e);
            LOGGER.error("error while trying to resolve redirections", e);
        }

        return target;
    }
}
