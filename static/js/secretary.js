this.Secretary = {};

Secretary.VERSION = "0.1";

Secretary.cache = {};

Secretary.id="master";

Secretary.base = "/";


Secretary.print = function(){
    console.log("Secretary" + this.VERSION + " " + this.id + "@"+this.base);
}

Secretary.put = function(name, data){
    this.cache[name] = data;
}

Secretary.get = function(name){
    return this.cache[name];
}

Secretary.hire = function(id){
    var her = jQuery.extend(true, {}, Secretary);
    her.id = id;
    return her;
}

//TODO merge to the load function, don't feel like messing with cache stuff right now
Secretary.text = function(name){
    if(name.charAt(0)=='/'){
      name = name.substr(1);
    }
      var plain = "fail";
      
      u = this.base+name;
      console.log("Trying to load text @  " + u);
      $.ajax({
	datatype:"text",
	url:u,
	success: function(data) {
	  plain = data;	  
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log(textStatus); //error logging
	},
	async:false
      });
    return plain;
}

Secretary.delete = function(name, doc){
    if(name.charAt(0)=='/'){
      name = name.substr(1);
    }
      var plain = "fail";
      
      u = this.base+name;
      console.log("Trying to delete text @  " + u);
      $.ajax({
	type:"DELETE",
	contentType:"application/json",
	dataType:"json",
	url:u,
	data:JSON.stringify(doc),
	success: function(data) {
	  plain = data;	  
	  console.log(data);
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log(textStatus); //error logging
	},
	async:false
      });
    return plain;
}

Secretary.put = function(name, doc){
    if(name.charAt(0)=='/'){
      name = name.substr(1);
    }
      var plain = "fail";
      
      u = this.base+name;
      console.log("Trying to put text @  " + u);
      $.ajax({
	type:"PUT",
	contentType:"application/json",
	dataType:"json",
	url:u,
	data:JSON.stringify(doc),
	success: function(data) {
	  plain = data;	  
	  console.log(data);
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log(textStatus); //error logging
	},
	async:false
      });
    return plain;
}


Secretary.load = function(name, returnType){
    returnType = returnType || "json";
    if(name.charAt(0)=='/'){
      name = name.substr(1);
    }
    var c = this.cache;
    if(!this.cache[name]){
	u = this.base+name;
	console.log("Trying to load form " + u);
	$.ajax({
	  datatype:returnType,
	  url:u,
	  success: function(data) {
	    var clazz = typeof data;
	    
	    console.log(name + " loaded " + clazz);
	    if(clazz=="string"){
	      data = eval('('+data+')');
	      console.log("converted" + (typeof data));
	    }
	    c[name]=data;
	  },
	  error: function(jqXHR, textStatus, errorThrown) {
	    console.log(textStatus); //error logging
	  },
	  async:false
	});
    }
    return this.cache[name];
}