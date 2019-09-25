define(['marionette', 
        'app/EventNames', 
        'text!templates/NavBarView.html'], 
        function(Marionette, 
        		EventNames, 
        		theTemplate){
	
	var NavBarView = Marionette.ItemView.extend({
		template: _.template(theTemplate),
		
		ui: {
			'statusButt': '#statusId',
			'connectionsButt': '#conId',
			'outgoingRequestsButt': '#orId',
			'devicesButt': '#devId'
		},
		
		triggers: {
			'click @ui.statusButt': EventNames.NavBar.showBridgeStatus,
			'click @ui.connectionsButt' : EventNames.NavBar.showConnections,
			'click @ui.outgoingRequestsButt': EventNames.NavBar.showOutgoingRequests,
			'click @ui.devicesButt': EventNames.NavBar.showDevices
		}
	});
	
	return NavBarView; 
});
