define(['marionette',
        'app/EventNames',
        'models/Device',
        'models/DeviceCollection',
        'views/ConfirmView',
        'views/DevicesTableView',
        'views/DeviceEditor',
        'text!templates/DevicesContentLayout.html'], 
        function(Marionette,
        		 EventNames,
        		 Device,
        		 DeviceCollection,
        		 ConfirmView,
        		 DevicesTableView,
        		 DeviceEditor,
        		 theTemplate){
	
	var DevicesContentLayout = Marionette.Layout.extend({
		
		template: _.template(theTemplate),

		initialize: function() {
			PacBridgeApp.vent.on(EventNames.DeviceRow.deleteDev, function(connection) {
				this.onDeleteDevice(connection);
			}, this);
			
			PacBridgeApp.vent.on(EventNames.DeviceTableView.add, function(){
				this.onShowDeviceEditor();
			}, this);
			
			PacBridgeApp.vent.on(EventNames.DeviceEditor.deviceUpdated, function(){
				this.showDevices();
			}, this);			
		},

		regions: {
			contentRegion: '#contentId',
			filterRegion: '#filter',
			dialog: '#dialog'
		},

		events: {
            'keyup #filter': 'filterTable'
		},

		filterTable: function(e) {
            var rex = new RegExp($(e.currentTarget).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();
		},
		
	    onClose: function () {
	    	PacBridgeApp.vent.off(EventNames.DeviceRow.deleteDev, null, this);
	    	PacBridgeApp.vent.off(EventNames.DeviceEditor.add, null, this);
	    	PacBridgeApp.vent.off(EventNames.DeviceEditor.deviceUpdated, null, this);
	    },
		
		onRender: function() {
			var that = this;
			var orc = new DeviceCollection();
			orc.fetch({
				success: function() {
					that.showDevices(orc);
				}
			});
			this.showDevices(orc);			
		},

		onShowDeviceEditor: function() {
			this.contentRegion.show(new DeviceEditor());
		},
		
		showDevices: function(devicesCollection) {
			this.contentRegion.show(new DevicesTableView({
				collection: devicesCollection
			}));
		},
		
	    onDeleteDevice: function(device) {
	    	device.destroy();
	    },
		
	});
	
	return DevicesContentLayout;
});