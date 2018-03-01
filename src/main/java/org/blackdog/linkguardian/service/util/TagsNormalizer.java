package org.blackdog.linkguardian.service.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import javax.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class TagsNormalizer {

    @Inject
    protected StringNormalizer normalizer;

    public String normalize(String tag) {
        return this.normalizer.normalize(tag);
    }

    /**
     * return a set empty tag from the input string
     * @param fusionedTags
     */
    public Set<String> split(String fusionedTags, String split, boolean normalize)
    {
        Set<String> result = null;
        if ( fusionedTags != null ) {
            // split with space
            StringTokenizer tokenizer = new StringTokenizer(fusionedTags, split);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token != null) {

                    String normalized = normalize ? normalizer.normalize(token.toLowerCase()) : token;
                    if (normalized != null) {
                        if (result == null) {
                            result = new HashSet<>();
                        }
                        result.add(normalized);
                    }
                }
            }
        }
        if ( result == null ) {
            result = Collections.<String>emptySet();
        }
        return result;
    }
}
