define(['marionette',
        'text!templates/NetworkRowView.html'],
		function(Marionette,
				theTemplate) {

	var NetworkRowView = Marionette.ItemView.extend({
		tagName: 'tr',

		template: _.template(theTemplate),

	});

	return NetworkRowView;
});