package com.tigeorgia.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Application initializer, referencing the application configuration file.
 * @author Etienne Baque
 *
 */
public class AppInitializer implements WebApplicationInitializer {

	public void onStartup(ServletContext container) throws ServletException {
		// Create the 'root' Spring application context
	      AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
	      rootContext.register(AppConfig.class);

	      // Manage the lifecycle of the root application context
	      container.addListener(new ContextLoaderListener(rootContext));
	      rootContext.setServletContext(container);

	      // Register and map the dispatcher servlet
	      ServletRegistration.Dynamic dispatcher =
	        container.addServlet("dispatcher", new DispatcherServlet(rootContext));
	      dispatcher.setLoadOnStartup(1);
	      dispatcher.addMapping("/");
		
	}

}
