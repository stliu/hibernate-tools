package org.hibernate.tool.hbmlint;

import net.sf.cglib.transform.impl.InterceptFieldCallback;
import net.sf.cglib.transform.impl.InterceptFieldEnabled;

import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;


public class FakeNonLazy implements InterceptFieldEnabled, FieldHandled {

	long id;
	
	public FakeNonLazy(long id) {
		this.id = id;
	}

    @Override
    public FieldHandler getFieldHandler() {
        return null;
    }

    @Override
    public void setInterceptFieldCallback(InterceptFieldCallback callback) {
    }

    @Override
    public InterceptFieldCallback getInterceptFieldCallback() {
        return null;
    }

    @Override
    public void setFieldHandler(FieldHandler handler) {
    }
}

