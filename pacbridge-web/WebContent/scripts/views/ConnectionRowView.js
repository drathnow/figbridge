define(['marionette',
        'app/EventNames',
        'text!templates/ConnectionRow.html'], 
        function(Marionette,
        		EventNames,
        		theTemplate){
	
	var ConnectionRowView = Marionette.ItemView.extend({
		tagName: 'tr',
		
		template: _.template(theTemplate),
		
		initialize: function() {
		},
		
		ui: {
			disconnect: 'button.disconnect'
		},
		
		events: {
			'click @ui.disconnect': 'doDisconnectRequest'
		},
		
		doDisconnectRequest: function() {
			PacBridgeApp.vent.trigger(EventNames.ConnectionRow.disconnect, this.model);
		}
	});
	
	return ConnectionRowView;
});