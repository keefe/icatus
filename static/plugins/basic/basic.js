this.Basic = {};
Basic.VERSION="0.1"
//TODO when instantiating, should make a copy of these things for each div
Basic.whichTemplate = function(depth, doc){
    return "basic";
}

Basic.templateDivId = function(id, depth,template){
    return id;
}

Basic.templateDivClass = function(id, depth,template){
    return template+"__"+depth;
}

Basic.afterRender = function(divId, doc, uri){
  $("#"+divId).prepend("we're done loading now, so we can add whatever event listeners and post processing we want.");
}
