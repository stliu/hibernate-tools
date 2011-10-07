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

package org.hibernate.tool.hbmlint;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.SchemaByMetaDataDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;

public class HbmLint implements IssueCollector {


    final Detector[] detectors;

    public HbmLint(Detector[] detectors) {
        this.detectors = detectors;
    }

    List<Issue> results = new ArrayList<Issue>();

    public void analyze(Configuration cfg, ServiceRegistry serviceRegistry) {
        Settings settings = cfg.buildSettings( serviceRegistry );
        for(Detector detector: detectors){
            detector.initialize( cfg, serviceRegistry, settings );
            detector.visit( cfg, this );
        }
    }

    /* (non-Javadoc)
      * @see org.hibernate.tool.hbmlint.IssueCollector#reportProblem(org.hibernate.tool.hbmlint.Issue)
      */
    public void reportIssue(Issue analyze) {
        results.add( analyze );
    }

    public List<Issue> getResults() {
        return results;
    }

    public static HbmLint createInstance() {
        return new HbmLint(
                new Detector[] {
                        new BadCachingDetector(),
                        new InstrumentationDetector(),
                        new ShadowedIdentifierDetector(),
                        new SchemaByMetaDataDetector()
                }
        );
    }
}
