define(['marionette',
        'app/EventNames',
        'text!templates/TopNavView.html'],
        function(Marionette,
        		EventNames,
        		theTemplate) {

	var TopNavView = Marionette.CompositeView.extend({
		template: _.template(theTemplate),

		templateHelpers: {
            version: function() {
		        return PacBridgeApp.version;
            }
        }
	});

	return TopNavView;
});