define(['marionette',
        'app/EventNames',
        'models/Connection',
        'models/ConnectionCollection',
        'views/ConfirmView',
        'views/ConnectionsTableView',
        'text!templates/ConnectionsContentLayout.html'], 
        function(Marionette,
        		 EventNames,
        		 Connection,
        		 ConnectionCollection,
        		 ConfirmView,
        		 ConnectionsTableView,
        		 theTemplate){
	
	var ConnectionsContentLayout = Marionette.Layout.extend({
		
		template: _.template(theTemplate),

		initialize: function() {
			that = this;
			PacBridgeApp.vent.on(EventNames.ConnectionRow.disconnect, function(connection) {
				this.onDeleteConnection(connection);
			}, this);

		},
		
		regions: {
			contentRegion: '#contentId',
			dialog: '#dialog'
		},

		onClose: function () {
	    	PacBridgeApp.vent.off(EventNames.ConnectionRow.disconnect, null, this);
	    },
		
		onRender: function() {
			var orc = new ConnectionCollection();
			orc.fetch({
				success: function() {
					that.showConnections(orc);
				}
			});
			that.showConnections(orc);			
		},
		
		showConnections: function(connectionsCollection) {
			this.contentRegion.show(new ConnectionsTableView({
				collection: connectionsCollection
			}));
		},
		
	    onDeleteConnection: function(connection) {
	    	connection.destroy();
	    },
		
	});
	
	return ConnectionsContentLayout;
});