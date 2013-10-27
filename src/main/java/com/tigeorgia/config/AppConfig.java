package com.tigeorgia.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.transform.Source;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;

import com.tigeorgia.webservice.MagtiClient;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@ComponentScan("com.tigeorgia")
@ImportResource("classpath:spring-security.xml")
public class AppConfig extends WebMvcConfigurerAdapter{
	
	@Resource
	private Environment env;

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
	

}
