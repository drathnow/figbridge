define(['marionette', 
        'models/OutgoingRequest'], 
        function(Marionette, 
        		OutgoingRequest) {
	return Backbone.Collection.extend({
		url: 'srv/outgoingrequests',
		model: OutgoingRequest,
		comparator: 'creationDate'
	});
});