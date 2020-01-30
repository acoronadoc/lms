var logininfo = null;

function onSignIn(googleUser) {
	
	var profile = googleUser.getBasicProfile();

	execute( "/api/login", { 
		'mail': profile.getEmail(),
		'name': profile.getName(),
		'img': profile.getImageUrl(),
		'idtoken': googleUser.getAuthResponse().id_token

		} 
	);
	
	}

function signOut(event) {
	event.preventDefault();

	if ( !logininfo ) return;
	
	if ( !logininfo.idtoken ) {
		execute( "/api/logout" );
		
		return;
		}
	
	var auth2 = gapi.auth2.getAuthInstance();
	auth2.signOut().then(function () {
		execute( "/api/logout" );
		
		logininfo = null;
		});
	}

function devLogin(event) {
	event.preventDefault();
	
	execute( "/api/login", { 
			'mail': 'developer@dev',
			'name': 'John Developer',
			'img': '/img/anonymous-user.png'
			} 
		);
	
	}

function loadContent(event,content, other) {
	event.preventDefault();
	
	var p={ 
			'logininfo': logininfo,
			'content': content
		};

	if ( other ) p.other=other;
	
	execute( "/api/content", p );	
	}

/* Responsive */
function openHamb() {
	if ( !document.querySelector('.right-bar-1') ) return;
	
	document.querySelector('.right-bar-1').classList.add('hambOpen');
	document.body.innerHTML+="<a class='abg' href='#' onclick='javascript: closeHamb();'><div class='bg'></div></a>";
	}

function closeHamb() {
	document.querySelector('.right-bar-1').classList.remove('hambOpen');
	
	document.body.removeChild( document.querySelector('.abg') );
	
	}