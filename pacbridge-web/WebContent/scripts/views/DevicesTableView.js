define(['marionette', 
        'views/DeviceRowView',
        'app/EventNames',
        'text!templates/DevicesTable.html'], 
		function(Marionette, 
				DeviceRowView,
				EventNames,
				theTemplate){
	
	var ConnectionsTableView = Marionette.CompositeView.extend({
		tagName: 'table',
		className: 'table table-hover',
		template: _.template(theTemplate),
		itemView: DeviceRowView,
 		itemViewContainer: 'tbody',
 		
 		ui: {
 			addDevice: 'button.addDevice'
 		},
 		
 		events: {
 			'click @ui.addDevice': 'doAddDevice'
 		},
 		
 		doAddDevice: function() {
 			PacBridgeApp.vent.trigger(EventNames.DeviceTableView.add);
 		}
	});
	
	return ConnectionsTableView;
});