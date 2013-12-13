/**
 * 
 */
package com.cisco.cc.socia;

import org.junit.Test;

import com.cisco.cc.store.ActivityStreamStore;
import com.cisco.cc.store.ActivityTypeStore;
import com.cisco.cc.store.ContactStore;
import com.cisco.cc.store.SocialGraphStore;

/**
 * @author zhefang
 *
 */
public class TestStartUp {
	
	@Test
	public void testStartup (){
		
		ContactStore.getStore().init();
		
		ActivityTypeStore.getStore().init();
		
		ActivityStreamStore.getStore().init();
		
		SocialGraphStore.getStore().init();
	}
}
