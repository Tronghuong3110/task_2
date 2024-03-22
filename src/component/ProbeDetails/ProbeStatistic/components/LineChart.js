import React,{memo} from 'react'
import { Line } from 'react-chartjs-2'
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);
function LineChart({ chartData,borderColor }) {
    return (
        <Line
            data={chartData}
            options={{
                color: "white",
                borderColor:borderColor,
                scales: {
                    x: {
                        type:"linear",
                        grid: {
                            color: "#FFF"
                        },
                        ticks: {
                            // beginAtZero: true,
                            color: 'white',
                            fontSize: 12,
                        }
                    },
                    y:{
                        type:"linear",
                        grid: {
                            color: "#FFF"
                        },
                        ticks: {
                            color: 'white',
                            fontSize: 12,
                            stepSize:25,
                            callback: function(value){
                                
                                return value.toFixed(2)+"%"
                            }
                        },
                    },

                }
            }}
            style={{height:"100%",width:"100%"}}
        ></Line>
    )
}
export default memo(LineChart)