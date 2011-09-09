package org.hibernate.tool.hbmlint;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.service.ServiceRegistry;

public abstract class Detector {

	private Configuration cfg;
	private ServiceRegistry serviceRegistry;
    private Settings settings;

	public void initialize(Configuration configuration, ServiceRegistry serviceRegistry, Settings settings) {
		this.cfg = configuration;
        this.serviceRegistry = serviceRegistry;
        this.settings = settings;
	}

    public Settings getSettings() {
        return settings;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    protected Configuration getConfiguration() {
		return cfg;
	}

	public void visit(Configuration configuration, IssueCollector collector) {
		
	}
	
	abstract public String getName();
}
