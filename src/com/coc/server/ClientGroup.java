package com.coc.server;

import java.util.Map;

public class ClientGroup {
private Map<String ,GroupDetails> groups;

public Map<String, GroupDetails> getGroups() {
	return groups;
}

public void setGroups(Map<String, GroupDetails> groups) {
	this.groups = groups;
}


}
