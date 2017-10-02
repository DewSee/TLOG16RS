package com.gytoth.tlog16rs.resources;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.gytoth.tlog16rs.TLOG16RSConfiguration;
import com.gytoth.tlog16rs.entities.TimeLogger;
import com.gytoth.tlog16rs.entities.WorkMonth;
import com.gytoth.tlog16rs.entities.WorkDay;
import com.gytoth.tlog16rs.entities.Task;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class TLOG16Database {

    private TLOG16Database() {
    }

    public static void initDatabase(TLOG16RSConfiguration configuration) {
        updateSchema(configuration);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();

        dataSourceConfig.setDriver(configuration.getDriver());
        dataSourceConfig.setUrl(configuration.getUrl());
        dataSourceConfig.setUsername(configuration.getUserName());
        dataSourceConfig.setPassword(configuration.getPassword());

        ServerConfig serverConfig = new ServerConfig();

        serverConfig.setName(configuration.getServerConfigName());
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TimeLogger.class);
        serverConfig.addClass(WorkMonth.class);
        serverConfig.addClass(WorkDay.class);
        serverConfig.addClass(Task.class);
        serverConfig.setDefaultServer(true);

        EbeanServer ebeanServer = EbeanServerFactory.create(serverConfig);

    }

    private static void updateSchema(TLOG16RSConfiguration configuration) {

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TLOG16Database.class.getName()).log(Level.CONFIG, null, ex);
        }

        java.sql.Connection conn;
        Liquibase liquibase;

        try {
            conn = DriverManager.getConnection(configuration.getUrl(), configuration.getUserName(), configuration.getPassword());
            liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(conn));
            liquibase.update(new Contexts());
        } catch (LiquibaseException | SQLException ex) {
            throw new IllegalStateException(ex);
        }

    }
}
