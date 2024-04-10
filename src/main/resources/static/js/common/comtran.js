let ERR_NONE = "NONE";
let ERR_CONSOLE = "CONSOLE";
let ERR_ALERT = "ALERT";
let ERR_CALLBACK = "CALLBACK";
let gv_ErrDisp = ERR_CALLBACK;


(function($){
  $.fn.serializeObject = function () {
    "use strict";

    let result = {};
    let extend = function (i, element) {
    let node = result[element.name];

      if ('undefined' !== typeof node && node !== null) {
        if ($.isArray(node)) {
          node.push(element.value);
        } else {
          result[element.name] = [node, element.value];
        }
      } else {
        result[element.name] = element.value;
      }
    };

    $.each(this.serializeArray(), extend);
    return result;
  };
})(jQuery);

function gf_formToJson( frm )
{
	var arr = frm.serializeArray();
    var ret = {};
    for (var i = 0; i < arr.length; i++){
        ret[arr[i]['name']] = arr[i]['value'];
    }
    return ret;	
}

function gf_Transaction(id, url, httpMethod, params, datas, callback, statusCode) {

  if( params != null ) {
	  params.sendaddr = paramTest( params );
  }
	
  $.ajax({
    url: url,
    contentType: 'application/json; charset=utf-8',
    type: httpMethod,
    data: JSON.stringify(params),
    dataType: 'json',
    success: function(ret) {
    	if (callback != null) {
       		callback( id, ret );
    	}
    },
    statusCode: statusCode,
    error: function (XMLHttpRequest, textStatus, errorThrown) {
    	if( gv_ErrDisp == ERR_ALERT )
    		alert("오류가 발생하였습니다. 관리자에게 문의 바랍니다.");
    	else if( gv_ErrDisp == ERR_CONSOLE ) {
    		console.log("AJAX 오류 ===================");
    		console.log( textStatus );
			console.log( errorThrown );
    	}
    	else if( gv_ErrDisp == ERR_CALLBACK ) {
				var ret = { "result":"FAIL", "msg":textStatus };
				callback( id, ret );
			}
    }
  });

  // header.jsp에 전역변수 설정
  // g_aDelay 톰캣서버 web.xml에 세션 타임아웃 시간
  // g_aTime 세션종료 예측 시간 = 현재시간 + 세션 타임아웃 시간
  // g_aTime = Date.now() + g_aDelay;

}

function gf_TransactionMultiForm(id, url, httpMethod, params, datas, callback, statusCode) {
	  if( params != null ) {
		  params.sendaddr = paramTest( params );;
	  }

	  $.ajax({
	    url: url,
	    enctype : "multipart/form-data",
	    type: httpMethod,
	    data: params,
	    dataType: 'json',
        processData : false,
        contentType : false,
	    success: function(ret) {
	    	
	    	if (callback != null) {
	    		console.log("responseBody 결과 : "+ret);
	       		callback( id, ret );
	    	}
	    },
	    statusCode: statusCode,
	    error: function (XMLHttpRequest, textStatus, errorThrown) {
	    	if( gv_ErrDisp == ERR_ALERT )
	    		alert("오류가 발생하였습니다. 관리자에게 문의 바랍니다.");
	    	else if( gv_ErrDisp == ERR_CONSOLE ) {
	    		console.log("AJAX 오류 ===================");
	    		console.log( textStatus );
				console.log( errorThrown );
	    	}
	    	else if( gv_ErrDisp == ERR_CALLBACK ) {
					var ret = { "result":"FAIL", "msg":textStatus };
					callback( id, ret );
				}
	    }
	  });
}

function gf_TransPostSubmit(path, params, method) {
	let form = document.createElement('form');
	form.method = method;
	form.action = path;
	for (let key in params) {
		if (params.hasOwnProperty(key)) {
			let hiddenField = document.createElement('input');
			hiddenField.type = 'hidden';
			hiddenField.name = key;
			hiddenField.value = params[key];
			form.appendChild(hiddenField);
		}
	}
	document.body.appendChild(form);
	addCsrfHiddenInputTag(form);
	form.submit();
}


let g_listSit = null;
let g_callbackOut = null;

function gf_selectSitInf(callback)
{
	if( typeof(callback) != "undefined" && callback != null )
		g_callbackOut = callback;
	gf_Transaction( "EMS_SIT_INF", "/ems/main/selectSitInf", "GET", null, null, gf_callbackSitInf );
}

function gf_callbackSitInf(id, ret)
{
	var i;
		
	g_listSit = ret.listSit;
	for( i=0; i<g_listSit.length; i++ ) {
		$("#sitNm"+g_listSit[i]["mno"]).val(g_listSit[i]["sitNm"]);
		$("#sitNm"+g_listSit[i]["mno"]).html(g_listSit[i]["sitNm"]);
		$("#menuSitNm"+g_listSit[i]["mno"]).html(g_listSit[i]["sitNm"]);
	}
	if( g_callbackOut != null )
		g_callbackOut( ret );
}

function paramTest( params ) {
	let keys = Object.keys( params );
	let i, j, m;
	
	for( i=0; i<keys.length-1; i++) {
		m = i;
		for( j=i+1; j<keys.length; j++ ) {
			if( keys[m]>keys[j] )
				m = j;
		}
		if( m != i ) {
			var tmp = keys[i];
			keys[i] = keys[m];
			keys[m] = tmp;
		}
	}
	
	
	let s = g_enc;
	for( i=0; i<keys.length; i++ )
		s += params[keys[i]];
	let enc = SHA256(s);
	
	// console.log("key = "+s);
	// console.log("enc = "+enc);
	return enc;
}


function enc(data, key){
	let encryptedData = CryptoJS.AES.encrypt(JSON.stringify(data), key, {
		mode: CryptoJS.mode.ECB,
		padding: CryptoJS.pad.Pkcs7
	});
	return encryptedData.toString();
}

function dec(data, key){

}
