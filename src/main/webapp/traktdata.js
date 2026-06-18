//
var map=null;
var projectionP = ol.proj.get('EPSG:3006');
var theurl;
var drawSource = new ol.source.Vector({wrapX: false});
var selSource =new ol.source.Vector({wrapX: false});
var gpsSource =new ol.source.Vector({wrapX: false});
var logSource =new ol.source.Vector({wrapX: false});
var mainSource =new ol.source.Vector({wrapX: false});
var fuSource =new ol.source.Vector({wrapX: false});
var stemSource =new ol.source.Vector({wrapX: false});
var adraw=null; 
var drawVector=null;
var selVector=null;
var mainVector=null;
var fuVector=null;
var stemVector=null;
const work="arbetar";
const idle="redo";
var isBusy=false;
var doSelect=false;
var prevSelFeat=null;
var doFit=true;

var incrId=0;
var selectedRowId;
var scaco1=[];

var timerVar;

var gpsFeat;

var tok;

// Orthophoto layer fetched directly from maps.forestlink.se via WMTS (no backend proxy needed)
var ortophotoLayer = new ol.layer.Tile({
	extent: [265000, 6130000, 1065000, 7730000]
});

(function() {
	var apiKey = "c52f1b8c-a047-4e86-a5f1-9c9e5a0a1c3d";
	var wmtsUrl = "https://maps.forestlink.se/api/wmts";

	fetch(wmtsUrl + "?service=WMTS&request=GetCapabilities", {
		headers: { "X-API-Key": apiKey }
	})
	.then(function(response) { return response.text(); })
	.then(function(text) {
		// The capabilities from this service returns coordinates in reversed order — swap them back
		var fixed = text.replace(/(\d+\.\d+|\d+)\s+(\d+\.\d+|\d+)/g, function(m, x, y) { return y + " " + x; });
		var parser = new ol.format.WMTSCapabilities();
		var result = parser.read(fixed);
		var opts = ol.source.WMTS.optionsFromCapabilities(result, {
			layer: "ortofoto",
			matrixSet: "ortofoto_3006"
		});
		if (!opts) return;
		opts.tileLoadFunction = function(tile, src) {
			var xhr = new XMLHttpRequest();
			xhr.open("GET", src);
			xhr.setRequestHeader("X-API-Key", apiKey);
			xhr.responseType = "arraybuffer";
			xhr.onload = function() {
				var blob = new Blob([new Uint8Array(this.response)], { type: "image/jpeg" });
				tile.getImage().src = (window.URL || window.webkitURL).createObjectURL(blob);
			};
			xhr.send();
		};
		ortophotoLayer.setSource(new ol.source.WMTS(opts));
	});
})();

$(document).ready(function(){

	
	$(".dropdown").click(function(event){
		console.log("Triggered by the " + event.target.id + " element.");
		switch(event.target.id){
			case "mnuItemPolygon":{addDrawInteraction("Polygon",1);};break;
			//mnuItemEditPolygons
			case "mnuItemEditPolygons":{addDrawInteraction("Polygon",2);};break;
			case "mnuItemLinje":{addDrawInteraction("LineString",1);};break;
			case "mnuItemPunkt":{addDrawInteraction("Point",1);};break;
			case "mnuItemMarkera":{addSelectInteraction();};break;
			case "mnuItemTabort":{deleteSelectedFeature();};break;
			case "mnuItemBuyers":{performBuyers();};break;
			//stampos
			case "mnuItemAllStems2":{fetchHarvesterPositionsVO("alla");};break;
			case "mnuItemStemsTradgrupp":{fetchHarvesterPositionsVO("grupp");};break;
			case "mnuItemStemsHogstubbar":{fetchHarvesterPositionsVO("hog");};break;
			case "mnuItemStemsEvighetstrad":{fetchHarvesterPositionsVO("evighet");};break;
			case "mnuItemStemsKulturstubbar":{fetchHarvesterPositionsVO("kultur");};break;
			case "mnuItemStemsRensa":{stemSource.clear();};break;
			//mnuItemTop
			case "mnuItemTop":{changeRenderOrder(1);};break;
			case "mnuItemBottom":{changeRenderOrder(2);};break;
			//mnuItemGpsOn
			case "mnuItemGpsOn":{$("#liMnuItemGpsOn").addClass('disabled').siblings().removeClass('disabled');
									gpsOn();
								};break;
			//mnuItemGpsOff
			case "mnuItemGpsOff":{$("#liMnuItemGpsOff").addClass('disabled').siblings().removeClass('disabled');
									gpsOff();
									};break;
			case "mnuShowLog":{getTracklogFromServer(1);} break;
			//mnuClearLog
			case "mnuClearLog":{getTracklogFromServer(2);} break;
			case "mnuGotoCoord":{$("#gotoCoordModal").modal()};break;
		}
		
	});
	
	$(".btn").click(function(event){
		console.log("Triggered by a " + event.currentTarget.id + " element.");
		switch(event.currentTarget.id){
			case "btnAddConcept":{incrId++;addRow(incrId,null,null)};break;
			case "btnDelConcept":{delRow()};break;

		}
		
	});
	
	$("#btnDoGotoCoord").click(function(){
		var xy=$("#gotocoord").val();
		
		var c=xy.split(",");
//		c.push(x);
//		c.push(y);
		console.log(c);
		map.getView().setCenter(c);
	});
	
	$("#loginbtn").click(function(){
		var tmp=$("#tok").val();
	
		tok=btoa(tmp);
		setScaco();
		initMap();
	});
	
	$("#freetext1").change(function(event){
		
		var str=$("#freetext1").val();
		//console.log(str);
		prevSelFeat.set("freetext",str);
		updateFeatureOnServer();
		//console.log("ny text "+prevSelFeat.get("freetext"));
	});
	

	
  	
 	$('#tableConcepts').on('click', '.clickable-row', function(event) {
  	 // console.log("hej "+this.id);
  	  selectedRowId=this.id;
  	  $(this).addClass('active').siblings().removeClass('active');
  	  
  	});
  	
   
	setContainerHeight();
	$(window).resize(function(){
		setContainerHeight();
	});

});

var selectStyle=new ol.style.Style({
    stroke: new ol.style.Stroke({
        color: '#00ffff',
        width: 3
      }),
      image: new ol.style.Circle({
          radius: 7,
          fill: new ol.style.Fill({
            color: '#00ffff'
          })
        })
});





/******************************************************************
 * FUNCTIONS
 ******************************************************************/



function setScaco(){
	$.get('SchemeListGetter', {"token":tok},
			function(resp) {
				if (resp.err!=undefined){errorHandler(resp);return;}
				//console.log("reply from schemagetter init");
				resp.schemes.forEach(function(arec){
					scaco1.push(arec);
					//console.log(arec);
				})
			})
		.fail(function() {
			showServerStatus(idle);
		}
	);
} 



function showServerStatus(msg){
	if (msg==work){
		isBusy=true;
	}
	else {
		isBusy=false;
	};
	$("#serverstatus").empty().text("Server status: "+msg);
}

function checkStatus(checkSel) {
	var r=false;

	if (checkSel && selSource!=undefined&&selSource.getFeatures()!=undefined&&selSource.getFeatures().length<1){
		r=true;
		//alert("Var vänlig välj ett objekt");
	};
	if (isBusy){
		r=true;
		//alert("Var vänlig vänta till servern arbetat klart");
	};
	console.log("check status:"+r);
	return r;
}







//genom klicka på plusknappen, eller från handle select som läser feature propps
function addRow(nid,scheme,concept){
	incrId=nid;
	if (prevSelFeat==undefined){return}
	
	var rowToAdd="<tr class=\"clickable-row\" id='"+nid+"'><td><select class='form-control selscheme' id='sels"+nid+"'></select></td><td><select class='form-control selconcept' id='selc"+nid+"'></select></td></tr>";
	//console.log("adding row:"+rowToAdd);
	$("#tableBodyConcepts").append(rowToAdd);
	
	$('#sels'+nid).empty();
	//populate selscheme
	if (scheme==undefined){
		$('#sels'+nid).append("<option value=\"0\">inget valt</option>");		
	}

	scaco1.forEach(function(arec){
		//console.log(arec.scheme);
		$('#sels'+nid).append("<option value=\""+arec.scheme+"\">"+arec.label+"</option>");
	})
	
	if (scheme!=undefined){
		  $("#sels"+nid).val(scheme);
		  getConceptList("sels"+nid,scheme,concept);
	}
	
	setSelectSchemeEvent(nid);
	setSelectConceptEvent(nid);
	
}


function setSelectSchemeEvent(nid){
    $("#sels"+nid).on("change", function(event){
    	var aid=event.target.id;
    	//console.log("klickat på "+event.target.id);
        getConceptList(aid,$("#"+aid+" option:selected").val(),null);
    });
}



function setSelectConceptEvent(nid){
    $("#selc"+nid).on("change", function(event){
    	console.log("selchange");
    	var selcId=event.target.id;
        //remove inget valt
        $("#"+selcId+" option").each(function() {
            if ($(this).val()=="0"){ this.remove()};
        }); 

        updateSelfeatFromTableSelects();
    });
}

//clear feat concepts, add what is selected i table, update server
function updateSelfeatFromTableSelects(){
    
    var table = document.getElementById('tableConcepts');
    var rowLength = table.rows.length;
    
    prevSelFeat.set("concepts",[]);
    for(var i=0; i<rowLength; i+=1){
    	  var row = table.rows[i];

    	  var aid=row.id;
    	  var selectedConcept=$("#selc"+aid+" option:selected").val();
    	  var selectedScheme=$("#sels"+aid+" option:selected").val();
    	  var schemeandconcept={scheme:selectedScheme, concept:selectedConcept};
    	  prevSelFeat.get("concepts").push(schemeandconcept);
    	  //console.log(aid+" "+selectedConcept);

    }

    var placeid=prevSelFeat.getId();
    updateFeatureOnServer();
    styleFeat(prevSelFeat);
   // debugPrint("after updateSelfeatFromTableSelects",prevSelFeat);
}

function updateFeatureOnServer(){
	prevSelFeat.get("concepts").forEach(function(concept){
		console.log(JSON.stringify(concept));
	})
	
	$.get('FeatureUpdater', {"token":tok,"aid":prevSelFeat.getId(),
							"vo":prevSelFeat.get("vo"),
							"tn":prevSelFeat.get("tn"),
							"oui":prevSelFeat.get("oui"),
							"freetext":prevSelFeat.get("freetext"),
							"concepts":JSON.stringify(prevSelFeat.get("concepts")),
							"traktinfo":JSON.stringify(prevSelFeat.get("traktinfo"))
							},

			function(resp) {
				if (resp.err!=undefined){errorHandler(resp);return;}
				//console.log("reply from FeatureUpdater");
			})
		.fail(function() {
			showServerStatus(idle);
		}
	);
	
	
}



function getConceptList(selsId,scheme,concept){
	var selcId=selsId.replace("sels","selc")
	//$("#tableBodyPlaceConcept").empty();
	//console.log("getting conceptlist "+selcId+" scheme:"+scheme);
	
	$('#'+selcId).empty();
	//populate selscheme
	if (concept==undefined){
		$('#'+selcId).append("<option value=\"0\">inget valt</option>");	
	}
	scaco1.forEach(function(arec){

		if (arec.scheme==scheme){
			arec.concepts.forEach(function(concept){
				//console.log(concept.label);
				$('#'+selcId).append("<option value=\""+concept.concept+"\">"+concept.label+"</option>");
			})
		}
	})
	
	if (concept!=undefined){
		  $('#'+selcId).val(concept);
	}

	
}

function delRow(){
	//console.log("deleting row "+selectedRowId);
	$("#"+selectedRowId).remove();

	updateSelfeatFromTableSelects();
}


function deleteSelectedFeature(){

	if (checkStatus(true)){return};

	showServerStatus(work);
	var aid=prevSelFeat.getId();
	//console.log("deleting"+aid);
	$.get('FeatureDeleter', {"token":tok,"aid":aid},
			function(resp) {
				if (resp.err!=undefined){errorHandler(resp);return;}
				//console.log("deleted from server"+aid);
				showServerStatus(idle);
				mainSource.removeFeature(prevSelFeat);
				selSource.clear();
				//console.log("deleted in mainsource"+aid);
			})
		.fail(function() {
			showServerStatus(idle);
		}
	);
}

function saveNewFeatureToServer(f){
	//console.log("savin");
	if (checkStatus(false)){return};
	showServerStatus(work);
	var t=f.getGeometry().getType();
	console.log(t);
	var geoJsonGeom = new ol.format.GeoJSON();    
	var s=geoJsonGeom.writeFeature(f);
	
	$.get('FeatureSaver', {"token":tok,"feat":s,"type":t},
			function(resp) {

				if (resp.err!=undefined){errorHandler(resp);return;}
				showServerStatus(idle);

				//var returnedFeat=(new ol.format.GeoJSON()).readFeatures(resp.feat);
				//!set style here

				//console.log("newid: "+resp.newid);
				if (t==="Polygon"){
					getFeaturesFromServer();
				} else{
					var newId=resp.newid;
					var aclone=f.clone();
					aclone.setId(newId);
					mainSource.addFeature(aclone);
					aclone.set("concepts",[],true);
					aclone.set("freetext","",true);
					styleFeat(aclone);
				}

				drawSource.clear();
			})
		.fail(function() {
			showServerStatus(idle);
			drawSource.clear();
		}
	);
	
}

//ModifyFeaturesOnServer
function modifyFeaturesOnServer(f){
	//console.log("savin");
	if (checkStatus(false)){return};
	showServerStatus(work);

	var geoJsonGeom = new ol.format.GeoJSON();    
	var s=geoJsonGeom.writeFeature(f);
	
	$.get('FeatureModifyer', {"token":tok,"feat":s},
			function(resp) {

				if (resp.err!=undefined){errorHandler(resp);return;}
				showServerStatus(idle);

				//var returnedFeat=(new ol.format.GeoJSON()).readFeatures(resp.feat);
				//!set style here

				console.log("newid: "+resp.newid);
  			    getFeaturesFromServer();


				drawSource.clear();
			})
		.fail(function() {
			showServerStatus(idle);
			drawSource.clear();
		}
	);
	
}

function changeRenderOrder(opt){
	if (checkStatus(true)){return};
	showServerStatus(work);
	//console.log("change render");
	var aid=prevSelFeat.getId();
	$.get('FeatureRenderSequencer', {"token":tok,"aid":aid,"opt":opt},
			function(resp) {

				if (resp.err!=undefined){errorHandler(resp);return;}
				showServerStatus(idle);

  			    getFeaturesFromServer();

			})
		.fail(function() {
			showServerStatus(idle);
			drawSource.clear();
		}
	);
}



function getFeaturesFromServer(){
	if (checkStatus(false)){return};
	showServerStatus(work);
	//console.log("calling FF");
	$.get('FeatureFetcher', {"token":tok,"test":1},
			function(resp) {
				//console.log("reply from FF");
				if (resp.err!=undefined){errorHandler(resp);return;}
				
				showServerStatus(idle);
				var feats=(new ol.format.GeoJSON()).readFeatures(resp.feats);
				//console.log("now styling");
				styleFeats(feats);
				
				mainSource.clear();
				selSource.clear();
				mainSource.addFeatures(feats);
/*				var myJSON = JSON.stringify(resp);
				console.log(JSON.stringify(myJSON));*/
				var fuFeats=(new ol.format.GeoJSON()).readFeatures(resp.followupFeats);
				styleFuFeats(fuFeats);
				fuSource.clear();
				fuSource.addFeatures(fuFeats);

				if (doFit) {
					var x=Number(localStorage.getItem("traktdemocenterx")); 
					var y=Number(localStorage.getItem("traktdemocentery")); 
					var aZoom=localStorage.getItem("traktdemozoom");
					//console.log(x+" "+y+" "+aZoom);
					if (aZoom!=undefined){
						var p=[x,y];
						map.getView().setCenter(p);
						map.getView().setZoom(aZoom);
						
					}else{
						map.getView().fit(mainSource.getExtent(), (map.getSize()));
					}
					doFit=false;

				}
			})
		.fail(function() {
			showServerStatus(idle);
		}
	);

}

function fetchHarvesterPositionsVO(opt){
	var aid=prevSelFeat.getId();
	stemSource.clear();
	//console.log("hämtar stammar för vo=");
	$.get('stemfetcher', {"token":tok,"aid":aid,"opt":opt},
			function(resp) { 
				
				var feats=(new ol.format.GeoJSON()).readFeatures(resp.geoms);
				
				stemSource.addFeatures(feats);
				
			})
		.fail(function() { 
			alert("Det gick inte att hämta stammar");
		}
	);
};

function getTracklogFromServer(opt){
	if (checkStatus(false)){return};
	showServerStatus(work);
	console.log("calling tracklog");
	$.get('TracklogGetter', {"token":tok,"opt":opt},//1==showw 2==clear
			function(resp) {
				console.log("reply from FF");
				if (resp.err!=undefined){errorHandler(resp);return;}

				showServerStatus(idle);
				logSource.clear();

				if (opt==1){
					var feats=(new ol.format.GeoJSON()).readFeatures(resp.feats);
					logSource.addFeatures(feats);
				}
			})
		.fail(function() {
			showServerStatus(idle);
		}
	);
	
}

function initMap(){

	// ortophotoLayer is initialised at module level via WMTS from maps.forestlink.se
	
//	var vmsSvo = new ol.layer.Tile({
//        source: new ol.source.TileWMS({
//            url:"http://geodpags.skogsstyrelsen.se/arcgis/services/Geodataportal/GeodataportalVisaBiotopskydd/MapServer/WmsServer",
//            projection:projectionP
//        })
//	});

	drawVector = new ol.layer.Vector({
      source: drawSource,
      style: new ol.style.Style({
        fill: new ol.style.Fill({
          color: 'rgba(255, 255, 255, 0.1)'
        }),
        stroke: new ol.style.Stroke({
          color: '#ffcc33',
          width: 2
        }),
        image: new ol.style.Circle({
          radius: 7,
          fill: new ol.style.Fill({
            color: '#ffcc33'
          })
        })
      })
    });
	
	selVector = new ol.layer.Vector({
	      source: selSource,
	      style: new ol.style.Style({
	        stroke: new ol.style.Stroke({
	          color: '#00ffff',
	          width: 3
	        }),
	        image: new ol.style.Circle({
	            radius: 7,
	            fill: new ol.style.Fill({
	              color: '#00ffff'
	            })
	          })
	      })
	 });
	

	
	gpsVector = new ol.layer.Vector({
	      source: gpsSource,
	      style: new ol.style.Style({
	        stroke: new ol.style.Stroke({
	          color: '#eeeeee',
	          width: 5
	        }),
	        image: new ol.style.Circle({
	            radius: 12,
	            fill: new ol.style.Fill({
	              color: 'rgba(255,153,255,0.7)'
	            })
	        })
	      })
	 });
	
	stemVector = new ol.layer.Vector({
	      source: stemSource,
	      style: new ol.style.Style({
	        fill: new ol.style.Fill({
	          color: '#ffcc33'
	        }),
	        stroke: new ol.style.Stroke({
	          color: '#ffcc33',
	          width: 2
	        }),
	        image: new ol.style.Circle({
	          radius: 2,
	          fill: new ol.style.Fill({
	            color: '#ffcc33'
	          })
	        })
	      })
	 });
	
	logVector = new ol.layer.Vector({
	      source: logSource,
	      style: new ol.style.Style({
		        stroke: new ol.style.Stroke({
			          color: '#eeeeee',
			          width: 5
			        }),
			    image: new ol.style.Circle({
			            radius: 4,
			            fill: new ol.style.Fill({
			              color: 'rgba(255,153,255,0.7)'
			            })

			    })
	      })
	 });
	
	mainVector = new ol.layer.Vector({
	      source: mainSource,
	      style: new ol.style.Style({
	        fill: new ol.style.Fill({
	          color: 'rgba(255, 255, 255, 0.3)'
	        }),
	        stroke: new ol.style.Stroke({
	          color: '#ffcc33',
	          width: 2
	        }),
	        image: new ol.style.Circle({
	          radius: 7,
	          fill: new ol.style.Fill({
	            color: '#ffcc33'
	          })
	        })
	      })
	});
	
	fuVector = new ol.layer.Vector({
	      source: fuSource,
	      style: new ol.style.Style({
	        fill: new ol.style.Fill({
	          color: 'rgba(255, 0, 0, 0.3)'
	        }),
	        stroke: new ol.style.Stroke({
	          //color: '#ffcc33',
	          width: 2
	        }),

	        image: new ol.style.Circle({
	          radius: 7,
	          fill: new ol.style.Fill({
	            color: '#ffcc33'
	          })
	        })
	      })
	});
	
	getFeaturesFromServer();
	
    var x=Number(localStorage.getItem("traktdemocenterx")); 
	var y=Number(localStorage.getItem("traktdemocentery")); 
	var aZoom=localStorage.getItem("traktdemozoom");
	if (x==undefined || x<407659.8||x<707659.8){
		var x=Number(657659.8); 
		var y=Number(6711257.8); 
		var aZoom=10;
	}


	map = new ol.Map({
		target: 'map',
		layers: [ortophotoLayer,fuVector,stemVector,mainVector,drawVector,selVector,gpsVector,logVector],
		interactions: ol.interaction.defaults({doubleClickZoom: false}),
		view: new ol.View({
			center:[x, y],
			zoom: aZoom,
			projection:projectionP
		})
	});
	//console.log("handle select init map");
	map.on('singleclick', function(evt) { 
		handleSelect(evt);
		localStorage.setItem("traktdemocenterx", map.getView().getCenter()[0]);
		localStorage.setItem("traktdemocentery", map.getView().getCenter()[1]);
		localStorage.setItem("traktdemozoom", map.getView().getZoom());
 	});
	
	map.on('dblclick', function(evt) { 
		console.log("härL");
		handleDoubleClick(evt);

 	});
} 

function handleDoubleClick(evt){
	$("#featIdLabel").text("Identitet:");
	console.log("här!");
	map.forEachFeatureAtPixel(evt.pixel,function(feature, layer) {
		if ( layer==fuVector){
			console.log("här?");
			var id=feature.getId();
			$("#featIdLabel").text("Identitet: "+id);
		}
		
	})	

}	

function handleSelect(evt){
	if (doSelect==false){return};
	
	selSource.clear();
	
	$("#tableBodyConcepts").empty();
	$("#freetext1").val('');
	$('#freetext1').prop('disabled', true);
	$("#featIdLabel").text("Identitet:");

	var i=0;
	var done=false;
	map.forEachFeatureAtPixel(evt.pixel,function(feature, layer) {
		i=i+1;	
		if ( layer==selVector||layer==fuVector){
			//console.log("selected layer"+feature.getId());
		} else if (!done&&feature!==prevSelFeat){
			//hantera lager
			//console.log("lägger till"+i);
			var id=feature.getId();
			//console.log("id: "+id);
			var clone=feature.clone();
			clone.setStyle(null);
			selSource.addFeature(clone);
			
			prevSelFeat=feature;
			//id+fritext
			
			$("#featIdLabel").text("Identitet: "+id);
			$('#freetext1').prop('disabled', false);
			var str=$("#freetext1").val(prevSelFeat.get("freetext"));
			
			//selectboxar
			var conc=prevSelFeat.get("concepts");
			aid=0;
			conc.forEach(function(element) {
				  aid++;
				  //console.log(element.concept);
				  addRow(aid,element.scheme,element.concept);
			}
			);

			
			
			done=true;
			
			
			
			
		}else if (feature===prevSelFeat){
			prevSelFeat=null;
			//console.log("exits"+i);
			return;
		}
	},{hitTolerance: 5});
	
}



function handleTableUpdate(f,doStyle){
	//if (checkStatus(true)){return};

	showServerStatus(work);
	var aid=f.getId();
	$("#tableBodyVarden").empty();
	$("#tableBodyHansyn").empty();
	$("#tableBodyOtherFactors").empty();
	$("#tableBodyAtg").empty();
	$("#vardenTable").hide();
	$("#hansynTable").hide();
	$("#otherFactorTable").hide();
	$("#atgTable").hide();
	
	$.get('GetFactorsForPlace', {"token":tok,"aid":aid},
			function(resp) {
				if (resp.err!=undefined){errorHandler(resp);return;}
				showServerStatus(idle);
				var factors=resp.records;
				var i=factors.length;
				var concepts=[];
				f.unset("concepts",true);
				factors.forEach(function(arec){
					i=i+1;
					var attr=arec.attribute_id;
					concepts.push(arec.concept);
					//console.log("skriver ut attr"+attr+" "+i);
					switch (Number(attr)){
						case 2:case 3:case 4: case 9:
						case 5:{
							$("#tableBodyVarden").append("<tr id=\""+arec.concept+"\"><td>"+arec.preflabel+"</td><tr>");
							$("#vardenTable").show();
							};break;
						case 7:{
							$("#tableBodyHansyn").append("<tr id=\""+arec.concept+"\"><td>"+arec.preflabel+"</td><tr>");
							$("#hansynTable").show();
							};break;
						case 8:{
							$("#tableBodyAtg").append("<tr id=\""+arec.concept+"\"><td>"+arec.preflabel+"</td><tr>");
							$("#atgTable").show();
							};break;
						case 1:{
							$("#tableBodyOtherFactors").append("<tr id=\""+arec.concept+"\"><td>"+arec.preflabel+"</td><tr>");
							$("#otherFactorTable").show();
							};break;
					}
				})
				f.set("concepts",concepts,true);
				//console.log("stylar nu");
				if (doStyle){
					styleFeat(f);
				}

			})
		.fail(function() {
			showServerStatus(idle);
		}
	);
}




function addDrawInteraction(t,opt) {
	console.log("adding draw avent")
	if (adraw!==null){
		map.removeInteraction(adraw);
		adraw=null;
	}
    var geometryFunction, maxPoints;
    doSelect=false;
    adraw = new ol.interaction.Draw({
      source: drawSource,
      type: t,
      geometryFunction: geometryFunction,
      maxPoints: maxPoints
    });
    adraw.on('drawend', function (event) {
    	//console.log("nu spara");
    	if (opt==1){
    		saveNewFeatureToServer(event.feature);
    	} else {
    		modifyFeaturesOnServer(event.feature);
    	}
    		
    		
    });
    map.addInteraction(adraw);
}

function addSelectInteraction(){
	doSelect=true;
	if (adraw!==null){
		map.removeInteraction(adraw);
	}
	
}

function setContainerHeight(){
	var h=$(window).height()-100;
	$('#divData').css("height",h);
	$('#map').css("height",h);

}

function errorHandler(resp){
	//console.log(JSON.stringify(resp));
	showServerStatus(idle);
	if (resp.err!=undefined && resp.err=="no_session"){
		window.location="login.html";
	} else{
		alert("Servern meddelar fel: "+resp.message);
	}
	
}

/*function debugPrint(head,feat){
	var conc=feat.get("concepts");
	console.log("*********"+head+"*********************");
	conc.forEach(function(element) {
		  console.log(element.scheme+" "+element.concept);
	});
	console.log("**************************************");
}*/


/*var gpsready=true;


function gpsInit(){

	var geom=new ol.geom.Point([648500,6635600]);
	gpsFeat=new ol.Feature();
	gpsFeat.setGeometry(geom);
	gpsFeat.setId(1);
}

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else { 
        alert("Geolocation is not supported by this browser");
    }
}*/

/*function showPosition(position) {

	
    $.get('GpsHandler', {"token":tok,"lat":position.coords.latitude,"lon":position.coords.longitude},
			function(resp) { 
				if (resp.err!=undefined){errorHandler(resp);return;}
				//console.log("reply from gpshandler "+resp.x +" "+resp.y);
				var coords=[];
				coords.push(resp.x);
				coords.push(resp.y);
				console.log("coords "+coords);

				//console.log("id:"+gpsFeat.getId());
				
				var g=gpsFeat.getGeometry();
				//console.log("geom:"+g.getCenter());
				
				g.setCoordinates(coords);
				//g.setCenter(coords);
				//g.setRadius(10);

				gpsready=true;
			})
		.fail(function() { 
			errorHandler(resp);
		}
	);
    
    
}
*/

/*function gpsOn(){
	if (timerVar==undefined){

		//feat.setStyle(cStyle2);
		gpsSource.clear();
		gpsSource.addFeature(gpsFeat);
		
		timerVar = setInterval(myTimer, 2000);
	} else {
		//timervar=null;
	}
	
}

function gpsOff(){
	window.clearInterval(timerVar);
	timerVar=undefined;
	gpsSource.clear();
}

var counter=0;
function myTimer() {
	counter++;
	console.log(counter);
	if (counter>1000){
		gpsOff();
		alert("GPS stängs av efter 1000 pts")
	}
	if (gpsready){
		gpsready=false;
		getLocation();
	}
        
}*/


/*function performBuyers() {
    if (checkStatus(true)) { return };

    showServerStatus(work);
    var aid = prevSelFeat.getId();
    console.log("calling buyers" + aid);

    $.ajax({
        url: 'buyers?token='+tok+'&pid='+aid,
        type: 'GET',
        headers: {
            'token': tok, // Add any headers here
        },
        data: {
            "aid": aid
        },
        success: function(resp) {
            if (resp.err != undefined) {
                errorHandler(resp);
                return;
            }
            
            showServerStatus(idle);
            
            console.log(JSON.stringify(resp, null, 2));
            var newWindow = window.open();
            newWindow.document.write('<pre>' + JSON.stringify(resp, null, 2) + '</pre>'); // Pretty print JSON
            newWindow.document.title = "JSON Response";
            

            
            var newWindow = window.open();
            newWindow.document.clear;
            newWindow.document.write('<p><b>Virkesorder: </b>'+resp.ContractNumber+'</p>');
            newWindow.document.write('<p><b>Skotningsavstånd: </b>'+resp.terraindist+'</p>');
            newWindow.document.write('<p><b>Volymvägd lutning: </b>'+resp.slope+'</p>');
            newWindow.document.write('<p><b>Antal träd 15+: </b>'+resp.n15dbh+'</p>');
            newWindow.document.write('<p><b>Medräkningsbara: </b>'+resp.countable+'</p>');
            
			newWindow.document.write('<p></p>');
            resp.atgarder.forEach(function (atg) {
				
            	newWindow.document.write('<p><b>Atgärd id: </b>'+atg.id+'</p>');
//            	newWindow.document.write('<p><b>AD QD: </b>'+atg.AD+' '+ atg.QD+'</p>');
	            newWindow.document.write('<table border="1" cellpadding="5" cellspacing="0">'); 
	            newWindow.document.write('<thead>');
	            newWindow.document.write('<tr>'); 
	            newWindow.document.write('<th>'+resp.ContractNumber+'</th><th>Prognos</th><th>Utfall</th>'); 
	            newWindow.document.write('</tr>');  
	            newWindow.document.write('</thead>'); 
	            newWindow.document.write('<tbody>');
				atg.keyvalues.forEach(function (aPair) {
					newWindow.document.write('<tr>'); 
					newWindow.document.write('<td><b>'+aPair.title+'</b></td>'); 
					newWindow.document.write('<td>'+aPair.prognos+'</td>'); 
					newWindow.document.write('<td>'+aPair.utfall+'</td>'); 
	            	newWindow.document.write('</tr>');  
	            });
	            newWindow.document.write('</tbody>');
	            newWindow.document.write('</table>'); 
            });
            
			newWindow.document.title = "Resultat id="+aid;
            console.log("buyers called ok" + aid);
        },
        error: function() {
			console.log("buyers did not work" + aid);
			showServerStatus(idle);
            errorHandler(resp);
        }
    });
    
    
}*/











