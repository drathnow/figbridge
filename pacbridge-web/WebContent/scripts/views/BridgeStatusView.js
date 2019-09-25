define(['marionette', 'text!templates/BridgeStatusView.html'], function(Marionette, theTemplate) {
	
	var BridgeStatusView = Marionette.ItemView.extend({
		template: _.template(theTemplate),
		
		ui: {
			
		},
		
		events: {
			'click @ui.refresh': 'doRefresh'
		},
		
		doRefresh: function() {
			var that = this;
			this.model.fetch({
				success: function() {
					that.render();
				}
			});
		}
	});
	
	return BridgeStatusView;
	
});