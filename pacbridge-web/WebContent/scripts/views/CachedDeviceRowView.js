define(['marionette',
        'app/EventNames',
        'text!templates/CachedDeviceRow.html'], 
        function(Marionette,
        		EventNames,
        		theTemplate){
	
	var CachedDeviceRowView = Marionette.ItemView.extend({
		tagName: 'tr',
		template: _.template(theTemplate),
		
		initialize: function() {
		},
		
		ui: {
			deleteIt: 'button.delete'
		},
		
		events: {
			'click @ui.deleteIt': 'doDeleteDevice'
		},
		
		doDeleteDevice: function() {
			PacBridgeApp.vent.trigger(EventNames.DeviceRow.deleteDev, this.model);
		}
	});
	
	return CachedDeviceRowView;
});