/**
 * 
 */
package com.cisco.css.service;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.css.store.Person;
import com.cisco.css.store.Relation;

/**
 * @author zhefang
 *
 */


@Controller
public class SocialSearchController {
	@Resource(name = "socialSearchService")
	private SocialSearchService socialSearchService;
	
	private static ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/query/{queryStr}", method = RequestMethod.GET)
	public @ResponseBody
	List<Person> query(@PathVariable String queryStr) throws IOException {
		return socialSearchService.query(queryStr);
	}
	
	@RequestMapping(value = "/relations/{id}", method = RequestMethod.GET)
	public @ResponseBody
	List<Relation> getRelatedContact(@PathVariable String id) throws IOException {
		return socialSearchService.getRelations(id);
	}
}
