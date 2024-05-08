package com.projak.usersyncutil.util;

import javax.naming.directory.SearchControls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SearchQueries {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchQueries.class);

	public static SearchControls GetDistinguishedName() {
		logger.info("Inside GetDistinguishedName method of SearchQueries");
		SearchControls cons = new SearchControls();
		cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] attrIDs = { "distinguishedName" };
		cons.setReturningAttributes(attrIDs);
		return cons;
    }
	
}
