define(['marionette',
        'models/Device',
        'app/EventNames',
        'text!templates/CachedDeviceEditor.html'
        ], function(Marionette,
        			Device,
        			EventNames,
        		    theTemplate) {
	
	var CachedDeviceEditor = Marionette.ItemView.extend({
		tagName: 'form',
		className: 'form-horizontal',
		template: _.template(theTemplate),
		
	    ui: {
	    	nuid: '#nuidTxt',
	    	secretKey: '#keyTxt',
	    	firmware: '#frmTxt',
	    	networkNumber: '#netNumTxt',
	    	add: 'button.add'
	    }, 
	    
	    events: {
	    	'click @ui.add': 'onSubmit'
	    },
	    
	    onSubmit: function() {
	    	var that = this;
	    	var device = new Device();
	    	device.urlRoot += "/add";
	    	device.save(
	    			{
				    	nuid: that.ui.nuid.val(),
				    	secretKey: that.ui.secretKey.val(),
				    	firmwareVersion: that.ui.firmware.val(),
				    	networkNumber: that.ui.networkNumber.val()	    		
			    	}, 
			    	{
			    		success: function(model, response, options) {
			    			console.log("Success");
			     			PacBridgeApp.vent.trigger(EventNames.DeviceEditor.deviceUpdated);
			    		},
			    		error: function(model, response, options) {
			    			console.log("Error");
			    		}
			    	}
	    	);
	    	
	    }
	});
	
	return CachedDeviceEditor;
});