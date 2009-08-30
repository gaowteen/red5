package org.red5.server.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.red5.server.Server;
import org.red5.server.api.plugin.IRed5Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates the plug-in environment and cleans up on shutdown.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class PluginLauncher implements ApplicationContextAware, InitializingBean, DisposableBean {

	// Initialize Logging
	protected static Logger log = LoggerFactory.getLogger(PluginLauncher.class);

	/**
	 * Spring application context
	 */
	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {

		ApplicationContext common = (ApplicationContext) applicationContext.getBean("red5.common");
		Server server = (Server) common.getBean("red5.server");

		//server should be up and running at this point so load any plug-ins now			

		//get the plugins dir
		File pluginsDir = new File(System.getProperty("red5.root"), "plugins");

		File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});

		for (File plugin : plugins) {
			JarFile jar = new JarFile(plugin, false);
			if (jar == null) {
				continue;
			}
			Manifest manifest = jar.getManifest();
			if (manifest == null) {
				continue;
			}
			Attributes attributes = manifest.getMainAttributes();
			if (attributes == null) {
				continue;
			}
			String pluginMainClass = attributes.getValue("Red5-Plugin-Main-Class");
			if (pluginMainClass == null) {
				continue;
			}
			// attempt to load the class; since it's in the lib directory this should work
			ClassLoader loader = common.getClassLoader();
			Class<?> pluginClass;
			try {
				pluginClass = Class.forName(pluginMainClass, true, loader);
			} catch (ClassNotFoundException e) {
				continue;
			}
			String pluginMainMethod = attributes.getValue("Red5-Plugin-Main-Method");
			if (pluginMainMethod == null) {
				continue;
			}
			Method method;
			try {
				method = pluginClass.getMethod(pluginMainMethod, (Class<?>[]) null);
			} catch (NoSuchMethodException e) {
				continue;
			}
			Object o = method.invoke(null, (Object[]) null);
			if (o != null && o instanceof IRed5Plugin) {
				IRed5Plugin red5Plugin = (IRed5Plugin) o;
				//set top-level context
				red5Plugin.setApplicationContext(applicationContext);
				//set server reference
				red5Plugin.setServer(server);
				//register the plug-in to make it available for lookups
				PluginRegistry.register(red5Plugin);
				//start the plugin
				red5Plugin.doStart();
			}

		}

	}

	@Override
	public void destroy() throws Exception {
		PluginRegistry.shutdown();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.debug("Setting application context");
		this.applicationContext = applicationContext;
	}

}
