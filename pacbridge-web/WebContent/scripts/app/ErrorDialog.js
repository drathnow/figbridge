define(['marionette',
        'bootstrap',
        'text!templates/ErrorDialog.html'],
		function(Marionette,
				Bootstrap,
				theTemplate) {

	var ErrorDialog = Marionette.ItemView.extend({
	    template: _.template(theTemplate),

	    initialize: function(options) {
			this.title = options.title;
	    	this.message = options.message;
	    },

	    ui: {
	    	title: '#title',
	    	message: '#message',
	    	okButton: 'button.ok'
	    },


	    onRender: function() {
            this.ui.title.html(this.title);
            this.ui.message.html(this.message);
	    },

	    events: {
	        'click @ui.okButton': 'doClose',
	    },

	    doClose: function() {
            this.$el.children().first().modal('hide');
            this.trigger('close');
	    },

	    onShow: function () {
	    	this.$el.children().first().modal('show');
	    	this.ui.okButton.focus();
	    }

	});

	return ErrorDialog;
});