this.Job = {};
Job.VERSION="0.1"
//TODO when instantiating, should make a copy of these things for each div
Job.whichTemplate = function(depth, doc){
    return "job";
}

Job.templateDivId = function(id, depth,template){
    return id;
}

Job.templateDivClass = function(id, depth,template){
    return template+"__"+depth;
}

Job.afterRender = function(divId, doc){

}
