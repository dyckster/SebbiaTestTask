package com.activeandroid.test;

import java.io.IOException;
import java.util.List;

import android.test.AndroidTestCase;

import com.activeandroid.sebbia.Configuration;
import com.activeandroid.sebbia.Model;
import com.activeandroid.sebbia.annotation.Table;

public class ConfigurationTest extends AndroidTestCase {

    public void testDefaultValue() throws IOException, ClassNotFoundException {
        Configuration conf = new Configuration.Builder(getContext()).create();
        assertNotNull(conf.getContext());
        assertEquals(1024, conf.getCacheSize());
        assertEquals("Application.db", conf.getDatabaseName());
        assertEquals(1, conf.getDatabaseVersion());
        assertNull(conf.getModelClasses());
        assertFalse(conf.isValid());
        assertNull(conf.getTypeSerializers());
        assertEquals(Configuration.SQL_PARSER_LEGACY, conf.getSqlParser());
    }

    public void testCreateConfigurationWithMockModel() {
        Configuration conf = new Configuration.Builder(getContext())
                .addModelClass(ConfigurationTestModel.class)
                .create();
        List<Class<? extends Model>> modelClasses = conf.getModelClasses();
        assertEquals(1, modelClasses.size());
        assertTrue(conf.isValid());
    }

    @Table(name = "ConfigurationTestModel")
    static class ConfigurationTestModel extends Model {
    }
}
