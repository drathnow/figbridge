define(['marionette', 
        'models/Device'], 
        function(Marionette, 
        		Device) {
	return Backbone.Collection.extend({
		url: 'srv/devices',
		model: Device,
		comparator: 'nuid'
	});
});