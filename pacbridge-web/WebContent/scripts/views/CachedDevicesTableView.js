define(['marionette', 
        'views/CachedDeviceRowView',
        'app/EventNames',
        'text!templates/CachedDevicesTable.html'], 
		function(Marionette, 
		        CachedDeviceRowView,
				EventNames,
				theTemplate){
	
	var CachedDevicesTableView = Marionette.CompositeView.extend({
		template: _.template(theTemplate),
		itemView: CachedDeviceRowView,
 		itemViewContainer: 'tbody',
 		
        
        onShow: function() {
            $('#cd-table').DataTable({
                responsive: true,
                aoColumnDefs : [ {
                    bSortable : false,
                    aTargets : [ 1 ]
                } ],

            });
        },

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
	
	return CachedDevicesTableView;
});