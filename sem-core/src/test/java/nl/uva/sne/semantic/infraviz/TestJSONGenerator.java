package nl.uva.sne.semantic.infraviz;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSON;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import junit.framework.TestCase;

public class TestJSONGenerator extends TestCase {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestJSONGenerator.class);
	static {
		BasicConfigurator.configure();
	}
	
	@Test
	public void testJSON() {
		List<IVConcept> nodes = new ArrayList<IVConcept>();
		List<IVConcept> links = new ArrayList<IVConcept>();
		
		IVNode node1 = new IVNode("node1");
		IVNode node2 = new IVNode("node2");
		IVNode node3 = new IVNode("node3");
		IVNode node4 = new IVNode("node4");
		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		
		IVLink link1 = new IVLink("link1", "node1", "node2");
		IVLink link2 = new IVLink("link2", "node2", "node3");
		IVLink link3 = new IVLink("link3", "node3", "node3");
		links.add(link1);
		links.add(link2);
		links.add(link3);
		
		JSON result = JSONGenerator.createJSON("links", links);
		log.info("\n"+result.toString(2));
	}
}
