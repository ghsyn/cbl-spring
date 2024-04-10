
function readFile(e) {
    console.log(e)
    let file = e.files[0];
    if(!file) return;

    const reader = new FileReader();

    reader.onload = function (event) {
        const content = event.target.result;
        const extension = getFileExtension(file.name);

        // 파일 확장자에 따라 처리 분기
        switch (extension.toLowerCase()) {
            case 'txt':
                //procTxt(content);
                txt2Json(content.split('\r\n'));
                break;
            case 'csv':
                //procCsv(content);
                csv2Json(content.split('\r\n'), 14);
                break;
            default:
                alert('처리되지 않은 파일 확장자 입니다.');
        }
    };
    reader.onerror = function(err){
        alert("파일을 읽는중 오류가 발생했습니다.");
    };
    reader.readAsText(file);
}

/**
* @comment: 파일명에서 확장자 추출
* @param: filename
* @return: str
* */
function getFileExtension(fileName) {
    return fileName.split('.').pop();
}

/**
* @comment: txt 파일 읽기를 처리
* @params: txt 파일을 읽어들인 데이터
* @return: list
* */
function procTxt(content) {
    txt2Json(content.split('\r\n'));
}

/**
* @comment: csv 파일 읽기를 처리
* @param: 읽어들인 데이터
* @return: {'key':[], 'key2':[],....} 형태의 map
* */
function procCsv(content) {
    csv2Json(content.split('\r\n') ,14)
}

/**
* @comment: txt파일을 읽어들여 []형태의 데이터로 return
* @param: list
* @return: list
* */
function txt2Json(data){
    if(!data) return;
    let result = [];
    for(let n of data)
        result.push(parseDynamicType(n));
    console.log(result)
    return result;
}

/**
* @comment: csv파일을 map형태로 parsing
* @param:list
* @return: map
* */
function csv2Json(data, index){
    if(!data) return;
    let result = {};
    index = ((!index) ? 0 : index); // index 값이 없을 경우 0,
    let tmp = data.slice(index, data.length);
    tmp[0].split('\t').forEach(function(key, index){
        if(key !== null || key !== '')
            result[key] = tmp.slice(1).map(function (row) {
                return parseDynamicType(row.split('\t')[index]);
            });
    });
    console.log(result)
    return result;
}

/**
 * @comment: data 형식에 맞게 데이터 parsing 함수
 * @param: 데이터
 * @return: casting 데이터
 * */
function parseDynamicType(value) {
    // 동적으로 데이터 타입을 인식하여 파싱
    if (value !== undefined) {
        if (!isNaN(value) && value !== "") {// 숫자인 경우
            return parseFloat(value);
        } else if (value.toLowerCase() === 'true' || value.toLowerCase() === 'false') {
            return value.toLowerCase() === 'true'; // bool
        } else {
            return value; // 문자열로 처리
        }
    } else {
        // 값이 undefined인 경우 처리
        return null; // 또는 다른 적절한 값으로 처리
    }
}
