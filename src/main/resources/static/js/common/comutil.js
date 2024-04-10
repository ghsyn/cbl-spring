let CMUTIL_CMUTIL_EMPTY_ALL = "ALL";
let EMPTY_NONE = "NONE";

let m_dlg = null;
let m_mask = null;

function gf_PostOpen(url, options, params, callback, maskOn) {
    // header.jsp에 전역변수 설정
    // g_aDelay 톰캣서버 web.xml에 세션 타임아웃 시간
    // g_aTime 세션종료 예측 시간 = 현재시간  + 세션 타임아웃 시간
    let g_aTime = Date.now() + g_aDelay;

    let nm = "dlg_" + $.now();
    // 팝업을 가운데로
    //let obj = eval("({" + options.replace(/=/gi, ":") + "})");

    let arr = options.split(",");
    let json = "{";
    let cmm = "";

    for( let i=0; i<arr.length; i++ ) {
        let dt = arr[i].split("=");
        json += cmm+'"'+dt[0]+'":"'+dt[1]+'"';
        cmm = ",";
    }
    json += "}";

    let obj = JSON.parse(json);


    if (gf_IsNull(obj.left) && gf_IsNull(obj.right)) {
        if (!gf_IsNull(obj.width) && !gf_IsNull(obj.height)) {
            let left = (screen.width - obj.width) / 2;
            let top = (screen.height - obj.height) / 2;
            options += ",left=" + left + ",top=" + top;
        }
    }

    m_dlg = window.open("", nm, options);
    g_aPopWin.push(m_dlg);

    let frm = document.createElement("form");
    let inp = document.createElement("input");

    $(frm).append(inp);
    inp.type = "hidden";
    inp.name = "params";
    inp.value = JSON.stringify(params);

    $("body").append(frm);
    frm.action = url;
    frm.target = nm;
    frm.method = "post";
    addCsrfHiddenInputTag(frm);
    frm.submit();

    $(frm).detach();

    if (maskOn)
        gf_MaskOn();

    return m_dlg;
}

function gf_MaskOn() {
    m_mask = document.createElement("div");
    m_mask.id = "mask";
    $(m_mask).css({
        "position": "absolute"
        , "left": 0
        , "top": 0
        , "z-index": 999999
        , "background-color": "rgba(0,0,0,0.5)"
        , "width": $(window).width()
        , "height": $(document).height()
    });
    $("body").append(m_mask);
    $(m_mask).fadeIn("slow");
    //$( m_mask ).fadeIn("slow",0.4);

    // wait mask
    //var img = document.createElement("img");
    //img.src = "../../resources/images/wait.gif";
    //console.log( window.innerWidth );
    //$(img).css( {"width":64, "height":64, "position":"absolute", "left":window.innerWidth / 2 - 32, "top":window.innerHeight / 2 - 32 } );
    //$(m_mask).append( img );

    let timer = setInterval(function () {
        if (!timer) return;

        if (m_dlg.closed) {
            clearInterval(timer);
            $(m_mask).detach();
        }
    }, 500);
}


//wait 이미지 보이기
function gf_MaskOnLoading() {
    m_mask = document.createElement("div");
    m_mask.id = "mask";
    $(m_mask).css({
        "position": "absolute"
        , "left": 0
        , "top": 0
        , "z-index": 999999
        , "background-color": "rgba(0,0,0,0.5)"
        , "width": $(window).width()
        , "height": $(document).height()
    });
    $("body").append(m_mask);
    $(m_mask).fadeIn("slow");

    // wait mask
    let img = document.createElement("img");
    img.src = "../../resources/images/wait.gif";
    $(img).css({
        "width": 64,
        "height": 64,
        "position": "absolute",
        "left": window.innerWidth / 2 - 32,
        "top": window.innerHeight / 2 - 32
    });
    $(m_mask).append(img);
}

//wait 이미지 닫기
function gf_MaskOffLoading() {
    $(m_mask).detach();
}

function gf_IsNull(arg) {
    if (typeof (arg) == undefined) return true;
    if (arg == null || arg == "") return true;

    return false;
}

function gf_NullToEmpty(arg) {
    if (gf_IsNull(arg) || arg == "null") return "";
    return arg;
}

function gf_DispInfo(info) {
    for (let key in info) {
        $("#" + key).html(info[key]);
    }
}

function gf_DispInfo2(info) {
    for (let key in info) {
        $("#" + key).html(gf_Comma(info[key]));
    }
}

function gf_SetInfo(info) {
    for (let key in info) {
        $("#" + key).val(info[key]);
    }
}

function gf_SetReadOnly(info) {
    for (let key in info) {
        $("#" + key).attr('readonly', true);
        //$("#"+key).attr( 'Style', "background-color: #e2e2e2;" );
    }
}

function gf_SetInfoByName(info) {
//	Object.keys(info).forEach(key => {
//		$('[name=${key}]').val(info[key]);
//	});
    for (let key in Object.keys(info)) {
        $('[name=${key}]').val(info[key]);
    }
}

// # Select 태그 Option 값 설정
function gf_SetCombo(id, list, emptyOptions, defVal, customFilter) {
    let i, map, cob, opt;
    let empDef = null;

    if (list == null) return;

    cob = $("#" + id);
    cob.html("");

    if (!gf_IsNull(emptyOptions)) {
        opt = document.createElement("option");		// <option></option>
        $(opt).val("");								// <option value=""></option>
        if (emptyOptions == CMUTIL_EMPTY_ALL)
            $(opt).append("전체");					// <option value="">전체</option>
        cob.append(opt);								// <select> 태그에 추가
    }

    for (i = 0; i < list.length; i++) {					// 컨트롤러가 보내준 목록의 개수만큼 반복
        map = list[i];									// 한개 가지고 옴
        if (gf_IsNull(customFilter) || customFilter(map)) {
            if (gf_IsNull(empDef)) empDef = map.commCd;
            opt = document.createElement("option");		// <option></option>
            $(opt).val(map.commCd);						// <option value="commCd값"></option>
            $(opt).append(map.commCdNm);				// <option value="commCd값">commCdNm값</option>
            cob.append(opt);								// 만들어진 option 태그를 select 태그에 추가
        }
    }

    if (!gf_IsNull(defVal))							// 기본값을 전달 받으면
        cob.val(defVal);								// 그 기본값을 기본 선택값으로 한다.
    else if (gf_IsNull(emptyOptions) && list.length > 0) //
        cob.val(empDef);						// 최초 항목을 기본값으로 선택
}

function gf_SetComboCustom(id, list, valNm, txtNm, emptyOptions, defVal, customFilter) {
    let i, map, cob, opt;
    let empDef = null;

    if (list == null) return;

    cob = $("#" + id);
    cob.html("");

    if (!gf_IsNull(emptyOptions)) {
        opt = document.createElement("option");		// <option></option>
        $(opt).val("");								// <option value=""></option>
        if (emptyOptions == CMUTIL_EMPTY_ALL)
            $(opt).append("전체");					// <option value="">전체</option>
        cob.append(opt);								// <select> 태그에 추가
    }

    for (i = 0; i < list.length; i++) {					// 컨트롤러가 보내준 목록의 개수만큼 반복
        map = list[i];									// 한개 가지고 옴
        if (gf_IsNull(customFilter) || customFilter(map)) {
            if (gf_IsNull(empDef)) empDef = map[valNm];
            opt = document.createElement("option");		// <option></option>
            $(opt).val(map[valNm]);						// <option value="commCd값"></option>
            $(opt).append(map[txtNm]);				// <option value="commCd값">commCdNm값</option>
            cob.append(opt);								// 만들어진 option 태그를 select 태그에 추가
        }
    }

    if (!gf_IsNull(defVal))							// 기본값을 전달 받으면
        cob.val(defVal);								// 그 기본값을 기본 선택값으로 한다.
    else if (gf_IsNull(emptyOptions) && list.length > 0) //
        cob.val(empDef);						// 최초 항목을 기본값으로 선택
}

// combo 박스 value 여러개 담기
function gf_SetComboCustomMultiVal(id, list, valNmList, txtNm, emptyOptions, defVal, customerFilter) {
    let i, map, cob, opt;
    let empDef = null;

    if (list == null) return;

    cob = $("#" + id)
    cob.html("");
}

function gf_AllFileDwld(url, param, fileNm) {
    if (!url || !param || !fileNm) {
        alert("전달값이 유효하지 않습니다.");
        return;
    }

    $.ajax({
        url: url
        , data: JSON.stringify(param)
        , type: 'POST'
        , cache: false
        , contentType: 'application/json'
        , xhrFields: {
            responseType: 'blob',
        },
    }).done(function (data, stat, xhr) {
        if (!data) return;
        let blob = new Blob([data], {type: xhr.getResponseHeader('content-type')});
        fileNm = decodeURI(fileNm);
        // IE
        if (window.navigator.msSaveOrOpenBlob)
            window.navigator.msSaveOrOpenBlob(blob, fileNm);
        // Other
        let link = document.createElement('a');
        let url = window.URL.createObjectURL(blob);
        link.href = url;
        link.target = '_self';

        if (fileNm) link.download = fileNm;
        document.body.append(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
    });
}

function gf_SetChecked(name) {

}

String.prototype.string = function (len) {
    let s = '', i = 0;
    while (i++ < len) {
        s += this;
    }
    return s;
};
String.prototype.zf = function (len) {
    return "0".string(len - this.length) + this;
};
Number.prototype.zf = function (len) {
    return this.toString().zf(len);
};

Date.prototype.format = function (f) {
    if (!this.valueOf()) return " ";

    let weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    let d = this;

    return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function ($1) {
        switch ($1) {
            case "yyyy":
                return d.getFullYear();
            case "yy":
                return (d.getFullYear() % 1000).zf(2);
            case "MM":
                return (d.getMonth() + 1).zf(2);
            case "dd":
                return d.getDate().zf(2);
            case "E":
                return weekName[d.getDay()];
            case "HH":
                return d.getHours().zf(2);
            case "hh":
                return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case "mm":
                return d.getMinutes().zf(2);
            case "ss":
                return d.getSeconds().zf(2);
            case "a/p":
                return d.getHours() < 12 ? "오전" : "오후";
            default:
                return $1;
        }
    });
};

// # 숫자 3자리마다 콤마를 추가한다.
function gf_Comma(src) {
    if (gf_IsNull(src)) return src;
    return src.toLocaleString();
}

// # 소수점 자리수 지정해서 콤마 추가한다.
function gf_Fix(value, loc) {
    if (gf_IsNull(value)) return value;

    return gf_Comma(Number(Number(value).toFixed(loc)));
}

// ### 그리드 선택 관련 함수 ####################

//# 그리드에 체크 컬럼을 추가한다.
function gf_addSelectColumn(grid, list) {
    let i;

    grid.selRowNum = -1;
    for (i = 0; i < list.length; i++)
        list[i].chk = "0";
}

//# 그리드 클릭 시 선택 처리한다.
function gf_selectRow(grid, rowNum) {
    if (grid.selRowNum >= 0)
        grid.setValue(grid.selRowNum, "chk", "0");
    grid.setValue(rowNum, "chk", "1");
    grid.selRowNum = rowNum;
}

//# 그리드 클릭시 표시하는 체크 포메터
function gf_dispCheckRow(value, rowData) {
    if (value == "1")
        return "<image src='../../resources/images/icon_check.png' style='width:13px; height:13px; '/>";
    return "";
}

// # 그리드 체크된 것만 펼침
function gf_updateFavoriteTree(gTreeGrid, favData, calType) {
    let i = 0, j = 0, analId;
    let all = gTreeGrid.getData();
    let favAnalMnos = favData.analMno.split(',');

    if (favData.favType > 1 && calType != null)     // 시보의 경우, calType(평균,변화량...) 등을 설정
        $("#" + calType).val(favData.calType);

    gTreeGrid.collapseAll();
    gTreeGrid.uncheckAll();

    for (i in favAnalMnos) {
        analId = parseInt(favAnalMnos[i]);
        for (; j < all.length; j++) {
            if (all[j].idx == analId) {
                // gTreeGrid.expand(gTreeGrid.getParentRow(j).rowKey);
                gTreeGrid.check(j);
                break;
            }
        }
    }
    checkRows = gTreeGrid.getCheckedRows();
    for (i = 0; i < checkRows.length; i++) {
        gTreeGrid.expand(checkRows[i].rowKey);
        ancestor = gTreeGrid.getAncestorRows(checkRows[i].rowKey);
        for (j = 0; j < ancestor.length; j++)
            gTreeGrid.expand(ancestor[j].rowKey);
    }
}

// # tui grid 생성 함수
// # id : 그리드 생성 구역 id
// # cols : 컬럼 정보 setting
// # option : tui 옵션
// # data : 그리드에 넣을 data 변수
function gf_setTuiGrid(id, cols, options, data) {

    let grid = new tui.Grid({
        el: $('#' + id)
        /* , options*/
        , columns: cols
        , data: data
    });

    return grid;
}

function gf_SetTimePicker(id) {
    return new tui.TimePicker(
        "#" + id, {
            initialHour: 0,
            initialMinute: 0,
            format: 'H:m',
            inputType: 'selectbox',
            showMeridiem: false
        }
    );
}

//# 파일 업로드
function gf_FileUpload(fd, callback, errorCallback) {
    if (!fd) return null;
    // # 데이터 전송
    $.ajax({
        url: "/kps/cmm/file/upload"
        , type: "POST"
        , data: fd
        , dataType: "text"
        , processData: false
        , contentType: false
        , success: function (ret) {
            ret = JSON.parse(ret);
            if (ret.result === "FAIL") {
                //alert("업로드중 오류가 발생했습니다.");
                if (errorCallback) errorCallback(ret);
            } else {
                //alert("파일을 성공적으로 업로드 했습니다.");
                if (callback) callback(ret);
            }
        }, error: function (xhr, status, err) {
            alert("업로드중 오류가 발생했습니다.");
        }
    });
}

function gf_FileDelete(params, success, error) {
    if (!params) return null;
    $.ajax({
        url: "/kps/cmm/file/delete"
        , type: "POST"
        , data: JSON.stringify(params)
        , dataType: "json"
        , processData: false
        , contentType: "application/json"
        , success: function (ret) {
            ret = JSON.parse(JSON.stringify(ret));
            if (ret.result === "FAIL") {
                if (error) error(ret);
            } else {
                if (success) success(ret);
            }
        }, error: function (xhr, status, err) {
            alert("파일삭제중 오류가 발생했습니다.");
        }
    });
}

function gf_FileRecovery(params, success, error) {
    if (!params) return;
    $.ajax({
        url: "/kps/cmm/file/recvry"
        , type: "POST"
        , data: JSON.stringify(params)
        , dataType: "json"
        , processData: false
        , contentType: "application/json"
        , success: function (ret) {
            ret = JSON.parse(JSON.stringify(ret));
            if (ret.result === "FAIL") {
                if (error) error(ret);
            } else {
                if (success) success(ret);
            }
        }, error: function (xhr, status, err) {
            alert("파일복원중 오류가 발생했습니다.");
        }
    });
}

function gf_FileGrpIdByFileList(params, callback, errCallback) {
    if (!params) return;
    $.ajax({
        url: "/kps/cmm/file/getFileGrpList"
        , type: "POST"
        , data: JSON.stringify(params)
        , dataType: "json"
        , processData: false
        , contentType: "application/json"
        , success: function (ret) {
            if (ret.result === "FAIL") {
                if (errCallback) errCallback(ret);
            } else {
                if (callback) callback(ret);
            }
        }, error: function (xhr, status, err) {
            alert("파일 목록을 불러오는중 오류가 발생했습니다.");
        }
    });
}

function gf_AllFileDwld(url, param, fileNm) {
    if (!url || !param || !fileNm) {
        alert("전달값이 유효하지 않습니다.");
        return;
    }

    $.ajax({
        url: url
        , data: JSON.stringify(param)
        , type: 'POST'
        , cache: false
        , contentType: 'application/json'
        , xhrFields: {
            responseType: 'blob',
        },
    }).done(function (data, stat, xhr) {
        if (!data) return;
        let blob = new Blob([data], {type: xhr.getResponseHeader('content-type')});
        fileNm = decodeURI(fileNm);
        // IE
        if (window.navigator.msSaveOrOpenBlob)
            window.navigator.msSaveOrOpenBlob(blob, fileNm);
        // Other
        let link = document.createElement('a');
        let url = window.URL.createObjectURL(blob);
        link.href = url;
        link.target = '_self';

        if (fileNm) link.download = fileNm;
        document.body.append(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
    });
}

function gf_required(varNm, txtNm) {
    return [varNm, txtNm + "은(는) 필수 입력값입니다.", new Function("varName", "return this[varName];")];
}

function gf_maxlength(varNm, txtNm, maxLen) {
    return [varNm, txtNm + "은(는) " + maxLen + "자 이상 입력할수 없습니다.", new Function("varName", "this.maxlength='" + maxLen + "';  return this[varName];")];
}

function gf_validateRequiredCustom(field, oRequired) {

    let isValid = true;
    let focusField = null;
    let i = 0;
    let fields = [];
    let x = 0;

    if ((field.type == 'hidden' ||
        field.type == 'text' ||
        field.type == 'textarea' ||
        field.type == 'file' ||
        field.type == 'radio' ||
        field.type == 'checkbox' ||
        field.type == 'select-one' ||
        field.type == 'password')) {
        let value = '';
        // get field's value
        if (field.type == "select-one") {
            let si = field.selectedIndex;
            if (si >= 0) {
                value = field.options[si].value;
            }
        } else if (field.type == 'radio' || field.type == 'checkbox') {
            if (field.checked) {
                value = field.value;
            }
        } else {
            value = field.value;
        }

        if (trim(value).length == 0) {
            if ((i == 0) && (field.type != 'hidden')) {
                focusField = field;
            }
            fields[i++] = oRequired[1];
            isValid = false;
        }
    } else if (field.type == "select-multiple") {
        let numOptions = field.options.length;
        lastSelected = -1;
        for (loop = numOptions - 1; loop >= 0; loop--) {
            if (field.options[loop].selected) {
                lastSelected = loop;
                value = field.options[loop].value;
                break;
            }
        }
        if (lastSelected < 0 || trim(value).length == 0) {
            if (i == 0) {
                focusField = field;
            }
            fields[i++] = oRequired[1];
            isValid = false;
        }
    } else if ((field.length > 0) && (field[0].type == 'radio' || field[0].type == 'checkbox')) {
        isChecked = -1;
        for (loop = 0; loop < field.length; loop++) {
            if (field[loop].checked) {
                isChecked = loop;
                break; // only one needs to be checked
            }
        }
        if (isChecked < 0) {
            if (i == 0) {
                focusField = field[0];
            }
            fields[i++] = oRequired[1];
            isValid = false;
        }
    }

    if (fields.length > 0) {
        try {
            field.focus();
        } catch (e) {
            console.log(e);
        }
        alert(fields.join('\n'));
    }
    return isValid;
}

function gf_validateMaxLengthCustom(field, oMaxLength) {
    let isValid = true;
    let focusField = null;
    let i = 0;
    let fields = [];

    if (field.type == 'text' || field.type == 'textarea') {
        let iMax = parseInt(oMaxLength[2]("maxlength"));
        if (field.value.length > iMax) {
            if (i == 0) {
                focusField = field;
            }
            fields[i++] = oMaxLength[1];
            isValid = false;
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return isValid;
}


function gf_movePagePost(url, params) {
    let frm = document.createElement("form");

    for (let key in params) {
        let inp = document.createElement("input");
        $(frm).append(inp);
        inp.type = "hidden";
        inp.name = key;
        inp.value = params[key];
    }

    $("body").append(frm);
    frm.action = url;
    frm.method = "post";
    addCsrfHiddenInputTag(frm);
    frm.submit();
    frm.remove();
}

function gf_movePage(url, params) {
    let frm = document.createElement("form");
    let inp = document.createElement("input");

    $(frm).append(inp);
    inp.type = "hidden";
    inp.name = "params";
    inp.value = JSON.stringify(params);

    $("body").append(frm);
    frm.action = url;
    frm.method = "post"
    addCsrfHiddenInputTag(frm);
    frm.submit();
}


//#그리드 스타일 변경 / 디자이너 확인 #품질프로젝트
function gf_setGridDesign() {
    let Grid = tui.Grid;
    Grid.applyTheme('default', {
        grid: {
            background: '#0f0',
            border: '#fff',
            text: '#fff'
        },
        selection: {
            background: '#0f0',
            border: '#000'
        },
        toolbar: {
            border: '#000',
            background: '#fff'
        },
        scrollbar: {
            background: '#fff',
            thumb: '#d3d9e0',
            active: '#c1c1c1'
        },
        cell: {
            normal: {
                background: 'white',	// 2022.12.08 정석인
                border: '#eee',
                text: '#000'
            },
            head: {
                background: '#67affe',
                border: '#67affe',
                text: '#fff'
            },
            editable: {
                background: '#f2f5f8'
            },
            selectedHead: {
                background: '#8fa4bb'
            },
            focused: {
                border: '#424c55'
            },
            disabled: {
                text: '#b0b0b0'
            },
        }
    });

    $('.tui-grid-cell').css('height', '50px');
    $('.tui-grid-cell-header').css('height', '50px !important');
    $('.tui-grid-cell-row-header').css('height', '50px');

    $('.tui-grid-header-area').css('height', '50px');
    $('.tui-grid-cell-header').css('background', 'linear-gradient(180deg, #3576dd, #2c5faf)');
    $('.tui-grid-cell-header').css('color', '#fff');
    $('.tui-grid-cell-header').css('text-indent', '0');

    //checkbox style need for fix
    /*
    $('.tui-grid-container input[type=checkbox]').css('background','#fff');
    $('.tui-grid-container input[type=checkbox]').css('width','18px');
    $('.tui-grid-container input[type=checkbox]').css('height','18px');
    $('.tui-grid-container input[type=checkbox]').css('border-radius','50%');
    $('.tui-grid-container input[type=checkbox]').css('border','1px solid #bcbfc9');
    $('.tui-grid-container input[type=checkbox]').css('appearance','none');

    $('.tui-grid-container input[type=checkbox]:checked').css('background','#67affe');
    $('.tui-grid-container input[type=checkbox]:checked').css('width','18px');
    $('.tui-grid-container input[type=checkbox]:checked').css('height','18px');
    $('.tui-grid-container input[type=checkbox]:checked').css('border-radius','50%');
    $('.tui-grid-container input[type=checkbox]:checked').css('border','1px solid #bcbfc9');
    $('.tui-grid-container input[type=checkbox]:checked').css('appearance','none');
    */
    //$('.tui-grid-cell').css('background-color','#fff !important');

    //$('.tui-pagination .tui-last-child.tui-is-selected').css('border-radius','50%')
    $('.tui-pagination .tui-last-child.tui-is-selected').css('background', '#184394')


    /*
        $('.tui-grid-body-area').css('background-color','#fff');
        $('.tui-grid-head-area').css('border-color','#b9c6d4');
        $('.tui-grid-border-line-top').css('background-color','#b9c6d4');
        $('.tui-grid-border-line-bottom').css('background-color','#fff');
        $('.tui-grid-no-scroll-x').css('background-color','#46494b');

        $('.tui-grid-scrollbar-right-top').css('background-color','#fff');
        $('.tui-grid-scrollbar-right-top').css('border-color','#fff');
        $('.tui-grid-scrollbar-y-inner-border').css('background-color','#fff');
        $('.tui-grid-scrollbar-y-outer-border').css('background-color','#fff');
        $('.tui-grid-scrollbar-right-bottom').css('background-color','#fff');
        $('.tui-grid-scrollbar-right-bottom').css('border-color','#fff');

        $('.tui-grid-layer-state').css('background-color','#fff');
        $('.tui-grid-layer-state-content').css('color','#000');
        $('.tui-grid-scrollbar-frozen-border').css('border-color','#d3d9e0');
    */
}


function serializeObject(form) {
    let data = form.serializeObject();
    let formData = {};

    function setKeyValue(ret, key, value) {
        let path = key.split(/[\[\].]/).filter(function (n) {
            return n
        });
        let r = ret;
        for (let i = 0; i < path.length; i++) {
            let subkey = path[i];
            if (i !== path.length - 1) {
                if (!r[subkey])
                    r[subkey] = /^\d+$/.test(path[i + 1]) ? [] : {};
                r = r[subkey];
                continue;
            }
            r[subkey] = value;
        }
    }

    for (let key in data) {
        setKeyValue(formData, key, data[key]);
    }
    return formData;
}

// 2022.12.07 정석인 그리드 선택 색상 표시 mousedown 에서 호출
function gf_setSelRowCo(g, row) {
    setTimeout(function () {
        g.setSelectionRange({
            start: [row, 0]
            , end: [row, g.getColumns().length]
        });
    }, 1000);
}

// 2022.12.07 정석인 그리드 선택 색상 표시 mousedown 에서 호출
let selRow = null;

function gf_setSelRowCo(g, row) {
    //g.setSelectionRange({
    //	start:[row, 0]
    //	,end:[row, g.getColumns().length]
    //});
    if (selRow >= 0)
        g.removeRowClassName(selRow, 'gridSelRowColor');
    selRow = row;
    g.addRowClassName(selRow, 'gridSelRowColor');
}

function ButtonRenderer(props) {
    this.constructor(props);
}

ButtonRenderer.prototype = {
    constructor: function (props) {
        const el = document.createElement('input');
        const text = props.columnInfo.renderer.options.text;
        const click = props.columnInfo.renderer.options.click;
        const classes = props.columnInfo.renderer.options.classNm;

        el.type = 'button';
        $(el).addClass('subInputBtn');
        if(classes){
            $(el).addClass(classes);
        }
        el.value = text;
        this.rowKey = props.rowKey;
        if (click) {
            let that = this;
            $(el).click(function (ev) {
                ev.rowKey = that.rowKey;
                ev.stopPropagation();
                click(ev);
            });
        }
        this.el = el;
    },
    getElement: function () {
        return this.el;
    }
}

function gf_FileSizeCheck(size) {
    let maxSize = 1024 * 1024 * 50;
    if (size > maxSize) {
        alert("파일 업로드 사이즈를 초과 하였습니다.");
        return false;
    }
    return true;
}

// ### 2022.12.13 정석인 파일 관련 공통 함수 추가 ###

function gf_makeUUid(){
    let randomValuesArray = new Uint8Array(10);
    let crypto = window.crypto || window.msCrypto;
    crypto.getRandomValues(randomValuesArray);

    let outStr = "";
    for (let i = 0; i < randomValuesArray.length; i++) {
        if (i > 0) outStr += ",";
        outStr += randomValuesArray[i];
    }
    return outStr;
    //return Math.random().toString(36).substr(2)+(new Date()).getTime().toString(36);
}

function gf_makeFileHtml(fileId, fileNm, downFlag, delFlag, style ) {
	var str = "<div id='"+gf_makeUUid()+"' name='fileItem' style='"+style+"' >"; 
	str += "<img src='../../resources/images/file_icon.png' />";
	if( downFlag )  
		str += "<a href='#' style='text-decoration: underline;' onclick='gf_downloadFileOne(\""+fileId+"\");'>" + fileNm + "</a>";
	else
		str += fileNm;
	str += "<input  type='hidden' value='"+ fileId +"' name='fileId'/>";
	str += "<input  type='hidden' value='"+ fileNm +"' name='fileNm'/>";
	if( delFlag )
		str += "<input class='subInputBtn delete' type='button' name='btnFileDel' id='btnFileDel_"+fileId+"' value='삭제'/>";
	str += "</div>"
	return str;	
}

function gf_makeDownHtml(fileId, fileNm, style) {
	var str = "<div style='"+style+"'>" +
	    "<img src='../../resources/images/file_icon.png' />" +
	    "<a href='#' style='text-decoration: underline;' onclick='gf_downloadFileOne(\""+fileId+"\");'>" + fileNm + "</a>" +
	    "</div>";
	return str;
}

function gf_downloadFileOne(fid) {
	if(fid != "" ) {
		if( confirm("선택하신 파일을 다운로드하시겠습니까?") )
			window.location.assign("/kps/cmm/file/dwld?fileId="+fid);
	}
	else
		alert("파일 아이디가 존재하지 않습니다.");
}

function gf_fileDel(e) {
    let grpId= $("#"+$(this).parent().parent().attr("id") +" > input[name='fileGrpId']").val();
    let fileId = $("#"+$(this).parent().attr("id")+" > input[name='fileId']").val();

    if(confirm("선택하신 파일을 삭제하시겠습니까? \n파일 삭제는 [취소] 버튼을 눌러도 되돌릴 수 없습니다.")){
        gf_FileDelete({"fileId": fileId, "fileGrpId":grpId}, function (ret){alert("파일을 삭제했습니다.")},function (err){alert("파일삭제에 실패했습니다.")} );
        $(this).parent().remove();
    }else
        alert("파일삭제가 취소되엇습니다.");
}

// ### 2022.12.26 정석인 디자인 수정한 파일 관련 함수 추가 ###
function gf_makeFileFrame(num, dmndId, docNm, style) {
	let html = "<tr>" 
		+ "<th id='"+dmndId+"'>"+docNm+"</th>" 
		+ "<td>" 
		+ "<div id='r"+num+"' name='uploadArea'>" 
        + "<input type='hidden' value='"+dmndId+"' name='fileDmndListId'/>"
		+ "<input  type='hidden' value='' name='fileGrpId'/>" 
        + "<input type='hidden' value='"+docNm+"' name='reqDocNm'/>"
		+ "<input class='subInputBtn upload' type='button' value='파일 업로드' id='btnFind_"+dmndId+"' name='btnFind' for='files' style='margin-bottom:2px;'/>" 
		+ "<input type='file' id='file_"+dmndId+"' name='files' style='display: none'  />" 
		+ "<table id='tb"+dmndId+"' cellspacing='0' cellpadding='0' style='width: auto; border: 1px solid white; border-collapse: collapse; border-spacing: 0; padding: 0;"+style+"' class='tbl_file'>"
		+ "</table>"
		+ "</div>" 
		+ "</td>" 
		+ "</tr>";
	return html;
}

function gf_fileOnChange(obj, evt, downFlag, delFlag, userId, entId) {
	evt.preventDefault();
	let fileArea ="";
	let parent = "#"+$(obj).parent().attr("id");
	let dmndId = $(parent+" > input[name='fileDmndListId']").val();

	let frm = new FormData();
	frm.append("file", evt.target.files[0]);
	let grpId = $(parent+" > input[name='fileGrpId']").val()
	if( grpId == "undefined" ) grpId = "";
	frm.append("fileGrpId", grpId);
	frm.append("fileDwndListId", dmndId);
	frm.append("userId", userId);
	frm.append("entId", entId);
	
	// 2022.12.13 정석인 파일 목록 수정
	gf_FileUpload(frm, function(ret){
			fileArea += gf_makeFileHtml2( ret.data.fileGrpId, ret.data.fileId, ret.data.fileNm, downFlag, delFlag );
			$(parent+" > input[name='fileGrpId']").val(ret.data.fileGrpId);
			$("#tb"+dmndId).append( fileArea );

		}, function (err){
			alert("파일 업로드중 오류가 발생했습니다.");

		});
	alert("파일 업로드를 완료했습니다.");
}

function gf_makeFileHtml2(fileGrpId, fileId, fileNm, downFlag, delFlag ) {
	let str = "<tr>"
		+ "<td id='"+gf_makeUUid()+"' name='fileItem' style='height: 30px;'>"
		+ "<img src='../../resources/images/file_icon.png' />"; 
	if( downFlag )  
		str += "<a href='#' style='text-decoration: underline;' onclick='gf_downloadFileOne(\""+fileId+"\");'>" + fileNm + "</a>";
	else
		str += fileNm;
	str += "<input  type='hidden' value='"+ fileId +"' name='fileId'/>" 
		+ "<input  type='hidden' value='"+ fileNm +"' name='fileNm'/>" 
		+ "</td>"
		+ "<td style='height: 30px;'>";
	if( delFlag ) str += "<input class='subInputBtn delete' type='button' name='btnFileDel' id='btnFileDel_"+fileId+"' value='삭제' "
		+ "    onclick='gf_fileDel2(this, \""+fileGrpId+"\", \""+fileId+"\");'/>";
	str += "</td>"
		+ "</tr>"; 
	return str;	
}

function gf_fileDel2(btn, grpId, fileId) {
    if(confirm("선택하신 파일을 삭제하시겠습니까? \n파일 삭제는 [취소] 버튼을 눌러도 되돌릴 수 없습니다.")){
        gf_FileDelete({"fileId": fileId, "fileGrpId":grpId}, function (ret){alert("파일을 삭제했습니다.")},function (err){alert("파일삭제에 실패했습니다.")} );
        $(btn).parent().parent().remove();
    }else
        alert("파일삭제가 취소되엇습니다.");
}



/* 221228 추가 */
  $(function(){
     // tui-grid-rside-area width값 지정
    $(".tui-grid-rside-area").each(function(){
      var lwidth = $(this).siblings(".tui-grid-lside-area").width();
      $(this).css("width","calc(100% - "+lwidth+"px)");
    });

    // td 안에 table이 바로 있는경우 td의 padding값 변경
    $('.detailTable01 td').each(function(){
        if ( $(this).children("table").length > 0 ) {
            $(this).addClass("tbl_in");
        }
    });

    // th 안에 contentTitle이 바로 있는경우 th의 padding값 변경
    $('.detailTable01 th').each(function(){
        if ( $(this).children(".contentTitle").length > 0 ) {
            $(this).addClass("tbl_in");
            $(this).parent().next("tr").children("td").addClass("tbl_in")
        }
    });

    // contentsArea에 bottomBtnLayer가 없을 경우 강제로 추가
    /*if ( $(".contentsArea").children(".bottomBtnLayer").length == 0 ) {
        $(".contentsArea").append("<div class='bottomBtnLayer'></div>")
    }*/

    // input file
	$(document).on('change', ".filebox input[type='file']", function () {
		var fileName = $(this).val();
		$(this).siblings(".file_input").val(fileName);
	});

  })

function addCsrfHiddenInputTag(frm) {
    let inp = document.createElement("input");
    inp.type = "hidden";
    inp.name = $("meta[name='_csrf_parameter_name']").attr("content");
    inp.value = $("meta[name='_csrf']").attr("content");
    $(frm).append(inp);
}