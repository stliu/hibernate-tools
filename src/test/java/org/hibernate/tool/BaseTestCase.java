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

package org.hibernate.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.ComparisonFailure;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Assert;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.service.internal.BasicServiceRegistryImpl;
import org.hibernate.testing.AfterClassOnce;
import org.hibernate.testing.BeforeClassOnce;
import org.hibernate.testing.cache.CachingRegionFactory;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.test.TestHelper;

public abstract class BaseTestCase extends BaseUnitTestCase {

    public static abstract class ExecuteContext {

        private final File sourceDir;
        private final File outputDir;
        private final List jars;
        private URLClassLoader ucl;

        public ExecuteContext(File sourceDir, File outputDir, List jars) {
            this.sourceDir = sourceDir;
            this.outputDir = outputDir;
            this.jars = jars;
        }

        public void run() throws Exception {

            TestHelper.compile(
                    sourceDir, outputDir, TestHelper.visitAllFiles( sourceDir, new ArrayList() ), "1.6",
                    TestHelper.buildClasspath( jars )
            );
            URL[] urls = TestHelper.buildClasspathURLS( jars, outputDir );

            Thread currentThread = null;
            ClassLoader contextClassLoader = null;

            try {
                currentThread = Thread.currentThread();
                contextClassLoader = currentThread.getContextClassLoader();
                ucl = new URLClassLoader( urls, contextClassLoader ) {

                    public Class loadClass(String name)
                            throws ClassNotFoundException {
                        // TODO Auto-generated method stub
                        return super.loadClass( name );
                    }


                };
                currentThread.setContextClassLoader( ucl );

                execute();

            }
            finally {
                currentThread.setContextClassLoader( contextClassLoader );
                TestHelper.deleteDir( outputDir );
            }
        }

        public URLClassLoader getUcl() {
            return ucl;
        }

        abstract protected void execute() throws Exception;

    }

    private File outputDir;
    private Configuration configuration;
    private BasicServiceRegistryImpl serviceRegistry;
    private Settings settings;
    private JdbcServices jdbcServices;
    private Dialect dialect;


    public BaseTestCase() {
        this.outputDir = new File( new File( "target", "tmp" ), getClass().getName() );
    }

    @BeforeClassOnce
    public void prepareTest() throws Exception {
//        cleanupOutputDir();
        if ( getOutputDir() != null ) {
            getOutputDir().mkdirs();
        }

//        assertNoTables();
    }

    @AfterClassOnce
    public void assertAllDataRemoved() {
        cleanupOutputDir();

        try {
            assertNoTables();
        }
        catch ( SQLException e ) {
            throw new RuntimeException( "can't clean test data", e );
        }
    }

    protected File createBaseFile(String relative) {
        String root = System.getProperty( "hibernatetool.test.supportdir", "." );
        return new File( root, relative );
    }

    protected void cleanupOutputDir() {
        if ( getOutputDir() != null ) {
            TestHelper.deleteDir( getOutputDir() );
        }
    }


    protected String findFirstString(String string, File file) {
        return TestHelper.findFirstString( string, file );
    }


    protected File getOutputDir() {
        return outputDir;
    }

    protected Configuration configuration() {
        if ( configuration == null ) {
            configuration = constructAndConfigureConfiguration();
            afterConstructAndConfigureConfiguration( configuration );
        }

        return configuration;
    }

    protected BasicServiceRegistryImpl serviceRegistry() {
        if ( serviceRegistry == null ) {
            serviceRegistry = buildServiceRegistry( configuration() );
        }
        return serviceRegistry;
    }

    protected Settings settings() {
        if ( settings == null ) {
            settings = configuration().buildSettings( serviceRegistry() );
        }
        return settings;
    }

    protected JdbcServices jdbcServices() {
        if ( jdbcServices == null ) {
            jdbcServices = serviceRegistry().getService( JdbcServices.class );
        }
        return jdbcServices;
    }

    protected Configuration constructAndConfigureConfiguration() {
        Configuration cfg = constructConfiguration();
        cfg.setProperty( AvailableSettings.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName() );
        cfg.setProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
        if ( createSchema() ) {
            cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
        }
        configure( cfg );
        return cfg;
    }

    protected Configuration constructConfiguration() {
        return new Configuration();
    }

    protected Dialect getDialect() {
        if ( dialect == null ) {
            dialect = jdbcServices().getDialect();
        }
        return dialect;
    }

    protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
        Properties properties = new Properties();
        properties.putAll( configuration.getProperties() );
        Environment.verifyProperties( properties );
        ConfigurationHelper.resolvePlaceHolders( properties );
        BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder(
                properties
        ).buildServiceRegistry();
        applyServices( serviceRegistry );
        return serviceRegistry;
    }

    private void afterConstructAndConfigureConfiguration(Configuration cfg) {
        addMappings( cfg );
        cfg.buildMappings();
        applyCacheSettings( cfg );
        afterConfigurationBuilt( cfg );
    }

    protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
    }

    protected void configure(Configuration configuration) {
    }

    protected boolean createSchema() {
        return true;
    }

    protected String getBaseForMappings() {
        return "org/hibernate/tool/";
    }

    protected void addMappings(Configuration configuration) {
        String[] mappings = getMappings();
        if ( mappings != null ) {
            for ( String mapping : mappings ) {
                configuration.addResource(
                        getBaseForMappings() + mapping,
                        getClass().getClassLoader()
                );
            }
        }
        Class<?>[] annotatedClasses = getAnnotatedClasses();
        if ( annotatedClasses != null ) {
            for ( Class<?> annotatedClass : annotatedClasses ) {
                configuration.addAnnotatedClass( annotatedClass );
            }
        }
        String[] annotatedPackages = getAnnotatedPackages();
        if ( annotatedPackages != null ) {
            for ( String annotatedPackage : annotatedPackages ) {
                configuration.addPackage( annotatedPackage );
            }
        }
        String[] xmlFiles = getXmlFiles();
        if ( xmlFiles != null ) {
            for ( String xmlFile : xmlFiles ) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
                configuration.addInputStream( is );
            }
        }
    }

    protected static final String[] NO_MAPPINGS = new String[0];

    protected String[] getMappings() {
        return NO_MAPPINGS;
    }

    protected static final Class<?>[] NO_CLASSES = new Class[0];

    protected Class<?>[] getAnnotatedClasses() {
        return NO_CLASSES;
    }

    protected String[] getAnnotatedPackages() {
        return NO_MAPPINGS;
    }

    protected String[] getXmlFiles() {
        // todo : rename to getOrmXmlFiles()
        return NO_MAPPINGS;
    }

    protected void addMappings(String[] files, Configuration cfg) {
        for ( int i = 0; i < files.length; i++ ) {
            if ( !files[i].startsWith( "net/" ) ) {
                files[i] = getBaseForMappings() + files[i];
            }
            cfg.addResource( files[i], this.getClass().getClassLoader() );
        }
    }

    protected void afterConfigurationBuilt(Configuration configuration) {
        afterConfigurationBuilt( configuration.createMappings(), getDialect() );
    }

    protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
    }


    protected void applyCacheSettings(Configuration configuration) {
        if ( getCacheConcurrencyStrategy() != null ) {
            Iterator itr = configuration.getClassMappings();
            while ( itr.hasNext() ) {
                PersistentClass clazz = (PersistentClass) itr.next();
                Iterator props = clazz.getPropertyClosureIterator();
                boolean hasLob = false;
                while ( props.hasNext() ) {
                    Property prop = (Property) props.next();
                    if ( prop.getValue().isSimpleValue() ) {
                        String type = ( (SimpleValue) prop.getValue() ).getTypeName();
                        if ( "blob".equals( type ) || "clob".equals( type ) ) {
                            hasLob = true;
                        }
                        if ( Blob.class.getName().equals( type ) || Clob.class.getName().equals( type ) ) {
                            hasLob = true;
                        }
                    }
                }
                if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
                    configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
                }
            }
            itr = configuration.getCollectionMappings();
            while ( itr.hasNext() ) {
                Collection coll = (Collection) itr.next();
                configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
            }
        }
    }

    protected boolean overrideCacheStrategy() {
        return true;
    }

    protected String getCacheConcurrencyStrategy() {
        return null;
    }

    /**
     * parse the url, fails if not valid xml. Does not validate against the DTD because they are remote
     */
    public Document assertValidXML(File url) {
        SAXReader reader = new SAXReader();
        reader.setValidation( false );
        Document document = null;
        try {
            document = reader.read( url );
        }
        catch ( DocumentException e ) {
            Assert.fail( "Could not parse " + url + ":" + e );
        }
        Assert.assertNotNull( document );
        return document;
    }

    protected void generateComparator() throws IOException {
        File file = new File( getOutputDir().getAbsolutePath() + "/comparator/NoopComparator.java" );
        file.getParentFile().mkdirs();

        FileWriter fileWriter = new FileWriter( file );
        PrintWriter pw = new PrintWriter( fileWriter );

        pw.println( "package comparator;" );

        pw.println( "import java.util.Comparator;" );

        pw.println(
                "public class NoopComparator implements Comparator {\n" +
                        "\n" +
                        "			public int compare(Object o1, Object o2) {\n" +
                        "				return 0;\n" +
                        "			}\n" +
                        "\n" +
                        "		}\n" +
                        ""
        );

        pw.flush();
        pw.close();
    }
    //~~~~~~~~~~~~~~~~ assertions

    public void assertNoTables() throws SQLException {
        JDBCMetaDataDialect dialect = new JDBCMetaDataDialect();
        dialect.configure(
                ReverseEngineeringRuntimeInfo.createInstance(
                        jdbcServices().getConnectionProvider(),
                        jdbcServices().getSqlExceptionHelper().getSqlExceptionConverter(),
                        new DefaultDatabaseCollector( dialect )
                )
        );
        Iterator tables = dialect.getTables(
                null,
                null,
                null
        );
        assertHasNext( 0, tables );
    }

    protected void assertHasNext(int expected, Iterator iterator) {
        assertHasNext( null, expected, iterator );
    }

    /**
     * @param i
     * @param iterator
     */
    protected void assertHasNext(String reason, int expected, Iterator iterator) {
        int actual = 0;
        Object last = null;
        while ( iterator.hasNext() && actual <= expected ) {
            last = iterator.next();
            actual++;
        }

        if ( actual < expected ) {
            throw new ComparisonFailure( reason == null ? "Expected were less" : reason, "" + expected, "" + actual );
        }

        if ( actual > expected ) {
            throw new ComparisonFailure(
                    ( reason == null ? "Expected were higher" : reason ) + ", Last: " + last,
                    "" + expected,
                    "" + actual
            );
        }
    }

    protected void assertFileAndExists(File file) {
        Assert.assertTrue( file + " does not exist", file.exists() );
        Assert.assertTrue( file + " not a file", file.isFile() );
        Assert.assertTrue( file + " does not have any contents", file.length() > 0 );
    }
}

