package com.tigeorgia.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.transform.Source;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.tigeorgia.validator.UploadedFileValidator;
import com.tigeorgia.webservice.MagtiClient;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@ComponentScan("com.tigeorgia")
@ImportResource("classpath:spring-security.xml")
public class AppConfig extends WebMvcConfigurerAdapter{
	
	private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
	private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
	private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String PROPERTY_NAME_HIBERNATE_POOL_SIZE = "connection.pool_size";
	private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
	
	@Resource
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
		dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
		dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
		dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));

		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
		sessionFactoryBean.setHibernateProperties(hibProperties());
		return sessionFactoryBean;
	}

	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
		properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
		properties.put(PROPERTY_NAME_HIBERNATE_POOL_SIZE, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_POOL_SIZE));
		
		return properties;	
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}

	@Bean
	public UrlBasedViewResolver setupViewResolver() {
		UrlBasedViewResolver resolver = new UrlBasedViewResolver();
		resolver.setPrefix("/WEB-INF/pages/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		return resolver;
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(31556926);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
    }
	
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
	
	@Bean
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new SourceHttpMessageConverter<Source>());
		converters.add(new StringHttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		return restTemplate;
		
	}
	
	@Bean
	public Jaxp13XPathTemplate xpathTemplate(){
		return new Jaxp13XPathTemplate();
	}
	
	@Bean
	public MagtiClient magtiClient(){
		MagtiClient magtiClient = new MagtiClient();
		
		String params = "username={username}&password={password}&client_id={client_id}&service_id={service_id}&to={to}&text={text}";
		magtiClient.setMagtiWebserviceEndpoint(env.getRequiredProperty("magti.webservice.endpoint") + params);
		
		Map<String,String> variables = new HashMap<String,String>();
		variables.put("username", env.getRequiredProperty("magti.username"));
		variables.put("password", env.getRequiredProperty("magti.password"));
		variables.put("client_id", env.getRequiredProperty("magti.clientid"));
		variables.put("service_id", env.getRequiredProperty("magti.serviceid"));
		
		magtiClient.setWsVariables(variables);
		
		return magtiClient;
	}
	
	@Bean
	public UploadedFileValidator fileValidator(){
		return new UploadedFileValidator(); 
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver(){
		return new CommonsMultipartResolver();
	}
	
	@Bean(name="draftlawHeadings")
	@Scope(value = BeanDefinition.SCOPE_SINGLETON)
	public Map<String,String> draftlawHeadings(){
		//InputStream inputStream;
		BufferedReader br = null;
		Map<String, String> results = new HashMap<String, String>();
		try {
			ClassPathResource resource = new ClassPathResource("draftlawHeadings.csv");
			InputStream inputStream = resource.getInputStream();
			//inputStream = new FileInputStream();
			if (inputStream != null){
				br = new BufferedReader(new InputStreamReader(inputStream));
				while (br.ready()) {
					String line = br.readLine();
					if (line != null && !line.isEmpty()){
						String[] splitLine = line.split(",");
						results.put(splitLine[0], splitLine[1]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
		
	}
	
	@Bean(name="spreadsheet")
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SpreadsheetEntry spreadsheet(){
		return new SpreadsheetEntry();
	}
	
	@Bean(name="service")
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SpreadsheetService service(){
		return new SpreadsheetService("tig-draftlaw-spreadsheet");
	}

}
