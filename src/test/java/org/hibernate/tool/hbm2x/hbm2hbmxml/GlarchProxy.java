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

package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.List;
import java.util.Map;

public interface GlarchProxy {

    public int getVersion();

    public int getDerivedVersion();

    public void setVersion(int version);

    public String getName();

    public void setName(String name);

    public GlarchProxy getNext();

    public void setNext(GlarchProxy next);

    public short getOrder();

    public void setOrder(short order);

    public List getStrings();

    public void setStrings(List strings);

    public Map getDynaBean();

    public void setDynaBean(Map bean);

    public Map getStringSets();

    public void setStringSets(Map stringSets);

    public List getFooComponents();

    public void setFooComponents(List fooComponents);

    public GlarchProxy[] getProxyArray();

    public void setProxyArray(GlarchProxy[] proxyArray);

    public Object getAny();

    public void setAny(Object any);
}







