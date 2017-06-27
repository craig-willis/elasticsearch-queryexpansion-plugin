package org.nationaldataservice.elasticsearch.queryexpansion;

import static org.elasticsearch.rest.RestRequest.Method.GET;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

public class RestQueryExpansionAction extends BaseRestHandler {
	private final Logger logger = ESLoggerFactory.getLogger(RestQueryExpansionAction.class);
	
    @Inject   
	public RestQueryExpansionAction (Settings settings, RestController controller) {
		super(settings);
		this.logger.info("Plugin loaded!");
		controller.registerHandler(GET, "/_hello", this);
		controller.registerHandler(GET, "/_hello/{name}", this);
	}
    
    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) throws IOException {
        QueryExpansionRequest request = new QueryExpansionRequest();
		this.logger.info("Preparing request!");
		
		String name = restRequest.param("name");
	    
        if (name != null) {
            request.setName(name);
        } else if (restRequest.hasContent()){
            request.setRestContent(restRequest.content());
        }
        
        return channel -> client.execute(QueryExpansionAction.INSTANCE, request, new ActionListener<QueryExpansionResponse>() {
        	private final Logger logger = ESLoggerFactory.getLogger(QueryExpansionResponse.class);
        	
			@Override
			public void onResponse(QueryExpansionResponse response) {
				this.logger.info("Sending response: " + response.getMessage());
		        XContentBuilder builder;
				try {
					builder = channel.newBuilder();
			        builder.startObject();
			        response.toXContent(builder, restRequest);
			        builder.endObject();
			        channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
				} catch (IOException innerException) {
					this.logger.error("I/O error:", innerException);
				}
			}

			@Override
			public void onFailure(Exception e) {
				this.logger.error("Sending error:", e);
		        try {
					channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, channel.newBuilder()));
				} catch (IOException innerException) {
					this.logger.error("I/O error:", innerException);
				}
			}
        });
    }
}