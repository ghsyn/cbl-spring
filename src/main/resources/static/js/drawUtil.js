let chartType = {
    'plotly':Plotly, 'chartJs':chartJs, 'd3':d3
};

// chart 생성을 위한 object
function Chart(){
    this.data = {
        x:[],  // xdata
        y1:[], // y1 data
        y2:[], // y2 data
        layout:{} // layout
    };
}
// setX data
Chart.prototype.setX = function (data){
    this.data.x = data;
};
// setY1 data
Chart.prototype.setY1 = function (data){
    this.data.y1 = data;
};
// setY2 data
Chart.prototype.setY2 = function(data){
    this.data.y2 = data;
};
// setYLayout data
Chart.prototype.setLayout = function(layout){
    this.data.layout = layout;
};
// chart 생성
Chart.prototype.show = function (){
    //todo
    console.log("chart를 그리자 . . .");

    const trace1 = {
        x: this.data.x,
        y: this.data.y1,
        type: 'scatter',
        name: 'Q(pC)',
        yaxis: 'y1' // 왼쪽 y축에 연결
    };

    const trace2 = {
        x: this.data.x,
        y: d3,
        type: 'scatter',
        name: 'V(kV)',
        yaxis: 'y2' // 오른쪽 y축에 연결
    };

    const data = [trace1, trace2];
    // 차트 생성
    Plotly.newPlot('tmp1', data, this.data.layout);
};

/*
* @comment: 차트 유형에 따라 chart를 생성하기 위한 함수
* @param: chart Type str
* @return: void
* */
Chart.prototype.show = function(type){
    switch (type){
        case chartType.chartJs:
            break;
        case chartType.d3:
            break;
        case chartType.plotly:
            break;

    }
}

// Chart Object 의 빌더패턴 구현체
function ChartBuilder(){
    this.chart = new Chart();
}
ChartBuilder.prototype.withX = function(data){
    this.chart.setX(data);
    return this;
};
ChartBuilder.prototype.withY1 = function (data){
    this.chart.setY1(data);
    return this;
};
ChartBuilder.prototype.withY2 = function(data){
    this.chart.setY2(data);
    return this;
};
ChartBuilder.prototype.withLayout = function (layout){
    this.chart.setLayout(layout);
    return this;
};
ChartBuilder.prototype.build = function(){
    return this.chart;
};

