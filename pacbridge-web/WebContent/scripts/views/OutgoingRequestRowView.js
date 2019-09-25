define(['marionette',
        'app/EventNames',
        'text!templates/OutgoingRequestRow.html'], 
        function(Marionette,
        		EventNames,
        		theTemplate){
	
	var OutgoingRequestRowView = Marionette.ItemView.extend({
		tagName: 'tr',
		
		template: _.template(theTemplate),
		
		initialize: function() {
		},
		
		ui: {
			remove: 'button.remove'
		},
		
		events: {
			'click @ui.remove': 'doRemoveRequest'
		},
		
		doRemoveRequest: function() {
			PacBridgeApp.vent.trigger(EventNames.OutgoingRequestRow.remove, this.model);
		}
	});
	
	return OutgoingRequestRowView;
});