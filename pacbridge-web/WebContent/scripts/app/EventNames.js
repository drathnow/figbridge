define(function(){

	return {
	    App: {
	        notResponding: 'notResponding'
	    },

	    TopNav: {
	        logout: 'logout'
	    },
	    
	    SideBar: {
	        home: 'home',
	        connection: 'connection',
	        orequests: 'orequests',
	        cachedevs: 'cachedevs',
	        foo: 'foo'
	    },
	    
		NavBar: {
			showBridgeStatus: 'showBridgeStatus',
			showOutgoingRequests: 'showOutgoingRequests',
			showConnections: 'showConnections',
			showDevices: 'showDevices'
		},
		
		OutgoingRequestRow: {
			remove: 'remove',
			inspect: 'inspect'
		},
		
		ConnectionRow: {
			disconnect: 'disconnect',
			inspect: 'inspect'
		},
		
		DeviceRow: {
			deleteDev: 'delete'
		},
		
		DeviceTableView: {
			add: 'add'
		},
		
		DeviceEditor: {
			deviceUpdated: 'deviceUpdated'
		}
	};
});