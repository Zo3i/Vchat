package com.jo;

import com.jo.controller.interceptor.MyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/META-INF/resources/")
				.addResourceLocations("file:E:/WeixinApp/userFile/");
	}

	@Bean
	public MyInterceptor myInterceptor() {
		return new MyInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(myInterceptor()).addPathPatterns("/user/**");
		super.addInterceptors(registry);
	}
}
