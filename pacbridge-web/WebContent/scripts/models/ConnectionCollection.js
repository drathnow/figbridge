define(['marionette', 
        'models/Connection'], 
        function(Marionette, 
        		Connection) {
	return Backbone.Collection.extend({
		url: 'srv/connections',
		model: Connection,
		comparator: 'nuid'
	});
});