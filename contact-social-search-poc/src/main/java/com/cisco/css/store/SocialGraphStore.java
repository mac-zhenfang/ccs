/**
 * 
 */
package com.cisco.css.store;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory
			.getLogger(SocialGraphStore.class);
	public static final String INDEX_NAME = "search";

	private static SocialGraphStore store = new SocialGraphStore();

	private TitanGraph graph;

	private AtomicBoolean isTitanInit = new AtomicBoolean();

	public static SocialGraphStore getStore() {
		return store;
	}

	public void prepareData() {
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

		activities.addAll(ActivityStreamStore.getStore().getActivityMap()
				.values());

		initEdges(initPersonVertex(personsList, graph),
				initThingVertex(thingss, graph), activities);

		graph.commit();
	}

	public void init() {
		initTitan();
		isTitanInit.set(true);
	}

	// FIXME
	private void initTitan() {
		if (isTitanInit.get()) {
			return;
		}
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
//		String directory = "c:\\a";
//		TitanGraph graph = null;
//		BaseConfiguration config = new BaseConfiguration();
//		Configuration storage = config
//				.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
//		// configuring local backend
//		storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY,
//				"local");
//		storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY,
//				directory);
//		// configuring elastic search index
//		Configuration index = storage.subset(
//				GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
//		index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
//		index.setProperty("local-mode", true);
//		index.setProperty("client-only", false);
//		index.setProperty(STORAGE_DIRECTORY_KEY, directory + File.separator
//				+ "es");
		this.graph = TitanFactory.open(config);
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

	private void addRelation(Map<String, Relation> map, Relation relation) {
		if (!map.containsKey(relation.getPerson().getId())) {
			map.put(relation.getPerson().getId(), relation);
		} else {
			Relation relationFroMap = map.get(relation.getPerson().getId());
			Iterator<String> keys = relation.getThings().keySet().iterator();
			while (keys.hasNext()) {
				relationFroMap.addThing(relation.getThings().get(keys.next()));
			}
		}
	}

	public List<Relation> queryAllRelations() {
		List<Relation> allRelations = new ArrayList<Relation>();
		// the key is the personId,
		Map<String, Relation> retRelations = new HashMap<String, Relation>();
		Iterator<Vertex> vertices = graph.query().has("type", "person")
				.vertices().iterator();
		while (vertices.hasNext()) {
			Vertex rootVertex = vertices.next();
			String personUuid = rootVertex.getProperty("vid");
			Relation rootRelation = new Relation();
			rootRelation.setPerson(PersonStore.getStore().getOne(personUuid));
			String[] labels = ActivityTypeStore.getStore().getVerbs()
					.toArray(new String[0]);
			// find the Thing
			Iterator<Vertex> thingIter = rootVertex.query().labels(labels)
					.vertices().iterator();
			while (thingIter.hasNext()) {
				Vertex thingVertex = thingIter.next();
				// put thing into relation
				String thingId = thingVertex.getProperty("vid");
				String name = thingVertex.getProperty("name");
				String type = thingVertex.getProperty("type");
				Thing thing = new Thing();
				thing.setId(thingId);
				thing.setThingDisplayName(name);
				thing.setThingObjectType(type);
				rootRelation.addThing(thing);
			}
			addRelation(retRelations, rootRelation);
		}
		allRelations.addAll(retRelations.values());
		return allRelations;
	}

	public List<Relation> queryRelation(String personUuid, String toId) {
		List<Relation> rtList = new ArrayList<Relation>();
		// the key is the personId,
		Map<String, Relation> retRelations = new HashMap<String, Relation>();
		// personUuid == rootVid
		Iterator<Vertex> vertices = graph.getVertices("vid", personUuid)
				.iterator();
		// should only have 1 since it is UUID
		while (vertices.hasNext()) {
			Vertex rootVertex = vertices.next();
			// create the main relation from Root person to Things
			Relation rootRelation = new Relation();
			rootRelation.setPerson(PersonStore.getStore().getOne(personUuid));
			String[] labels = ActivityTypeStore.getStore().getVerbs()
					.toArray(new String[0]);
			// find the Thing
			Iterator<Vertex> thingIter = rootVertex.query().labels(labels)
					.vertices().iterator();
			while (thingIter.hasNext()) {
				Vertex thingVertex = thingIter.next();
				// put thing into relation
				String thingId = thingVertex.getProperty("vid");
				String name = thingVertex.getProperty("name");
				String type = thingVertex.getProperty("type");
				Thing thing = new Thing();
				thing.setId(thingId);
				thing.setThingDisplayName(name);
				thing.setThingObjectType(type);
				rootRelation.addThing(thing);

				// Find any Edges In Direction.IN and find the Person
				Iterator<Vertex> personsIter = thingVertex.query()
						.direction(Direction.IN).labels(labels).vertices()
						.iterator();
				// iterate the person and bind to the above thing - unique
				while (personsIter.hasNext()) {

					Vertex otherPerson = personsIter.next();
					String otherPersonId = otherPerson.getProperty("vid");
					String otherName = otherPerson.getProperty("name");
					if (null != toId) {
						if (otherPersonId.endsWith(toId)) {
							Relation otherRelation = new Relation();
							// logger.info(otherName);
							// logger.info(otherPersonId);
							otherRelation.setPerson(PersonStore.getStore()
									.getOne(otherPersonId));
							otherRelation.addThing(thing);
							addRelation(retRelations, otherRelation);
							break;
						} else {
							continue;
						}
					} else {
						if (!otherPersonId.equals(personUuid)) {
							Relation otherRelation = new Relation();
							// logger.info(otherName);
							// logger.info(otherPersonId);
							otherRelation.setPerson(PersonStore.getStore()
									.getOne(otherPersonId));
							otherRelation.addThing(thing);
							addRelation(retRelations, otherRelation);
						}
					}
				}
			}
			addRelation(retRelations, rootRelation);
		}

		rtList.addAll(retRelations.values());
		return rtList;
	}

	public List<Person> query(List<ActivityType> types,
			Person rootVertexPerson, Set<String> personIds) {
		long start = System.currentTimeMillis();
		String rootVid = rootVertexPerson.getId();
		Iterator<Vertex> vertices = graph.getVertices("vid", rootVid)
				.iterator();
		List<Person> retPersons = new ArrayList<Person>();
		Map<String, Person> checkMap = new HashMap<String, Person>();
		while (vertices.hasNext()) {
			Vertex v = vertices.next();
			for (ActivityType type : types) {
				// FIXME
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
							List<Person> persons = PersonStore.getStore().get(
									personVid);
							if (!persons.isEmpty()
									&& null != persons.get(0)
									&& !checkMap.containsKey(persons.get(0)
											.getId())
									&& personIds.contains(persons.get(0)
											.getId())) {
								checkMap.put(persons.get(0).getId(),
										persons.get(0));
							}
						}
					}
				}
			}
		}
		retPersons.addAll(checkMap.values());
		logger.info(" cost: " + (System.currentTimeMillis() - start));
		return retPersons;
	}
}
