define(['marionette',
        'app/EventNames',
        'models/OutgoingRequest',
        'models/OutgoingRequestCollection',
        'views/ConfirmView',
        'views/OutgoingRequestsTableView',
        'text!templates/OutgoingRequestsContentLayout.html'], 
        function(Marionette,
        		 EventNames,
        		 OutgoingRequest,
        		 OutgoingRequestCollection,
        		 ConfirmView,
        		 OutgoingRequestsTableView,
        		 theTemplate){
	
	var OutgoingRequestsContentLayout = Marionette.Layout.extend({
		
		template: _.template(theTemplate),

		initialize: function() {
			var self = this;
			PacBridgeApp.vent.on(EventNames.OutgoingRequestRow.remove, function(outgoingRequest) {
			    self.onDeleteOutgoingRequest(outgoingRequest);
			}, this);

		},
		
		regions: {
			contentRegion: '#contentId',
			dialog: '#dialog'
		},
		
		onClose: function() {
			PacBridgeApp.vent.off(EventNames.OutgoingRequestRow.remove, null, this);
		},
		
		onRender: function() {
		    var self = this;
			var orc = new OutgoingRequestCollection();
			orc.fetch({
				success: function() {
				    self.showOutgoingRequests(orc);
				}
			});
			this.showOutgoingRequests(orc);			
		},
		
		showOutgoingRequests: function(outgoingRequestsCollection) {
			this.contentRegion.show(new OutgoingRequestsTableView({
				collection: outgoingRequestsCollection
			}));
		},
		
	    onDeleteOutgoingRequest: function(outgoingRequest) {
	        var deleteOutgoingRequestDialog = new ConfirmView({
	        	model: outgoingRequest,
	        	onConfirm: function() {
	        		outgoingRequest.destroy();
	        	}
	        });
	        deleteOutgoingRequestDialog.on('close', function () {
	            this.dialog.close();
	        }, this);
	        this.dialog.show(deleteOutgoingRequestDialog);
	    },
		
	});
	
	return OutgoingRequestsContentLayout;
});