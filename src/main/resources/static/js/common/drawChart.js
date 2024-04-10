/**
 * 차트 관련 라이브러리
 */
let csvData = null;
function initChart(data) {
    csvData = data;

    drawChart("chart1", "line", setParams(1), setLayout(1));
    drawChart("chart2", "line", setParams(2), setLayout(2));
    drawChart("chart3", "line", setParams(3), setLayout(3));
    drawChart("chart4", "line", setParams(4), setLayout(4));


}

function drawChart(canvasId, type, data, option){
    console.log(data.x);
    let builder = new ChartBuilder();
    let chart = builder
        .setCanvas(canvasId)
        .setType(type)
        .setX(data.x)
        .setY(data.y)
        .setZ(data.z)
        .setOption(option)
        .build();
    chart.drawChart();

}

function setLayout(tabNo){
    let layout = {
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
    };

    switch (tabNo){
        case "2":
            layout.yaxis.title = 'N(1/s)';
            break;
        case "3":
            layout.yaxis.title = 'I(pA)';
            break;
        case "4":
            layout.yaxis.title = 'Q(pC)';
            layout.xaxis.title = 'V(kV)';
            delete layout['yaxis2']
            break;
    }
    return layout;
}
function setParams(tabNo){
    let subTab = document.getElementById("subTab"+tabNo);
    let y = [], z= [] ;
    let xKey = tabNo !== 4?'Timestamp (HH:MM:SS)' : document.querySelector("input[name='v4']:checked").value;
    let x = {
        'name': xKey,
        'value': csvData[xKey]
    };

    if(subTab){
        let setting = subTab.querySelectorAll("input[type='radio'][data-name*='yaxis']:checked");

        for(let i=0; i<setting.length; i++){
            let params = {};
            let keys =setting[i].value;
            params['value'] =csvData[setting[i].value];
            params['name'] = keys;
            y.push(params);
        }

    }
    return {x:x, y:y, z:z};
}

function drawHeatMap(){
    let data = {};
    let request = new XMLHttpRequest();
    request.open('GET', '/heatMap');
    request.send();
    request.onload = function(){
        data = JSON.parse(request.response);
        let phases = data['degList'];
        let pC_values = data['calcPulse'];
        // Create 2D histogram
        let heatmapData = {
            x: data['degList'],
            y: data['calcPulse'],
            z: data['originPulse']
        };

        let layout = {
            xaxis: { title: 'Phase Degree' },
            yaxis: { title: 'pC' },
            title: 'Heatmap Analysis',
            colorbar: { title: 'Counts' }
        };

        console.log(":: heatMap : "+JSON.stringify(heatmapData));

        drawChart("heatMap", 'heatMap', heatmapData, layout);
    }
}