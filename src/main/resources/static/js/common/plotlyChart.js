function PlotlyChart(){
    this.canvas = null;
    this.type = null;
    this.data = {
        x: [], //x 값
        y: [], // y 값
        z: [], // z f값
        option:{} // 옵션
    };

}


PlotlyChart.prototype.setCanvas = function(canvas){
    this.canvas = canvas;
}
PlotlyChart.prototype.setType = function(type){
    this.type = type;
}

PlotlyChart.prototype.setX= function(x){
    this.data.x = x;
}

PlotlyChart.prototype.setY = function(y){
    this.data.y = y;
}

PlotlyChart.prototype.setZ = function(z){
    this.data.z = z;
}
//부모 생성자 함수의 메서드
PlotlyChart.prototype.setOption = function(option){
    this.data.option = option;
}

PlotlyChart.prototype.drawChart = function(){
    let dataset = [];

    // IE에서 promise 안 되는 걸 대비하여 try_catch로 바꿔줌
    try{
        switch (this.type){
            case "line":
                dataset = lineChartData(this.data);
                break;
            case "heatMap":
                this.data.colorscale = 'Jet';
                this.data.type='heatmap';
                dataset.push(this.data);
                break;

        }
        // Plotly.newPlot 호출 전에 차트 영역을 비워줌
       document.getElementById(this.canvas).innerHTML = '';
       Plotly.newPlot(this.canvas, dataset, this.data.option, { editable: true });
    }catch (error){
        console.log("Error creating chart :"+error);
    }
}

function lineChartData(data){
    let dataset = [];

    for(let i=0; i<data.y.length; i++){
        let trace = {
            x: data.x.value,
            y: data.y[i].value,
            type: data.type || 'scatter',
            name: data.y[i].name,
            yaxis: 'y' + (i === 0 ? '' : (i + 1))
        }
        dataset.push(trace);
    }

    return dataset;
}