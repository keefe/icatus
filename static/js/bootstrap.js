function bootstrap(callback){
	$.ajax({
	  datatype:"json",
	  url:"/_",
	  success: function(data) {
	    callback(data);
	  },
	  error: function(jqXHR, textStatus, errorThrown) {
	    console.log("Error Retrieving Server Bootstrap, This will end poorly."); //error logging
	  },
	  async:false
	});  
}

 
