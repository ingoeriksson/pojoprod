function styleFeats(feats){
	
	feats.forEach(function(f){
		//console.log("sltyling1"+f.getGeometry().getType());
		styleFeat(f);
		
	})

}

function styleFeat(feat){
	var aFill=new ol.style.Fill();
	var aStroke=new ol.style.Stroke();
	var pFill=new ol.style.Fill();
	var pStroke=new ol.style.Stroke();
    var concepts=feat.get("concepts");
    
    var isAtg=false;
    var isTrakt=false;
    var isValue=false;
    var isKartobj=false;
    var isHansynNogo=false;
    var isHansynManage=false;
    var isSnitsel=false;
    var isAterrapport=false;
    
    feat.setStyle(null);
    //console.log("styling");
    if (concepts!=undefined){
        concepts.forEach(function(schemeandconcept){
        	var concept=schemeandconcept.concept;
        	//console.log("concept: "+concept);
        	if (!isTrakt){isTrakt=concept.includes("http://forestand.skogforsk.se/other_factors/traktgrans")};
        	if (!isAtg){isAtg=concept.includes("http://forestand.skogforsk.se/hpryield_atgtyp")};
        	if (!isValue){isValue=concept.includes("http://forestand.skogsstyrelsen.se/gmb")};
        	if (!isValue){isValue=concept.includes("http://forestand.skogforsk.se/nv")};
        	if (!isValue){isValue=concept.includes("http://forestand.skogforsk.se/hansyn/")};
        	if (!isKartobj){isKartobj=concept.includes("http://forestand.skogforsk.se/other_factors/")};
        	if (!isHansynNogo){isHansynNogo=concept.includes("http://forestand.skogforsk.se/hansyn/nogo")};
        	if (!isHansynNogo){isHansynNogo=concept.includes("http://forestand.skogforsk.se/hansyn/")};
        	if (!isHansynManage){isHansynManage=concept.includes("http://forestand.skogsstyrelsen.se/eavverka")};
        	if (!isSnitsel){isSnitsel=concept.includes("http://forestand.skogforsk.se/snitsling/snitslat")};
        	if (!isAterrapport){isAterrapport=concept.includes("http://forestand.skogforsk.se/aterrapport")};
        	
        	if (!isKartobj){isKartobj=concept.includes("http://www.sis.se/ss637009/Code_LandUseTrad/")};
        	//http://www.sis.se/ss637009/Code_LandUseTrad/
        	
        });
    	
    }
    
    const atgcol='#ff9933';
    const valuecol='#33cc33';
    const objcol='#999999';
    const hansyncol='#ff0000';
    const traktcol='#DCDCDC';
    const aterrapportcol='#cc33ff';
	
	aFill.setColor('rgba(255, 255, 255, 0.01)');
	aStroke.setColor('#cccccc');
	aStroke.setWidth(3);
	
	pFill.setColor('rgba(255, 255, 255, 0.5)');
	pStroke.setColor('rgba(255, 255, 255, 0.01)');
	pStroke.setWidth(3);
	
	if (isValue){
		aStroke.setColor(valuecol);
	} 
	else if (isAtg){
	    aStroke.setColor(atgcol);
	} 
	else if (isKartobj){
		aStroke.setColor(objcol);
	}
	
	if (isAterrapport){
		aStroke.setColor(aterrapportcol);
	}
	
	if (isHansynNogo){
		//aFill.setColor('rgba(255, 0, 0, 0.5)');
	}
	else if (isHansynManage){
		//aFill.setColor('rgba(255, 0, 0, 0.2)');
	}
	
	if (isSnitsel){
		aStroke.setLineDash([4]);
	}
	
	if (isTrakt){
		aStroke.setLineDash([10]);
		aStroke.setColor('rgba(255, 0, 0, 0.3)');
	}
	
	var aImage= new ol.style.Circle({
        radius: 5,
        fill: pFill,
        stroke:pStroke
    });
	
	var aStyle= new ol.style.Style({
          fill: aFill,
          stroke:aStroke,
          image: aImage
        })
	
	//console.log("styling "+aStyle);
	feat.setStyle(aStyle);
	
	
}

function styleFuFeats(feats){
	
	feats.forEach(function(f){
		//console.log("sltyling1"+f.getGeometry().getType());
		styleFuFeat(f);
		
	})

}

function styleFuFeat(feat){
	var aFill=new ol.style.Fill();
	var aStroke=new ol.style.Stroke();
	
	var concept=feat.get("concept");
	var vo=feat.get("vo");

    if (concept!=undefined){

        	//console.log("concept: "+concept +"vo"+vo);
        	if (concept.includes("avverkad")){
        		aFill.setColor('rgba(255, 255, 255, 0.1)');
        	} else if (concept.includes("vrig")) {
        		aFill.setColor('rgba(255, 0, 0, 0.2)');
        	}  else  {
        		aFill.setColor('rgba(255, 0, 0, 0.4)');
        	}

    	
    }
	
	
	//aFill.setColor('rgba(255, 255, 255, 0.01)');
	aStroke.setColor('rgba(0, 0, 0, 1)');
	aStroke.setWidth(1);
	var aImage= new ol.style.Circle({
        radius: 7,
        fill: aFill,
        stroke:aStroke
    });
	var aStyle= new ol.style.Style({
        fill: aFill,
        stroke:aStroke,
        image: aImage
      })
	feat.setStyle(aStyle);
}


const tstyle = new ol.style.Style({
  text: new Text({
    font: 'bold 11px "Open Sans", "Arial Unicode MS", "sans-serif"',
    placement: 'line',
    fill: new ol.style.Fill({
      color: 'white',
    }),
  }),
});



//var styles = {
//	  'varde_vatten': [new ol.style.Style({
//		    fill: new ol.style.Fill({
//	          color: 'rgba(100, 149, 237, 0.2)'
//	        }),
//	        stroke: new ol.style.Stroke({
//	          color: '#6495ED',
//	          width: 2
//	        }),
//	        image: new ol.style.Circle({
//	          radius: 7,
//	          fill: new ol.style.Fill({
//	            color: '#6495ED'
//	          })
//	        })
//	    
//	  })]
//};

