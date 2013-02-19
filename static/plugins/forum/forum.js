this.Forum = {};
Forum.VERSION="0.1"

Forum.whichTemplate = function(depth, doc){
    if(depth==0) return "post";
    return "comment";
}

Forum.templateDivId = function(id, depth,template){
    return id+"-"+depth;
}

Forum.templateDivClass = function(id, depth,template){
    return template+"-"+depth;
}

Forum.afterRender = function(divId, doc){
  
}
