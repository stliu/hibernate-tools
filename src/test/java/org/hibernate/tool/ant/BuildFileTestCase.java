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

package org.hibernate.tool.ant;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.Assert;

import org.hibernate.tool.BaseTestCase;

/**
 * A BuildFileTest is a TestCase which executes targets from an Ant buildfile
 * for testing.
 *
 * This class provides a number of utility methods for particular build file
 * tests which extend this class.
 *
 * Copied into hibernate to avoid dependency on ant test src directory.
 */
public abstract class BuildFileTestCase extends BaseTestCase {

    protected Project project;

    private StringBuffer logBuffer;
    private StringBuffer fullLogBuffer;
    private StringBuffer outBuffer;
    private StringBuffer errBuffer;
    private BuildException buildException;


    /**
     * run a target, expect for any build exception
     *
     * @param target target to run
     * @param cause information string to reader of report
     */
    protected void expectBuildException(String target, String cause) {
        expectSpecificBuildException( target, cause, null );
    }

    /**
     * Assert that only the given message has been logged with a
     * priority &gt;= INFO when running the given target.
     */
    protected void expectLog(String target, String log) {
        executeTarget( target );
        String realLog = getLog();
        Assert.assertEquals( log, realLog );
    }

    /**
     * Assert that the given substring is in the log messages
     */

    protected void assertLogContaining(String substring) {
        String realLog = getLog();
        Assert.assertTrue(
                "expecting log to contain \"" + substring + "\" log was \""
                        + realLog + "\"",
                realLog.indexOf( substring ) >= 0
        );
    }

    /**
     * Assert that the given message has been logged with a priority
     * &gt;= INFO when running the given target.
     */
    protected void expectLogContaining(String target, String log) {
        executeTarget( target );
        assertLogContaining( log );
    }

    /**
     * Gets the log the BuildFileTest object.
     * only valid if configureProject() has
     * been called.
     *
     * @return The log value
     *
     * @pre logBuffer!=null
     */
    protected String getLog() {
        return logBuffer.toString();
    }

    /**
     * Checks the log for Exceptions
     */
    protected boolean checkLogWithoutExceptions() {
        return !( getLog().contains( "Exception" ) );
    }

    /**
     * Assert that the given message has been logged with a priority
     * &gt;= DEBUG when running the given target.
     */
    protected void expectDebuglog(String target, String log) {
        executeTarget( target );
        String realLog = getFullLog();
        Assert.assertEquals( log, realLog );
    }

    /**
     * Gets the log the BuildFileTest object.
     * only valid if configureProject() has
     * been called.
     *
     * @return The log value
     *
     * @pre fullLogBuffer!=null
     */
    protected String getFullLog() {
        return fullLogBuffer.toString();
    }

    /**
     * execute the target, verify output matches expectations
     *
     * @param target target to execute
     * @param output output to look for
     */

    protected void expectOutput(String target, String output) {
        executeTarget( target );
        String realOutput = getOutput();
        Assert.assertEquals( output, realOutput.trim() );
    }

    /**
     * execute the target, verify output matches expectations
     * and that we got the named error at the end
     *
     * @param target target to execute
     * @param output output to look for
     * @param error Description of Parameter
     */

    protected void expectOutputAndError(String target, String output, String error) {
        executeTarget( target );
        String realOutput = getOutput();
        Assert.assertEquals( output, realOutput );
        String realError = getError();
        Assert.assertEquals( error, realError );
    }

    protected String getOutput() {
        return cleanBuffer( outBuffer );
    }

    protected String getError() {
        return cleanBuffer( errBuffer );
    }

    protected BuildException getBuildException() {
        return buildException;
    }

    private String cleanBuffer(StringBuffer buffer) {
        StringBuffer cleanedBuffer = new StringBuffer();
        boolean cr = false;
        for ( int i = 0; i < buffer.length(); i++ ) {
            char ch = buffer.charAt( i );
            if ( ch == '\r' ) {
                cr = true;
                continue;
            }

            if ( !cr ) {
                cleanedBuffer.append( ch );
            }
            else {
                cleanedBuffer.append( ch );
            }
        }
        return cleanedBuffer.toString();
    }

    /**
     * set up to run the named project
     *
     * @param filename name of project file to run
     */
    protected void configureProject(String filename) throws BuildException {
        configureProject( filename, Project.MSG_DEBUG );
    }

    /**
     * set up to run the named project
     *
     * @param filename name of project file to run
     */
    protected void configureProject(String filename, int logLevel)
            throws BuildException {
        logBuffer = new StringBuffer();
        fullLogBuffer = new StringBuffer();
        project = new Project();
        project.init();
        File antfile = createBaseFile( filename );
        project.setUserProperty( "ant.file", antfile.getAbsolutePath() );
        project.setProperty(
                "basedir",
                new File(".").getAbsolutePath()
        ); // overriding to make Maven and Ant test runs the same
        project.addBuildListener( new AntTestListener( logLevel ) );
//        new ProjectHelper().parse( project, createBaseFile( filename ) );
        ProjectHelper.configureProject( project, createBaseFile( filename ) );
    }

    /**
     * execute a target we have set up
     *
     * @param targetName target to run
     *
     * @pre configureProject has been called
     */
    protected void executeTarget(String targetName) {
        PrintStream sysOut = System.out;
        PrintStream sysErr = System.err;
        PrintStream out = null;
        PrintStream err = null;
        boolean error = false;
        try {
            sysOut.flush();
            sysErr.flush();
            outBuffer = new StringBuffer();
            out = new PrintStream( new AntOutputStream( outBuffer ) );
            System.setOut( out );
            errBuffer = new StringBuffer();
            err = new PrintStream( new AntOutputStream( errBuffer ) );
            System.setErr( err );
            logBuffer = new StringBuffer();
            fullLogBuffer = new StringBuffer();
            buildException = null;
            project.executeTarget( targetName );
        }
        catch ( BuildException be ) {
            error = true;
            throw be;
        }
        finally {
            if ( out != null ) {
                out.flush();
            }
            if ( err != null ) {
                err.flush();
            }
            System.setOut( sysOut );
            System.setErr( sysErr );
            if ( error ) {
                System.out.println( outBuffer );
                System.err.println( errBuffer );
                if ( buildException != null ) {
                    buildException.printStackTrace();
                }
            }
        }

    }

    /**
     * Get the project which has been configured for a test.
     *
     * @return the Project instance for this test.
     */
    protected Project getProject() {
        return project;
    }

    /**
     * get the directory of the project
     *
     * @return the base dir of the project
     */
    protected File getProjectDir() {
        return project.getBaseDir();
    }

    /**
     * run a target, wait for a build exception
     *
     * @param target target to run
     * @param cause information string to reader of report
     * @param msg the message value of the build exception we are waiting for
     * set to null for any build exception to be valid
     */
    protected void expectSpecificBuildException(String target, String cause, String msg) {
        try {
            executeTarget( target );
        }
        catch ( org.apache.tools.ant.BuildException ex ) {
            buildException = ex;
            if ( ( null != msg ) && ( !ex.getMessage().equals( msg ) ) ) {
                Assert.fail(
                        "Should throw BuildException because '" + cause
                                + "' with message '" + msg
                                + "' (actual message '" + ex.getMessage() + "' instead)"
                );
            }
            return;
        }
        Assert.fail( "Should throw BuildException because: " + cause );
    }

    /**
     * run a target, expect an exception string
     * containing the substring we look for (case sensitive match)
     *
     * @param target target to run
     * @param cause information string to reader of report
     * @param contains substring of the build exception to look for
     */
    protected void expectBuildExceptionContaining(String target, String cause, String contains) {
        try {
            executeTarget( target );
        }
        catch ( org.apache.tools.ant.BuildException ex ) {
            buildException = ex;
            if ( ( null != contains ) && ( ex.getMessage().indexOf( contains ) == -1 ) ) {
                Assert.fail(
                        "Should throw BuildException because '" + cause + "' with message containing '" + contains + "' (actual message '" + ex
                                .getMessage() + "' instead)"
                );
            }
            return;
        }
        Assert.fail( "Should throw BuildException because: " + cause );
    }


    /**
     * call a target, verify property is as expected
     *
     * @param target build file target
     * @param property property name
     * @param value expected value
     */

    protected void expectPropertySet(String target, String property, String value) {
        executeTarget( target );
        assertPropertyEquals( property, value );
    }

    /**
     * assert that a property equals a value; comparison is case sensitive.
     *
     * @param property property name
     * @param value expected value
     */
    protected void assertPropertyEquals(String property, String value) {
        String result = project.getProperty( property );
        Assert.assertEquals( "property " + property, value, result );
    }

    /**
     * assert that a property equals &quot;true&quot;
     *
     * @param property property name
     */
    protected void assertPropertySet(String property) {
        assertPropertyEquals( property, "true" );
    }

    /**
     * assert that a property is null
     *
     * @param property property name
     */
    protected void assertPropertyUnset(String property) {
        assertPropertyEquals( property, null );
    }


    /**
     * call a target, verify named property is "true".
     *
     * @param target build file target
     * @param property property name
     */
    protected void expectPropertySet(String target, String property) {
        expectPropertySet( target, property, "true" );
    }


    /**
     * call a target, verify property is null
     *
     * @param target build file target
     * @param property property name
     */
    protected void expectPropertyUnset(String target, String property) {
        expectPropertySet( target, property, null );
    }

    /**
     * Retrieve a resource from the caller classloader to avoid
     * assuming a vm working directory. The resource path must be
     * relative to the package name or absolute from the root path.
     *
     * @param resource the resource to retrieve its url.
     *
     * @throws AssertionFailureException if resource is not found.
     */
    protected URL getResource(String resource) {
        URL url = getClass().getResource( resource );
        Assert.assertNotNull( "Could not find resource :" + resource, url );
        return url;
    }

    /**
     * an output stream which saves stuff to our buffer.
     */
    private static class AntOutputStream extends java.io.OutputStream {
        private StringBuffer buffer;

        public AntOutputStream(StringBuffer buffer) {
            this.buffer = buffer;
        }

        public void write(int b) {
            buffer.append( (char) b );
        }
    }

    /**
     * our own personal build listener
     */
    private class AntTestListener implements BuildListener {
        private int logLevel;

        /**
         * Constructs a test listener which will ignore log events
         * above the given level
         */
        public AntTestListener(int logLevel) {
            this.logLevel = logLevel;
        }

        /**
         * Fired before any targets are started.
         */
        public void buildStarted(BuildEvent event) {
        }

        /**
         * Fired after the last target has finished. This event
         * will still be thrown if an error occured during the build.
         *
         * @see BuildEvent#getException()
         */
        public void buildFinished(BuildEvent event) {
        }

        /**
         * Fired when a target is started.
         *
         * @see BuildEvent#getTarget()
         */
        public void targetStarted(BuildEvent event) {
            //System.out.println("targetStarted " + event.getTarget().getName() );
        }

        /**
         * Fired when a target has finished. This event will
         * still be thrown if an error occured during the build.
         *
         * @see BuildEvent#getException()
         */
        public void targetFinished(BuildEvent event) {
            //System.out.println("targetFinished " + event.getTarget().getName() );
        }

        /**
         * Fired when a task is started.
         *
         * @see BuildEvent#getTask()
         */
        public void taskStarted(BuildEvent event) {
            //System.out.println("taskStarted " + event.getTask().getTaskName() );
        }

        /**
         * Fired when a task has finished. This event will still
         * be throw if an error occured during the build.
         *
         * @see BuildEvent#getException()
         */
        public void taskFinished(BuildEvent event) {
            //System.out.println("taskFinished " + event.getTask().getTaskName() );
        }

        /**
         * Fired whenever a message is logged.
         *
         * @see BuildEvent#getMessage()
         * @see BuildEvent#getPriority()
         */
        public void messageLogged(BuildEvent event) {
            if ( event.getPriority() > logLevel ) {
                // ignore event
                return;
            }

            if ( event.getPriority() == Project.MSG_INFO ||
                    event.getPriority() == Project.MSG_WARN ||
                    event.getPriority() == Project.MSG_ERR ) {
                logBuffer.append( event.getMessage() + "\n" );
            }
            fullLogBuffer.append( event.getMessage() + "\n" );

        }
    }


}
