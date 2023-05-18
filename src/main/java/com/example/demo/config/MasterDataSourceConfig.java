package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Follow https://medium.com/swlh/a-complete-guide-to-setting-up-multiple-datasources-in-spring-8296d4ff0935
 *
 * JDBC Connection Pool is a replacement to Apache Commons DBCP connection pool.
 * The tomcat jdbc pool is starvation proof. If the pool is empty and threads are waiting for connection, when
 * a connection is returned, the pool will awake the correct thread waiting.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.demo.repository",
        excludeFilters = @ComponentScan.Filter(ReadOnlyRepository.class),
        entityManagerFactoryRef = "masterEntityManagerFactory"
)
public class MasterDataSourceConfig {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private Boolean showSQL;
    private Boolean formatSQL;
    // Max active connections during any heavy load
    private Integer tomcatMaxActiveConnections;
    // Min active connections during low load
    private Integer tomcatMaxIdleConnections;
    // Time interval to recycle idle connection
    private Integer tomcatTimeBetweenEvictionRunsInMillis;
    // effective time of idle connection in connection pool
    private Integer tomcatMinTimeForEvictionEligibilityInMillis;
    /**
     * Abandoned connections are connection used by application to do some task but the application missed to
     * close them. Their resources are not returned to the system and will hog resources.
     * Setting removeAbandoned = true will close the connection after the time limit set for removeAbandonedTimeout.
     */
    private Boolean tomcatRemoveAbandonedConnections;

    @Autowired
    public MasterDataSourceConfig(@Value("${spring.datasource.url}") String url,
                                  @Value("${spring.datasource.username}") String username,
                                  @Value("${spring.datasource.password}") String password,
                                  @Value("${spring.datasource.driver-class-name}") String driverClassName,
                                  @Value("${spring.jpa.show-sql}") Boolean showSQL,
                                  @Value("${spring.jpa.properties.hibernate.format_sql}") Boolean formatSQL,
                                  @Value("${spring.datasource.tomcat.max-active}") Integer tomcatMaxActiveConnections,
                                  @Value("${spring.datasource.tomcat.max-idle}") Integer tomcatMaxIdleConnections,
                                  @Value("${spring.datasource.tomcat.time-between-eviction-runs-millis}") Integer tomcatTimeBetweenEvictionRunsInMillis,
                                  @Value("${spring.datasource.tomcat.min-evictable-idle-time-millis}") Integer tomcatMinTimeForEvictionEligibilityInMillis,
                                  @Value("${spring.datasource.tomcat.remove-abandoned}") Boolean tomcatRemoveAbandonedConnections
                                  ) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
        this.showSQL = showSQL;
        this.formatSQL = formatSQL;
        this.tomcatMaxActiveConnections = tomcatMaxActiveConnections;
        this.tomcatMaxIdleConnections = tomcatMaxIdleConnections;
        this.tomcatTimeBetweenEvictionRunsInMillis = tomcatTimeBetweenEvictionRunsInMillis;
        this.tomcatMinTimeForEvictionEligibilityInMillis = tomcatMinTimeForEvictionEligibilityInMillis;
        this.tomcatRemoveAbandonedConnections = tomcatRemoveAbandonedConnections;
    }

    /**
     * @Primary is needed when we need to register more than one bean of the same type.
     * In this case, we will have 2 beans that will inject the DataSource object into the Spring context, one for
     * the master db and one for the slave db.
     * By marking this bean with @Primary, we are telling spring to preferentially inject masterDataSource over the
     * other. This means that when @Autowiring is used, the DataSource instance created by masterDataSource will
     * have preference over all other instances.
     */
    @Bean
    @Primary
    public DataSource masterDataSource() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(url);
        poolProperties.setUsername(username);
        poolProperties.setPassword(password);
        poolProperties.setDriverClassName(driverClassName);
        poolProperties.setMaxActive(tomcatMaxActiveConnections);
        poolProperties.setMaxIdle(tomcatMaxIdleConnections);
        poolProperties.setTimeBetweenEvictionRunsMillis(tomcatTimeBetweenEvictionRunsInMillis);
        poolProperties.setMinEvictableIdleTimeMillis(tomcatMinTimeForEvictionEligibilityInMillis);
        poolProperties.setRemoveAbandoned(tomcatRemoveAbandonedConnections);

        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setPoolProperties(poolProperties);
        return dataSource;
    }

    /**
     * HibernateJpaVendorAdapter exposes Hibernate's persistence provider and Session as extended EntityManager
     * interface.
     * LocalContainerEntityManagerFactoryBean creates a JPA EntityManagerFactory according to the JPA's standard
     * bootstrap contract. This EntityManagerFactory can then be passed to our repository via dependency injection.
     */
    @Bean(name = "masterEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");
        properties.put("hibernate.show_sql", String.valueOf(showSQL));
        properties.put("hibernate.format_sql", String.valueOf(formatSQL));
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(masterDataSource());
        factoryBean.setPackagesToScan("com.example.demo.model");
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.getJpaPropertyMap().putAll(properties);
        return factoryBean;
    }
}
