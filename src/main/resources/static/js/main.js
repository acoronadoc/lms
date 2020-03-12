
function onSignIn(googleUser) {
	
	var profile = googleUser.getBasicProfile();
	
	partHeaders["Authorization-Type"]="google";
	partHeaders["Authorization"]=googleUser.getAuthResponse().id_token;

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

	if ( partHeaders["Authorization-Type"]=="dev" ) {
		execute( "/api/logout" );
		
		return;
		}
	
	var auth2 = gapi.auth2.getAuthInstance();
	auth2.signOut().then(function () {
		execute( "/api/logout" );
		});
	}

function devLogin(event) {
	event.preventDefault();
	
	partHeaders["Authorization-Type"]="dev";
	partHeaders["Authorization"]="developer@dev";
	
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

/* Syllabus */
function openSyllabus(event, index, courseId) {
	
	if ( event.currentTarget.parentElement.classList.contains('item-selected') ) {
		event.preventDefault();
		event.currentTarget.parentElement.classList.remove('item-selected');
		return;
		}
	
	document.querySelectorAll('.course-syllabus-item-content').forEach(function(currentValue, indice, array){
	    currentValue.innerHTML="";
	    if (currentValue.parentElement) currentValue.parentElement.classList.remove('item-selected');
	    });

	event.currentTarget.parentElement.classList.add('item-selected');

	loadContent( event, "showsyllabuspart", { 'part': index, 'courseId': courseId } );
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