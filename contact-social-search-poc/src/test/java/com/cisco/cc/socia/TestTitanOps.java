/**
 * 
 */
package com.cisco.cc.socia;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import com.cisco.css.store.Person;
import com.cisco.css.store.PersonStore;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanKey;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.rexster.client.RexsterClient;
import com.tinkerpop.rexster.client.RexsterClientFactory;

/**
 * @author zhefang
 * 
 */
public class TestTitanOps {

	public static final String INDEX_NAME = "search";

	/**
	 * 1. 1 client execute 1 transaction
	 * */
	@Test
	public void testTitanRexsterClient() throws Exception {
		RexsterClient client = RexsterClientFactory.open("10.224.194.174",
				8184, "mac11");
		// client.execute("g = rexster.getGraph('graph')");
		List<Map<String, Object>> result = client
				.execute("g.V('name','saturn').in('father').map");
		System.out.println(result);
	}

	@Test
	public void testTitanRemoveVertices() throws Exception {
		RexsterClient client = RexsterClientFactory.open("10.224.194.174",
				8184, "mac11");
		client.execute("g.clear()");
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

	@Test
	public void testGet2FromVertex() throws Exception {
		PersonStore.getStore().init();
		Person me = PersonStore.getStore().getMe();
		TitanGraph graph = initGraph();
		String id = "d6d2e702-ee8d-45e0-a005-080925bed274";
		// Me (out)->(edges)->(in)target(in)<-(edge)<-(out)person
		Iterator<Vertex> vertices = graph.getVertices("vid", id)
				.iterator();
		// Me
		Vertex v = vertices.next();

		System.out.println(v.getProperty("name") + " " + v.getProperty("vid"));
		// Me (out)->(edges)
		Iterator<Edge> iter = v.getEdges(Direction.OUT, "join").iterator();
		//
		while (iter.hasNext()) {

			Edge edge = iter.next();
			// Me (out)->(edges)->(in)target
			Vertex ver = edge.getVertex(Direction.IN);
			// Me (out)->(edges)->(in)target(in)<-(edge)
			Iterator<Edge> iter2 = ver.getEdges(Direction.IN, "join", "start")
					.iterator();
			while (iter2.hasNext()) {
				// Me (out)->(edges)->(in)target(in)<-(edge)<-(out)person
				Vertex vv = iter2.next().getVertex(Direction.OUT);
				if (!vv.getProperty("vid").equals(
						id)) {
					System.out.println(vv.getProperty("name") + " " + vv.getProperty("vid"));
				}

			}
			// Iterator<Vertex> iterV = ver.getVertices(Direction.IN,
			// "start").iterator();
			// while(iterV.hasNext()) {
			// Vertex ver2 = iterV.next();
			// System.out.println(ver2.getProperty("name") + " " +
			// ver2.getProperty("vid"));
			// }
		}
	}

	@Test
	public void testGetSomethingFromVertex() throws Exception {

		TitanGraph graph = initGraph();

		Iterator<Vertex> vertices = graph.getVertices("name", "Mac Fang")
				.iterator();

		while (vertices.hasNext()) {
			Vertex ver = vertices.next();
			System.out.println(ver.getProperty("name") + " "
					+ ver.getProperty("vid"));
			Iterator<Vertex> iter = ver.query().labels("start").vertices()
					.iterator();
			while (iter.hasNext()) {
				Vertex v = iter.next();
				System.out.println(v.getProperty("name") + " "
						+ v.getProperty("vid"));
			}

		}
	}

	@Test
	public void testGetAllVertices() throws Exception {
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
		Iterator<Vertex> vertices = graph.getVertices().iterator();
		while (vertices.hasNext()) {
			Vertex ver = vertices.next();
			System.out.println(ver.getProperty("name"));
		}
	}

	@Test
	public void testCreateTitanGraph() throws Exception {
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

		load(graph);
	}

	public static void load(final TitanGraph graph) {

		graph.makeKey("name").dataType(String.class).indexed(Vertex.class)
				.make();
		graph.makeKey("age").dataType(Integer.class)
				.indexed(INDEX_NAME, Vertex.class).make();
		graph.makeKey("type").dataType(String.class).make();

		final TitanKey time = graph.makeKey("time").dataType(Integer.class)
				.make();
		final TitanKey reason = graph.makeKey("reason").dataType(String.class)
				.indexed(INDEX_NAME, Edge.class).make();
		graph.makeKey("place").dataType(Geoshape.class)
				.indexed(INDEX_NAME, Edge.class).make();

		graph.makeLabel("father").manyToOne().make();
		graph.makeLabel("mother").manyToOne().make();
		graph.makeLabel("battled").sortKey(time).make();
		graph.makeLabel("lives").signature(reason).make();
		graph.makeLabel("pet").make();
		graph.makeLabel("brother").make();

		graph.commit();

		// vertices

		Vertex saturn = graph.addVertex(null);
		saturn.setProperty("name", "saturn");
		saturn.setProperty("age", 10000);
		saturn.setProperty("type", "titan");

		Vertex sky = graph.addVertex(null);
		ElementHelper.setProperties(sky, "name", "sky", "type", "location");

		Vertex sea = graph.addVertex(null);
		ElementHelper.setProperties(sea, "name", "sea", "type", "location");

		Vertex jupiter = graph.addVertex(null);
		ElementHelper.setProperties(jupiter, "name", "jupiter", "age", 5000,
				"type", "god");

		Vertex neptune = graph.addVertex(null);
		ElementHelper.setProperties(neptune, "name", "neptune", "age", 4500,
				"type", "god");

		Vertex hercules = graph.addVertex(null);
		ElementHelper.setProperties(hercules, "name", "hercules", "age", 30,
				"type", "demigod");

		Vertex alcmene = graph.addVertex(null);
		ElementHelper.setProperties(alcmene, "name", "alcmene", "age", 45,
				"type", "human");

		Vertex pluto = graph.addVertex(null);
		ElementHelper.setProperties(pluto, "name", "pluto", "age", 4000,
				"type", "god");

		Vertex nemean = graph.addVertex(null);
		ElementHelper
				.setProperties(nemean, "name", "nemean", "type", "monster");

		Vertex hydra = graph.addVertex(null);
		ElementHelper.setProperties(hydra, "name", "hydra", "type", "monster");

		Vertex cerberus = graph.addVertex(null);
		ElementHelper.setProperties(cerberus, "name", "cerberus", "type",
				"monster");

		Vertex tartarus = graph.addVertex(null);
		ElementHelper.setProperties(tartarus, "name", "tartarus", "type",
				"location");

		// edges

		jupiter.addEdge("father", saturn);
		jupiter.addEdge("lives", sky).setProperty("reason",
				"loves fresh breezes");
		jupiter.addEdge("brother", neptune);
		jupiter.addEdge("brother", pluto);

		neptune.addEdge("lives", sea).setProperty("reason", "loves waves");
		neptune.addEdge("brother", jupiter);
		neptune.addEdge("brother", pluto);

		hercules.addEdge("father", jupiter);
		hercules.addEdge("mother", alcmene);
		ElementHelper.setProperties(hercules.addEdge("battled", nemean),
				"time", 1, "place", Geoshape.point(38.1f, 23.7f));
		ElementHelper.setProperties(hercules.addEdge("battled", hydra), "time",
				2, "place", Geoshape.point(37.7f, 23.9f));
		ElementHelper.setProperties(hercules.addEdge("battled", cerberus),
				"time", 12, "place", Geoshape.point(39f, 22f));

		pluto.addEdge("brother", jupiter);
		pluto.addEdge("brother", neptune);
		pluto.addEdge("lives", tartarus).setProperty("reason",
				"no fear of death");
		pluto.addEdge("pet", cerberus);

		cerberus.addEdge("lives", tartarus);

		// commit the transaction to disk
		graph.commit();
	}

}
