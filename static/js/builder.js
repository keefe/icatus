this.Builder = {};
Builder.VERISON="0.1";
Builder.id="master";
//TODO issues with multiple templates of same name
//TODO factor out core builder and handlebars template engine

//maybe I'll support multiple data sources eventually?
Builder.office = {};
Builder.templateSpec={};
Builder.uiMeta={};
Builder.controllers={};

Builder.construct = function(id){
  var site = jQuery.extend(true, {}, Builder);
  site.id = id;
  return site;
}

Builder.init = function(spec){
  if(spec){
    this.pluginPath = spec.pluginPath;
  }else{
    this.pluginPath = "/plugins";
  }
  this.secretary = Secretary.hire("root");  
}


Builder.loadScript = function(text){
    var sc0 = '<script type="text/javascript">';
    var sc1 = '</script>';
    
    var toAdd = sc0+text+sc1;
    $("head").append(toAdd);
  
}

Builder.loadStyle = function(text){
  
    //TODO no way to remove these
    var sc0 = '<style>';
    var sc1 = '</style>';
    
    var toAdd = sc0+text+sc1;
    $("head").append(toAdd);
  
}

Builder.fileTypes = [".js",".css",".tmpl"];

Builder.file2id = function(uri, callback){
  for(var i in this.fileTypes){
      console.log("check " + uri + " on " + this.fileTypes[i]); 
      var endex = uri.lastIndexOf(this.fileTypes[i]);
      if(endex>0){
	  var id = uri.substring(0,endex);
	  var lastSlash = id.lastIndexOf("/");
	  id = id.substring(lastSlash+1, id.length);
	  var text = this.secretary.text(uri);
	  callback(id, this.fileTypes[i], text);
	  return;
      }
  }
}


Builder.loadWidget = function(name){
  
    var spec = this.secretary.load("/_"+this.pluginPath+"/"+name);
    var container = this.secretary.load(this.pluginPath+"/"+name);
    var reallyThis = this;
    if(container && container.contains){
	for(var i in container.contains){
	  this.file2id(container.contains[i], function(id, type, text){
	      console.log("cb " + id + " , " + type);
	      if(".js"==type){
		  reallyThis.loadScript(text);
	      }else if(".css"==type){
		  reallyThis.loadStyle(text);
	      }else if(".tmpl"==type){
		  reallyThis.registerTemplate(id, text);
	      }
	  });
	}
    }
    
    /*may want to fix this eventually
    for(var i in spec.templates){
	this.loadTemplate(name, spec.templates[i]);
    }
    for(var j in spec.code){
      this.loadScript(spec.src, spec.code[j]);  
    }
    
    for(var k in spec.css){
      this.loadStyle(spec.src, spec.css[k]);  
    }
    */
}

Builder.getTemplate = function(name){
    return Handlebars.templates[name];
}

Builder.findRelations = function(docId, relations){
    if(relations==null){
      return {id:docId};
    }
//    var relations = this.office["relations"].load(docId, pre);
    return {id:docId};
}

Builder.renderWidget = function(widgetName, docId, whereTo){
  var up = "/_"+this.pluginPath+"/"+widgetName;
  var spec = this.secretary.load(up);

  var relations = this.findRelations(docId, spec.relations);
  var control = window[spec.singleton];
  control.secretary = this.secretary;
  var o = this.office;
  var reallyThis = this;
  var edge = "to";
  //TODO handle multiple relations
  //all these should probably be pulled out into a base controlller
  //that the forum.js etc is expected to replace
  var relations = {id:docId};
  if(spec.relations && spec.relations.length>0){
    var relBase = spec.relations[0].uriTemplate;
    var relURI = relBase.replace(spec.relations[0].varString, docId).replace("//","/");
    relations = this.secretary.load(relURI);
  }
  
  var recurseBuild = function(depth, rel, parent){
      var doc =reallyThis.secretary.load(rel.id);
      var template = control.whichTemplate(depth, doc);
      var okId = "r"+rel.id.split("/").join("-");
      //TODO for now, assuming 1 div per doc, also this ^ does not belong here
      var divid = control.templateDivId(okId, depth, template);
      var divclass = control.templateDivClass(okId, depth, template);
      
      var rndr = jQuery.extend(true, {divid:divid, divclass:divclass}, doc);
      var thetmp = reallyThis.getTemplate(template);
      if(!thetmp){
	  parent.append("Error Loading Template " + template + " for " + rel.id);
	  return;
      }
      var something = parent.append(thetmp(rndr));
      if(rel["link"] && rel["link"][edge]){
	var edges = rel["link"][edge];
	for(var i in edges){
	   var newRel = edges[i];
	   recurseBuild(depth+1, newRel,$("#"+divid));
	}
      }
      control.afterRender(divid, doc, rel.id);//TODO rather have rel.id up front
      //something.append("<div> <p>where am III???</p></div>");
      //console.log("here I am");
  }
  recurseBuild(0, relations, whereTo);
  console.log("here I am");
}

Builder.renderDoc = function(docId, templateId){
    var thetmp = this.getTemplate(templateId);
    var doc = this.office["domain"].load(docId);
    return thetmp(doc);
}

Builder.registerTemplate = function(id, text) {
  console.log("registering template for " + id);
  console.log("template value " + text);
  if (Handlebars.templates === undefined) {
	Handlebars.templates = {};
  }
  Handlebars.templates[id] = Handlebars.compile(text);
}

Builder.loadTemplate = function(prefix, name) {
    var h = Handlebars;
    if (Handlebars.templates === undefined || Handlebars.templates[name] === undefined) {
        var u = this.templateSpec.url +prefix+"/"+ name + this.templateSpec.suffix;
	$.ajax({
	    url : u,
	    success : function(response, status, jqXHR) {
		if (Handlebars.templates === undefined) {
		    Handlebars.templates = {};
		}
		Handlebars.templates[name] = Handlebars.compile(jqXHR.responseText);
		//Handlebars.templates[name] = Handlebars.compile(response);
	    },
	    async : false
	});
    }
    return Handlebars.templates[name];
}; 