/**
 * 
 */
package com.cisco.cc.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;

/**
 * @author zhefang
 * 
 */
public class SocialGraphStore implements IStore {

	public static final String INDEX_NAME = "search";

	private static SocialGraphStore store = new SocialGraphStore();

	public static SocialGraphStore getStore() {
		return store;
	}

	public void init() {

		Map<String, Contact> contacts = ActivityStreamStore.getStore()
				.getUsedContacts();

		List<Target> targets = ActivityStreamStore.getStore().getUsedTargets();

		TitanGraph graph = initTitan();

		initTitanGraphTypes(graph);

		List<Contact> contactList = new ArrayList<Contact>();
		for (Entry<String, Contact> entry : contacts.entrySet()) {
			contactList.add(entry.getValue());
		}
		List<Activity> activities = new ArrayList<Activity>();
		for (Entry<String, Activity> entry : ActivityStreamStore.getStore()
				.getActivityMap().entrySet()) {
			activities.add(entry.getValue());
		}

		initEdges(initContactVertex(contactList, graph),
				initTargetVertex(targets, graph), activities);

		graph.commit();
	}

	private TitanGraph initTitan() {
		BaseConfiguration config = new BaseConfiguration();
		Configuration storage = config
				.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY,
				"cassandra");
		storage.setProperty(GraphDatabaseConfiguration.HOSTNAME_KEY,
				"10.224.194.174");

		Configuration index = storage.subset(
				GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
		index.setProperty(GraphDatabaseConfiguration.INDEX_BACKEND_KEY,
				"elasticsearch");
		index.setProperty(GraphDatabaseConfiguration.HOSTNAME_KEY,
				"10.224.194.171");
		index.setProperty("local-mode", false);
		index.setProperty("client-only", true);
		// TitanGraph graph =
		// TitanFactory.open("titan-cassandra-es.properties");
		TitanGraph graph = TitanFactory.open(config);
		return graph;
	}

	private void initTitanGraphTypes(final TitanGraph graph) {
		// Mac Fang
		graph.makeKey("vid").dataType(String.class).indexed(Vertex.class)
				.unique().make();
		graph.makeKey("name").dataType(String.class).indexed(Vertex.class)
				.make();
		// person,
		graph.makeKey("type").dataType(String.class).make();
		Set<String> verbs = ActivityTypeStore.getStore().getVerbs();
		for (String verb : verbs) {
			graph.makeLabel(verb).make();
		}

		graph.commit();
	}

	private Map<String, Vertex> initContactVertex(List<Contact> contacts,
			final TitanGraph graph) {
		Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
		for (Contact contact : contacts) {
			Vertex contactVert = graph.addVertex(null);
			ElementHelper.setProperties(contactVert, "vid", contact.getId(),
					"name", contact.getFirstName(), "type", "person");
			vertexMap.put(contact.getId(), contactVert);
		}
		return vertexMap;

	}

	private Map<String, Vertex> initTargetVertex(List<Target> targets,
			final TitanGraph graph) {
		Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
		for (Target target : targets) {
			Vertex contactVert = graph.addVertex(null);
			ElementHelper.setProperties(contactVert, "vid", target.getId(),
					"name", target.getTargetDisplayName(), "type",
					target.getTargetObjectType());
			vertexMap.put(target.getId(), contactVert);
		}
		return vertexMap;
	}

	private void initEdges(Map<String, Vertex> contactVertexMap,
			Map<String, Vertex> targetVertexMap, List<Activity> activities) {
		for (Activity activity : activities) {
			String actId = activity.getActorId();
			String targetId = activity.getTargetId();
			Vertex contactVertex = contactVertexMap.get(actId);
			Vertex targetVertex = targetVertexMap.get(targetId);
			contactVertex.addEdge(activity.getVerb(), targetVertex);
		}
	}
}
