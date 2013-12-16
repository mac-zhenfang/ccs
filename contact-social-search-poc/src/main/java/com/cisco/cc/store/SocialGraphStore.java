/**
 * 
 */
package com.cisco.cc.store;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;

/**
 * @author zhefang
 * 
 */
public class SocialGraphStore extends Store {

	public static final String INDEX_NAME = "search";

	private static SocialGraphStore store = new SocialGraphStore();

	private TitanGraph graph;
	
	private AtomicBoolean isTitanInit = new AtomicBoolean();
	
	public static SocialGraphStore getStore() {
		return store;
	}
	public void prepareData(){
		Map<String, Person> personss = ActivityStreamStore.getStore()
				.getUsedPersons();

		List<Thing> thingss = ActivityStreamStore.getStore().getUsedThings();

		initTitan();

		initTitanGraphTypes(graph);

		List<Person> personsList = new ArrayList<Person>();
		for (Entry<String, Person> entry : personss.entrySet()) {
			personsList.add(entry.getValue());
		}
		List<Activity> activities = new ArrayList<Activity>();
		for (Entry<String, Activity> entry : ActivityStreamStore.getStore()
				.getActivityMap().entrySet()) {
			activities.add(entry.getValue());
		}

		initEdges(initPersonVertex(personsList, graph),
				initThingVertex(thingss, graph), activities);

		graph.commit();
	}
	
	public void init() {
		initTitan();
		isTitanInit.set(true);
	}
	//FIXME
	private void initTitan() {
		if(isTitanInit.get()){
			return;
		}
		// BaseConfiguration config = new BaseConfiguration();
		// Configuration storage = config
		// .subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
		// storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY,
		// "cassandra");
		// storage.setProperty(GraphDatabaseConfiguration.HOSTNAME_KEY,
		// "10.224.194.174");
		//
		// Configuration index = storage.subset(
		// GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
		// index.setProperty(GraphDatabaseConfiguration.INDEX_BACKEND_KEY,
		// "elasticsearch");
		// index.setProperty(GraphDatabaseConfiguration.HOSTNAME_KEY,
		// "10.224.194.171");
		// index.setProperty("local-mode", false);
		// index.setProperty("client-only", true);
		// // TitanGraph graph =
		// // TitanFactory.open("titan-cassandra-es.properties");
		String directory = "c:\\a";
		TitanGraph graph = null;
        BaseConfiguration config = new BaseConfiguration();
        Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
        // configuring local backend
        storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
        storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, directory);
        // configuring elastic search index
        Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
        index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
        index.setProperty("local-mode", true);
        index.setProperty("client-only", false);
        index.setProperty(STORAGE_DIRECTORY_KEY, directory + File.separator + "es");
        graph = TitanFactory.open(config);
		this.graph = graph;
		isTitanInit.set(true);
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

	private Map<String, Vertex> initPersonVertex(List<Person> personss,
			final TitanGraph graph) {
		Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
		for (Person persons : personss) {
			Vertex personsVert = graph.addVertex(null);
			ElementHelper.setProperties(personsVert, "vid", persons.getId(),
					"name",
					persons.getFirstName() + " " + persons.getLastName(),
					"type", "person");
			vertexMap.put(persons.getId(), personsVert);
		}
		return vertexMap;

	}

	private Map<String, Vertex> initThingVertex(List<Thing> things,
			final TitanGraph graph) {
		Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
		for (Thing thing : things) {
			Vertex personsVert = graph.addVertex(null);
			ElementHelper.setProperties(personsVert, "vid", thing.getId(),
					"name", thing.getThingDisplayName(), "type",
					thing.getThingObjectType());
			vertexMap.put(thing.getId(), personsVert);
		}
		return vertexMap;
	}

	private void initEdges(Map<String, Vertex> personsVertexMap,
			Map<String, Vertex> thingsVertexMap, List<Activity> activities) {
		for (Activity activity : activities) {
			String actId = activity.getActorId();
			String thingsId = activity.getThingId();
			Vertex personsVertex = personsVertexMap.get(actId);
			Vertex thingsVertex = thingsVertexMap.get(thingsId);
			personsVertex.addEdge(activity.getVerb(), thingsVertex);
		}
	}

	public List<Person> query(List<ActivityType> types,
			Person rootVertexPerson) {
		String rootVid = rootVertexPerson.getId();
		Iterator<Vertex> vertices = graph.getVertices("vid", rootVid)
				.iterator();
		List<Person> retPersons = new ArrayList<Person>();
		Map<String, Person> checkMap = new HashMap<String, Person>();
		while (vertices.hasNext()) {
			Vertex v = vertices.next();
			for (ActivityType type : types) {
				//FIXME 
				String targetName = type.getObjectName();
				String[] verbs = type.getVerbs();
				// Me (out)->(edges)->(in)target(in)<-(edge)<-(out)person
				Iterator<Edge> edgeIter = v.getEdges(Direction.OUT, verbs)
						.iterator();
				while (edgeIter.hasNext()) {
					Edge edge = edgeIter.next();
					// find the target
					Vertex target = edge.getVertex(Direction.IN);
					if (!target.getProperty("name").toString()
							.equalsIgnoreCase(targetName)) {
						// continue ?
						continue;
					}
					// find the edges that into this target
					Iterator<Edge> iter2 = target.getEdges(Direction.IN, verbs)
							.iterator();
					while (iter2.hasNext()) {
						// find the person for this edge
						Vertex personVertex = iter2.next().getVertex(
								Direction.OUT);
						String personVid = personVertex.getProperty("vid");
						if (!personVid.equals(rootVertexPerson.getId())) {
							Person person = PersonStore.getStore()
									.get(personVid).get(0);
							if (null != person && !checkMap.containsKey(person.getId())) {
								checkMap.put(person.getId(), person);
							}
						}
					}
				}
			}
		}
		retPersons.addAll(checkMap.values());
		return retPersons;
	}
}
