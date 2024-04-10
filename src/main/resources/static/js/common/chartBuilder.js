function ChartBuilder(){
    this.chart = new PlotlyChart();
}

ChartBuilder.prototype.setCanvas = function(data){
    this.chart.setCanvas(data)
    return this;
}

ChartBuilder.prototype.setType = function(data){
    this.chart.setType(data)
    return this;
}


ChartBuilder.prototype.setOption = function(data){
    this.chart.setOption(data);
    return this;
}

ChartBuilder.prototype.setX = function(data){
    this.chart.setX(data);
    return this;
}

ChartBuilder.prototype.setY = function(data){
    this.chart.setY(data);
    return this;
}

ChartBuilder.prototype.setZ = function(data){
    this.chart.setZ(data);
    return this;
}

ChartBuilder.prototype.setOption = function (data){
    this.chart.setOption(data);
    return this;
}

ChartBuilder.prototype.build = function(){
    return this.chart;
}




