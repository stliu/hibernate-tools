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

//$Id$
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;

public class Glarch implements GlarchProxy, Lifecycle, Serializable {

    private int version;
    private GlarchProxy next;
    private short order;
    private List strings;
    private Map stringSets;
    private List fooComponents;
    private GlarchProxy[] proxyArray;
    private transient Map dynaBean;
    private String immutable;
    private int derivedVersion;
    private Object any;
    private int x;
    private String name;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public GlarchProxy getNext() {
        return next;
    }

    public void setNext(GlarchProxy next) {
        this.next = next;
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(short order) {
        this.order = order;
    }

    public List getStrings() {
        return strings;
    }

    public void setStrings(List strings) {
        this.strings = strings;
    }

    public Map getStringSets() {
        return stringSets;
    }

    public void setStringSets(Map stringSets) {
        this.stringSets = stringSets;
    }

    public List getFooComponents() {
        return fooComponents;
    }

    public void setFooComponents(List fooComponents) {
        this.fooComponents = fooComponents;
    }

    public GlarchProxy[] getProxyArray() {
        return proxyArray;
    }

    public void setProxyArray(GlarchProxy[] proxyArray) {
        this.proxyArray = proxyArray;
    }

    public boolean onDelete(Session s) throws CallbackException {
        return NO_VETO;
    }

    public void onLoad(Session s, Serializable id) {
        if ( !( ( (String) id ).length() == 32 ) ) {
            throw new RuntimeException( "id problem" );
        }
    }

    public boolean onSave(Session s) throws CallbackException {
        dynaBean = new HashMap();
        dynaBean.put( "foo", "foo" );
        dynaBean.put( "bar", new Integer( 66 ) );
        immutable = "never changes!";
        return NO_VETO;
    }

    public boolean onUpdate(Session s) throws CallbackException {
        return NO_VETO;
    }

    /*public Currency getCurrency() {
         return currency;
     }

     public void setCurrency(Currency currency) {
         this.currency = currency;
     }*/

    /**
     * Returns the dynaBean.
     *
     * @return DynaBean
     */
    public Map getDynaBean() {
        return dynaBean;
    }

    /**
     * Sets the dynaBean.
     *
     * @param dynaBean The dynaBean to set
     */
    public void setDynaBean(Map dynaBean) {
        this.dynaBean = dynaBean;
    }

    /**
     * Returns the immutable.
     *
     * @return String
     */
    public String getImmutable() {
        return immutable;
    }

    /**
     * Sets the immutable.
     *
     * @param immutable The immutable to set
     */
    public void setImmutable(String immutable) {
        this.immutable = immutable;
    }

    /**
     * Returns the derivedVersion.
     *
     * @return int
     */
    public int getDerivedVersion() {
        return derivedVersion;
    }

    /**
     * Sets the derivedVersion.
     *
     * @param derivedVersion The derivedVersion to set
     */
    public void setDerivedVersion(int derivedVersion) {
        this.derivedVersion = derivedVersion;
    }

    /**
     * Returns the any.
     *
     * @return Object
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the any.
     *
     * @param any The any to set
     */
    public void setAny(Object any) {
        this.any = any;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}







