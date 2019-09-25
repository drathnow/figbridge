define(['marionette',
        'text!templates/Foo.html'], 
        function(Marionette,
        		 theTemplate){
	
	var Foo = Marionette.Layout.extend({
		template: _.template(theTemplate),		
	});
	
	return Foo;
});