/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.hibernate.tool.ide.completion;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.cfg.Configuration;

public class HQLCodeAssist implements IHQLCodeAssist {

    private Configuration configuration;
    private ConfigurationCompletion completion;

    private static final char[] charSeparators;

    static {
        charSeparators = new char[] { ',', '(', ')' };
        Arrays.sort( charSeparators );
    }

    public HQLCodeAssist(Configuration configuration) {
        this.configuration = configuration;
        completion = new ConfigurationCompletion( configuration );
    }

    public void codeComplete(String query, int position, IHQLCompletionRequestor collector) {

        int prefixStart = findNearestWhiteSpace( query, position );
        String prefix = query.substring( prefixStart, position );

        boolean showEntityNames;
        try {
            showEntityNames = new HQLAnalyzer().shouldShowEntityNames( query, position );

            if ( showEntityNames ) {
                if ( hasConfiguration() ) {
                    completion.getMatchingImports( prefix, position, collector );
                }
                else {
                    collector.completionFailure( "Configuration not available nor open" );
                }
            }
            else {
                List visible = new HQLAnalyzer().getVisibleEntityNames( query.toCharArray(), position );
                int dotIndex = prefix.lastIndexOf( "." );
                if ( dotIndex == -1 ) {
                    // It's a simple path, not a dot separated one (find aliases that matches)
                    for ( Iterator iter = visible.iterator(); iter.hasNext(); ) {
                        EntityNameReference qt = (EntityNameReference) iter.next();
                        String alias = qt.getAlias();
                        if ( alias.startsWith( prefix ) ) {
                            HQLCompletionProposal completionProposal = new HQLCompletionProposal(
                                    HQLCompletionProposal.ALIAS_REF,
                                    position
                            );
                            completionProposal.setCompletion( alias.substring( prefix.length() ) );
                            completionProposal.setReplaceStart( position );
                            completionProposal.setReplaceEnd( position + 0 );
                            completionProposal.setSimpleName( alias );
                            completionProposal.setShortEntityName( qt.getEntityName() );
                            if ( hasConfiguration() ) {
                                String importedName = (String) getConfiguration().getImports()
                                        .get( qt.getEntityName() );
                                completionProposal.setEntityName( importedName );
                            }
                            collector.accept( completionProposal );
                        }
                    }
                }
                else {
                    if ( hasConfiguration() ) {
                        String path = CompletionHelper.getCanonicalPath( visible, prefix.substring( 0, dotIndex ) );
                        String propertyPrefix = prefix.substring( dotIndex + 1 );
                        completion.getMatchingProperties( path, propertyPrefix, position, collector );
                    }
                    else {
                        collector.completionFailure( "Configuration not available nor open" );
                    }
                }

                completion.getMatchingFunctions( prefix, position, collector );
                completion.getMatchingKeywords( prefix, position, collector );


            }
        }
        catch ( SimpleLexerException sle ) {
            collector.completionFailure( "Syntax error: " + sle.getMessage() );
        }

    }

    private boolean hasConfiguration() {
        return configuration != null;
    }

    private Configuration getConfiguration() {
        return configuration;
    }

    public static int findNearestWhiteSpace(CharSequence doc, int start) {
        boolean loop = true;

        int offset = 0;

        int tmpOffset = start - 1;
        while ( loop && tmpOffset >= 0 ) {
            char c = doc.charAt( tmpOffset );
            if ( isWhitespace( c ) ) {
                loop = false;
            }
            else {
                tmpOffset--;
            }
        }
        offset = tmpOffset + 1;

        return offset;
    }

    private static boolean isWhitespace(char c) {
        return Arrays.binarySearch( charSeparators, c ) >= 0
                || Character.isWhitespace( c );
    }

}
