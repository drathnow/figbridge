define(['marionette', 
        'views/OutgoingRequestRowView', 
        'text!templates/OutgoingRequestsTable.html'], 
		function(Marionette, 
		        OutgoingRequestRowView, 
				theTemplate){
	
	var OutgoingRequestsTableView = Marionette.CompositeView.extend({
		template: _.template(theTemplate),
		itemView: OutgoingRequestRowView,
 		itemViewContainer: 'tbody',
 		
        onShow: function() {
            $('#or-table').DataTable({
                responsive: true,
                aoColumnDefs : [ {
                    bSortable : false,
                    aTargets : [ 6 ]
                } ],

            });
        },

	});
	
	return OutgoingRequestsTableView;
});