define(['marionette', 
        'bootstrap', 
        'text!templates/ConfirmView.html'], 
		function(Marionette, Bootstrap, theTemplate) {
	
	var ConfirmView = Marionette.ItemView.extend({
		
	    template: _.template(theTemplate),
	    
	    initialize: function(options) {
	    	this.model = options.model;
	    	if (options.onConfirm)
	    		this.onConfirm = options.onConfirm;
	    	if (options.onCancel)
	    		this.onCancel = options.onCancel;
	    },
	    
	    events: {
	        'click #yes': 'yesClicked',
	        'click #no': 'noClicked'
	    },

	    yesClicked: function (ev) {
	        ev.preventDefault();
	        this.onConfirm();
	        this.$el.children().first().modal('hide');
	        this.trigger('close');        
	    },

	    noClicked: function (ev) {
	        ev.preventDefault();
	        this.onCancel();
	        this.$el.children().first().modal('hide');
	        this.trigger('close');        
	    },
	    
	    onShow: function () {
	    	this.$el.children().first().modal('show');
	    },
	    
	    onConfirm: function() { 
	    },

	    onCancel: function() { 
	    }
	    
	});
	
	return ConfirmView;
});