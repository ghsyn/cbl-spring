let grid = null;

$(document).ready(function(){
    initComponents();
    initParams();
    initDatas();

});


function initComponents(){
    document.getElementById("commandDate").value=new Date().format("yyyy-MM-dd");
}
function initParams(){}
function initDatas(){}

function shiftDate(v) {
    let date = new Date($("#commandDate").val());

    if (v === '<') {
        date.setDate(date.getDate() - 1);
    }
    if (v === '>') {
        date.setDate(date.getDate() + 1);
    }

    document.getElementById("commandDate").valueAsDate = date;

    if (date.format("yyyy-MM-dd") >= new Date().format("yyyy-MM-dd")) {
        // $("#afterbtn").attr('disabled','disabled');
        document.getElementById('afterbtn').disabled = true;
    } else if (date.format("yyyy-MM-dd") < new Date().format("yyyy-MM-dd")) {
        document.getElementById('afterbtn').disabled = false;
    }
}

// function getCBL(name, url, httpMethod, param) {
//     return new Promise(function (resolve, reject) {
//         $.ajax({
//             url: url,
//             contentType: 'application/json; charset=utf-8',
//             type: (httpMethod === null) ? "GET" : httpMethod,
//             data: JSON.stringify(param),
//             dataType: "json",
//             success: function (response) {
//                 resolve(response);
//             },
//             error: function (XMLHttpRequest, textStatus, errorThrown) {
//                 alert("get data error");
//                 reject();
//             }
//         })
//     }).then(function (data) {
//         drawChart(name, data['chart']);
//         drawGrid(name, data['grid']);
//     });
// }

function getCBL(name, url, httpMethod, payload) {
    let req = request(url, httpMethod, payload);
    req.then(function (data) {
        if (!Object.keys(data).length) {
            alert("Unsaved");
        } else {
            drawChart(name, data['chart']);
            drawGrid(name, data['grid']);
        }
    }).catch(function (err) {
        console.log(err);
    });
}

function request(url, httpMethod, payload) {
    return new Promise(function (resolve, reject) {
        let xhr = new XMLHttpRequest();
        xhr.open(httpMethod || 'GET', url);
        xhr.setRequestHeader('Content-Type', 'application/json;charset=utf-8');
        xhr.onload = function () {
            if (xhr.status === 200) {
                let responseData;
                try {
                    responseData = JSON.parse(xhr.responseText);
                } catch (error) {
                    reject(error);
                    return;
                }
                resolve(responseData);
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.onerror = function () {
            reject(xhr.statusText);
        };
        xhr.send(JSON.stringify(payload));
    })
}

function drawChart(name, val) {

    let mid610 = {
        x: val['CBL_TIME'],
        y: val['MID610'],
        mode: 'lines+markers',
        name: 'MID610'
    };

    let mid46 = {
        x: val['CBL_TIME'],
        y: val['MID46'],
        mode: 'lines+markers',
        name: 'MID46'
    };

    let mid810 = {
        x: val['CBL_TIME'],
        y: val['MID810'],
        mode: 'lines+markers',
        name: 'MID810'
    };

    let data = [mid610, mid46, mid810];

    let layout = {
        title: name,
        xaxis: {
            title: 'command datetime'
        },
        yaxis: {
            title: 'CBL value'
        }
    };

    Plotly.newPlot('cblChart', data, layout);
}

function drawGrid(id, data) {
    if (grid == null) {
        grid = new tui.Grid({
            el: document.getElementById('cblGrid'),
            columns: [
                {header: 'Datetime', name: 'CBL_TIME'},
                {header: 'MID610', name: 'MID610'},
                {header: 'MID46', name: 'MID46'},
                {header: 'MID810', name: 'MID810'},
            ]
        });
    }
    grid.resetData(data);
}