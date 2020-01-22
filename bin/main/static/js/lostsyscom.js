var partScripts={};

function execute( url, reg, onSuccess ) {
	var post=JSON.stringify(reg);

	fetch( url, {
		  method: 'POST', 
		  body: post,
		  headers: {
		      'Accept': 'application/json',
		      'Content-Type': 'application/json'
		    }		  
		} ).then(function(response) {
			response.json().then(function(p) {
				if ( !onSuccess )
					render( p );
				else
					onSuccess( p );
	        	});
	    });		
	}

function render( p ) {

	if ( p.parts )
		for ( var i=0; i<p.parts.length; i++ ) {
			
			if ( partScripts[ p.parts[i].action ] )
				partScripts[ p.parts[i].action ]( p.parts[i] );
			
			}	
	
	}

partScripts.html=function(p) {
	document.querySelector( p.selector ).innerHTML= p.content;
	}

partScripts.script=function(p) {
	eval( p.content );
	}