package org.nationaldataservice.elasticsearch.queryexpansion.test.integration;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;

public class HelloIT extends AbstractIntegTestCase {
	@Test
    public void testCase() throws Exception {
        Response response = client.performRequest("GET", "/_cat/plugins");
        System.out.println(response);
        
        HttpEntity entity = response.getEntity();
        System.out.println(entity);
    }
}