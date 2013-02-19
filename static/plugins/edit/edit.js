this.Edit = {};
Edit.VERSION="0.1"
//TODO when instantiating, should make a copy of these things for each div
//TODO could dynamically add the CSS and JSON files to the page when it is loading, then rely on ready event to deal with asynch
Edit.whichTemplate = function(depth, doc){
    return "edit";
}

Edit.templateDivId = function(id, depth,template){
    return id;
}

Edit.templateDivClass = function(id, depth,template){
    return template+"__"+depth;
}

Edit.afterRender = function(divId, doc, uri){
  var home = $("#"+divId);
  
  var reallyThis = this;
  
  home.find('.save').click(function(){
    console.log("click put");
    var newData = home.find('form').serializeArray();
    
    var expanded = {};
    for(var i in newData){
	expanded[newData[i].name] = newData[i].value;
    }
    
    console.log("PUT @ " + uri + JSON.stringify(expanded));
    reallyThis.secretary.put(uri, expanded);
  });
  home.find('.new').click(function(){
    $("input[type=text], textarea").val("");
	//console.log("tis " + this.attr('name'));
  });
  
    home.find('.delete').click(function(){
	reallyThis.secretary.delete(uri);
	$("input[type=text], textarea").val("");
    });
  
  //var newnew = home.children('input[name]');
//    $.each(newnew, function(index, value){	
//	console.log(newnew.name()+"   123     "+value);
//   });
  
  
}
