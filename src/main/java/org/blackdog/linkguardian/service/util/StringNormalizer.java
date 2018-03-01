package org.blackdog.linkguardian.service.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/**
 * String normalizer
 *
 * Created by alexisparis on 16/03/16.
 */
@Service
public class StringNormalizer {

    private static final Pattern pattern = Pattern.compile("\\W");

    public String normalize(String input) {
        String result = null;

        if ( input != null )
        {
            StringTokenizer stringTokenizer = new StringTokenizer(input, " ");

            List<String> tokens = null;

            while(stringTokenizer.hasMoreTokens()) {
                String currentToken = stringTokenizer.nextToken();

                if ( currentToken != null && currentToken.length() > 0 ) {

                    currentToken = Normalizer.normalize(currentToken.trim(), Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                        .replaceAll("_", "");

                    if ( currentToken != null && currentToken.length() > 0 ) {
                        currentToken = currentToken.trim();

                        if( ! currentToken.isEmpty() ) {
                            currentToken = pattern.matcher(currentToken).replaceAll("");
                            if ( currentToken != null && ! currentToken.isEmpty() ) {
                                if (tokens == null) {
                                    tokens = new ArrayList<>();
                                }
                                tokens.add(currentToken);
                            }
                        }
                    }
                }
            }

            if (tokens != null) {
                result = String.join("_", tokens);
            }
        }

        return result;
    }
}
