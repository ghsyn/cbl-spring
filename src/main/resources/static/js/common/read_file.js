let data = null;

function drawChart(data){
    createChart('chart1', data,  'Timestamp (HH:MM:SS)', document.querySelector("input[name='q1']:checked").value,  document.querySelector("input[name='v1']:checked").value,
        {
            title: {
                text: 'I(t) & V(t)'
            },
            xaxis: {
                title: 'Time(hh:mm:ss)'
            },
            yaxis: {
                title: 'I(pA)',
                side: 'left'
            },
            yaxis2: {
                title: 'V(kV)',
                overlaying: 'y',
                side: 'right'
            }
        });
    createChart('chart2', data, 'Timestamp (HH:MM:SS)', document.querySelector("input[name='q2']:checked").value,  document.querySelector("input[name='v2']:checked").value,
        {
            title: {
                text: 'N(t) & V(t)'
            },
            xaxis: {
                title: 'Time(hh:mm:ss)'
            },
            yaxis: {
                title: 'N(1/s)',
                side: 'left'
            },
            yaxis2: {
                title: 'V(kV)',
                overlaying: 'y',
                side: 'right'
            }
        });

    createChart('chart3',  data,'Timestamp (HH:MM:SS)', document.querySelector("input[name='q3']:checked").value,  document.querySelector("input[name='v3']:checked").value,
        {
            title: {
                text: 'I(t) & V(t)'
            },
            xaxis: {
                title: 'Time(hh:mm:ss)'
            },
            yaxis: {
                title: 'I(pA)',
                side: 'left'
            },
            yaxis2: {
                title: 'V(kV)',
                overlaying: 'y',
                side: 'right'
            }
        });

    createChart('chart4',data, document.querySelector("input[name='v4']:checked").value,  document.querySelector("input[name='q4']:checked").value, null,
        {
            title: {
                text: 'Q(V)'
            },
            xaxis: {
                title: 'V(kV)'
            },
            yaxis: {
                title: 'Q(pC)',
                side: 'left'
            }
        });
}



/*
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

/*
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
    return result;
}


/**
 *
 * @param txt파일을 읽어서 json 형식으로 변환하는 함수
 */
function txt2Json(data){
    if(data == null ) return;
    let result = {};

    return result;
}

const element = document.querySelector('#myFile');
const samplingRate = 1000;
// 샘플 데이터 읽어고기
// 데이터의 형태에 맞게 parsing함수 구현
//


function readFile(e, callback) {
    let file = e.files[0];
    if(!file) return;

    const reader = new FileReader();

    reader.onload = function (event) {
        const content = event.target.result;
        const extension = getFileExtension(file.name);
        let data = null;
        // 파일 확장자에 따라 처리 분기
        switch (extension.toLowerCase()) {
            case 'txt':
                //procTxt(content);
                data = txt2Json(content.split('\r\n'));
                break;
            case 'csv':
                //procCsv(content);
                data = csv2Json(content.split('\r\n'), 14);
                break;
            default:
                alert('처리되지 않은 파일 확장자 입니다.');
        }
        if(callback && typeof callback === "function"){
            callback(data);
        }


    };
    reader.onerror = function(err){
        alert("파일을 읽는중 오류가 발생했습니다.");
    };
    reader.readAsText(file);
}

/*
* @comment: 파일명에서 확장자 추출
* @param: filename
* @return: str
* */
function getFileExtension(fileName) {
    return fileName.split('.').pop();
}

/*
* @comment: txt 파일 읽기를 처리
* @params: txt 파일을 읽어들인 데이터
* @return: list
* */
function procTxt(content) {
    txt2Json(content.split('\r\n'));
}
/*
* @comment: csv 파일 읽기를 처리
* @param: 읽어들인 데이터
* @return: {'key':[], 'key2':[],....} 형태의 map
* */
function procCsv(content) {
    csv2Json(content.split('\r\n') ,14)
}

/*
* @comment: txt파일을 읽어들여 []형태의 데이터로 return
* @param: list
* @return: list
* */
function txt2Json(data){
    if(!data) return;
    let result = [];
    for(let n of data)
        result.push(parseDynamicType(n));
    return data;
}
