package com.solar.smartformsnav;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "anp.cnf.nrviewer";
	
    /*‘› ±Œﬁ”√
     * private final Map<String, APPNav> appsnav = new HashMap<String, APPNav>();
    public synchronized Map<String, APPNav> getAppsnav() {
		return appsnav;
	}

	public synchronized Map<String, FormListNav> getFormlistnav() {
		return formlistnav;
	}
	private final Map<String, FormListNav> formlistnav = new HashMap<String, FormListNav>();
    */
	// The shared instance
	private static Activator plugin;
	public static synchronized Activator getPlugin() {
		return plugin;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
