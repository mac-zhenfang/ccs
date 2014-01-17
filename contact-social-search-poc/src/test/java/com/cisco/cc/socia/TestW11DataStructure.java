package com.cisco.cc.socia;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;

/**
 * SQL: select confuuid, concat_ws(',', map_keys(UNION_MAP(MAP(concat(case when(extrainfo = 'start') then concat("host", "-", useruuid) else useruuid end, ':', orgid, ':', email), '')))) as users from wbx11_conferenceserver where ( dt > '2012-01-01' and dt < '2012-12-30' ) and confuuid!='' and useruuid!='' and Email != '' and ( extrainfo = 'start' or extrainfo = 'join') and event = 'LaunchConference'  and (! siteurl rlike 'w11mcttest') group by confuuid 
 * */
public class TestW11DataStructure {
	
	private void readW11Data() throws Exception {
		
	}
	private TitanGraph initGraph() {
		String directory = "c:\\a";
		BaseConfiguration config = new BaseConfiguration();
		Configuration storage = config
				.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
		// configuring local backend
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY,
				"local");
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY,
				directory);
		// configuring elastic search index
		Configuration index = storage.subset(
				GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
		index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
		index.setProperty("local-mode", true);
		index.setProperty("client-only", false);
		index.setProperty(STORAGE_DIRECTORY_KEY, directory + File.separator
				+ "es");
		// TitanGraph graph =
		// TitanFactory.open("titan-cassandra-es.properties");
		TitanGraph graph = TitanFactory.open(config);

		return graph;
	}
	
	private void 
}
